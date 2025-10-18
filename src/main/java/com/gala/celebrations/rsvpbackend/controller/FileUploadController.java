package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.service.GcsSignedUrlService;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Autowired
    private GcsSignedUrlService gcsSignedUrlService;

    @Value("${gcs.bucket-name}")
    private String bucketName;

    /**
     * Generate a signed URL for single file upload.
     * Frontend will use this URL to upload directly to GCS.
     */
    @PostMapping("/generate-upload-url")
    public ResponseEntity<Map<String, String>> generateUploadUrl(@RequestBody Map<String, String> request) {
        try {
            String fileName = request.get("fileName");
            String contentType = request.get("contentType");
            String event = request.get("event");

            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "fileName is required"));
            }
            if (contentType == null || contentType.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "contentType is required"));
            }
            if (event == null || event.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "event is required"));
            }

            String username = getUsernameFromAuth();
            
            // Sanitize the filename to prevent path traversal and other attacks
            String sanitizedFilename = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String blobPath = event + "/" + username + "/" + sanitizedFilename;

            // Generate signed URL for upload
            String signedUrl = gcsSignedUrlService.generateSignedUploadUrl(blobPath, contentType);

            logger.info("Generated signed upload URL for: {}", blobPath);

            Map<String, String> response = new HashMap<>();
            response.put("signedUrl", signedUrl);
            response.put("blobPath", blobPath);
            response.put("fileName", sanitizedFilename);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate signed upload URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate signed upload URL: " + e.getMessage()));
        }
    }

    /**
     * Generate signed URLs for multiple file uploads.
     * Frontend will use these URLs to upload directly to GCS.
     */
    @PostMapping("/generate-multi-upload-urls")
    public ResponseEntity<Map<String, Object>> generateMultiUploadUrls(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> files = (List<Map<String, String>>) request.get("files");
            String event = (String) request.get("event");

            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "files array is required"));
            }
            if (event == null || event.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "event is required"));
            }

            String username = getUsernameFromAuth();
            List<Map<String, String>> uploadUrls = new ArrayList<>();

            for (Map<String, String> fileInfo : files) {
                String fileName = fileInfo.get("fileName");
                String contentType = fileInfo.get("contentType");

                if (fileName == null || fileName.isEmpty() || contentType == null || contentType.isEmpty()) {
                    logger.warn("Skipping file with missing fileName or contentType");
                    continue;
                }

                // Sanitize the filename
                String sanitizedFilename = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                String blobPath = event + "/" + username + "/" + sanitizedFilename;

                // Generate signed URL
                String signedUrl = gcsSignedUrlService.generateSignedUploadUrl(blobPath, contentType);

                Map<String, String> urlInfo = new HashMap<>();
                urlInfo.put("fileName", sanitizedFilename);
                urlInfo.put("signedUrl", signedUrl);
                urlInfo.put("blobPath", blobPath);

                uploadUrls.add(urlInfo);
            }

            logger.info("Generated {} signed upload URLs", uploadUrls.size());

            Map<String, Object> response = new HashMap<>();
            response.put("uploadUrls", uploadUrls);
            response.put("count", uploadUrls.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate signed upload URLs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate signed upload URLs: " + e.getMessage()));
        }
    }

    /**
     * List images from GCS with signed URLs for secure access.
     * Returns time-limited signed URLs instead of public URLs.
     */
    @GetMapping("/list-images")
    public ResponseEntity<Map<String, Object>> listImages(
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String user) {
        try {
            List<Storage.BlobListOption> options = new ArrayList<>();
            String prefix = "";

            if (eventName != null && !eventName.isBlank()) {
                prefix += eventName + "/";
                if (user != null && !user.isBlank()) {
                    prefix += user + "/";
                }
            }

            if (!prefix.isEmpty()) {
                logger.info("Listing images with prefix: {}", prefix);
                options.add(Storage.BlobListOption.prefix(prefix));
            } else {
                logger.info("Listing all images in the bucket.");
            }

            List<String> imageUrls = new ArrayList<>();
            Page<Blob> blobs = storage.list(bucketName, options.toArray(new Storage.BlobListOption[0]));

            for (Blob blob : blobs.iterateAll()) {
                if (!blob.isDirectory()) { // Exclude directories
                    // Generate signed URL for each image (valid for 1 hour)
                    String signedUrl = gcsSignedUrlService.generateSignedReadUrl(blob.getName());
                    imageUrls.add(signedUrl);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("images", imageUrls);
            response.put("count", imageUrls.size());

            logger.info("Listed {} images with signed URLs", imageUrls.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to list images from GCS", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to list images from GCS: " + e.getMessage()));
        }
    }

    private String getUsernameFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            logger.debug("JWT Claims: {}", jwt.getClaims());
            String username = jwt.getClaimAsString("email");
            if (username == null) {
                username = jwt.getClaimAsString("sub");
            }
            return username != null ? username : "anonymous";
        }
        return "anonymous";
    }
}
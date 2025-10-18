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

    @PostMapping("/picture-upload")
    public ResponseEntity<Map<String, String>> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("event") String event) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        try {
            String username = getUsernameFromAuth();
            Map<String, String> uploadResult = uploadSingleFile(file, event, username);
            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            logger.error("Failed to upload file to GCS", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file to GCS"));
        }
    }

    @PostMapping("/multi-picture-upload")
    public ResponseEntity<Map<String, Object>> handleMultipleFileUpload(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("event") String event) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "No files uploaded"));
        }

        String username = getUsernameFromAuth();
        List<Map<String, String>> successfulUploads = new ArrayList<>();
        List<Map<String, String>> failedUploads = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    successfulUploads.add(uploadSingleFile(file, event, username));
                } catch (IOException e) {
                    String originalFilename = file.getOriginalFilename();
                    logger.error("Failed to upload a file in multi-upload batch: {}", originalFilename, e);
                    failedUploads.add(Map.of(
                            "fileName", originalFilename != null ? originalFilename : "unknown",
                            "error", e.getMessage()
                    ));
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch upload process completed.");
        response.put("successfulUploads", successfulUploads);
        response.put("failedUploads", failedUploads);

        return ResponseEntity.ok(response);
    }

    /**
     * Private helper to handle the logic for uploading one file to GCS.
     *
     * @return A map containing the blobPath and publicUrl.
     * @throws IOException if the upload fails.
     */
    private Map<String, String> uploadSingleFile(MultipartFile file, String event, String username) throws IOException {
        // Sanitize the filename to prevent path traversal and other attacks
        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = (originalFilename != null)
                ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
                : "file_upload";

        String blobPath = event + "/" + username + "/" + sanitizedFilename;

        BlobId blobId = BlobId.of(bucketName, blobPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getBytes());

        String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, blobPath);

        return Map.of(
                "blobPath", blobPath,
                "publicUrl", publicUrl
        );
    }

    // ... listImages and getUsernameFromAuth methods remain the same
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
                    String publicUrl = String.format("https://storage.googleapis.com/%s/%s",
                            bucketName, blob.getName());
                    imageUrls.add(publicUrl);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("images", imageUrls);
            response.put("count", imageUrls.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to list images from GCS", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to list images from GCS"));
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
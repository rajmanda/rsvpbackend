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
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

            String sanitizedFilename = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String blobPath = event + "/" + username + "/" + sanitizedFilename;
            String gcsUri = String.format("gs://%s/%s", bucketName, blobPath);

            String signedUrl = gcsSignedUrlService.generateSignedUploadUrl(blobPath, contentType);

            logger.info("Generated signed URL for upload to: {}", gcsUri);

            Map<String, String> response = new HashMap<>();
            response.put("signedUrl", signedUrl);
            // Return the full GCS URI so it can be saved in the database
            response.put("blobPath", gcsUri);
            response.put("fileName", sanitizedFilename);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate signed upload URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate signed upload URL: " + e.getMessage()));
        }
    }

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

                String sanitizedFilename = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                String blobPath = event + "/" + username + "/" + sanitizedFilename;
                String gcsUri = String.format("gs://%s/%s", bucketName, blobPath);

                String signedUrl = gcsSignedUrlService.generateSignedUploadUrl(blobPath, contentType);

                logger.info("Generated signed URL for upload to: {}", gcsUri);

                Map<String, String> urlInfo = new HashMap<>();
                urlInfo.put("fileName", sanitizedFilename);
                urlInfo.put("signedUrl", signedUrl);
                // Return the full GCS URI
                urlInfo.put("blobPath", gcsUri);

                uploadUrls.add(urlInfo);
            }

            logger.info("Generated a total of {} signed upload URLs for event '{}' and user '{}'", uploadUrls.size(), event, username);

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

    @PostMapping("/generate-download-url")
    public ResponseEntity<Map<String, String>> generateDownloadUrl(@RequestBody Map<String, String> request) {
        try {
            String fullPath = request.get("blobPath");

            if (fullPath == null || fullPath.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "blobPath is required"));
            }

            // Parse the full GCS URI to get the relative path for the service
            String relativeBlobPath = parseGcsUri(fullPath);
            if (relativeBlobPath == null) {
                logger.warn("Invalid GCS URI format provided: {}", fullPath);
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid blobPath format. Expected gs://<bucket>/<path>"));
            }

            BlobId blobId = BlobId.of(bucketName, relativeBlobPath);
            Blob blob = storage.get(blobId);

            if (blob == null || !blob.exists()) {
                logger.warn("Requested blob does not exist: {}", relativeBlobPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "File not found: " + relativeBlobPath));
            }

            String signedUrl = gcsSignedUrlService.generateSignedReadUrl(relativeBlobPath);

            logger.info("Generated signed download URL for blob: {}", relativeBlobPath);

            Map<String, String> response = new HashMap<>();
            response.put("signedUrl", signedUrl);
            response.put("blobPath", fullPath); // Return the original full path

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate signed download URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate signed download URL: " + e.getMessage()));
        }
    }

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
                if (!blob.isDirectory()) {
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

    /**
     * Parses a GCS URI (gs://bucket/path) to extract the relative object path.
     * Also handles plain relative paths for backward compatibility.
     * @param path The full GCS URI or a relative path.
     * @return The relative object path, or null if the format is invalid.
     */
    private String parseGcsUri(String path) {
        String prefix = String.format("gs://%s/", this.bucketName);
        if (path != null && path.startsWith(prefix)) {
            return path.substring(prefix.length());
        }
        // Handle the old relative path format for backward compatibility
        if (path != null && !path.startsWith("gs://")) {
            return path;
        }
        // Return null if the format is invalid (e.g., gs://wrong-bucket/...)
        return null;
    }

    private String getUsernameFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication object from SecurityContext: {}", authentication);
        if (authentication == null) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            logger.debug("Extracting username from JWT principal.");
            username = jwt.getClaimAsString("email");
            if (username == null) {
                username = jwt.getClaimAsString("sub");
            }
        } else if (principal instanceof OidcUser oidcUser) {
            logger.debug("Extracting username from OidcUser principal.");
            username = oidcUser.getEmail();
            if (username == null) {
                username = oidcUser.getSubject();
            }
        } else if (principal instanceof OAuth2User oauth2User) {
            logger.debug("Extracting username from generic OAuth2User principal.");
            username = oauth2User.getAttribute("email");
            if (username == null) {
                username = oauth2User.getName();
            }
        }

        if (username == null) {
            logger.warn("Could not determine username from principal of type: {}. Principal details: {}", principal.getClass().getName(), principal);
            return "anonymous";
        }

        return username;
    }
}
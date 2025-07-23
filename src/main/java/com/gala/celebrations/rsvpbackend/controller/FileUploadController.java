package com.gala.celebrations.rsvpbackend.controller;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Value("${gcs.bucket-name}")
    private String bucketName;

    @PostMapping("/picture-upload")
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        // Get the username from the JWT token
        String username = getUsernameFromAuth();

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String fileName = username + "_" + file.getOriginalFilename(); // Prefix username to the filename
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            storage.create(blobInfo, file.getBytes());

            String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

            return ResponseEntity.ok(Map.of(
                    "message", "File uploaded successfully to GCS",
                    "fileName", fileName,
                    "publicUrl", publicUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file to GCS"));
        }
    }

    @GetMapping("/list-images")
    public ResponseEntity<Map<String, Object>> listImages() {
        try {
            List<String> imageUrls = new ArrayList<>();
            Page<Blob> blobs = storage.list(bucketName,
                    Storage.BlobListOption.currentDirectory()
                    //,Storage.BlobListOption.prefix("images/")
                    ); // Optional: filter by prefix

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to list images from GCS"));
        }
    }

    @PostMapping("/multi-picture-upload")
    public ResponseEntity<Map<String, Object>> handleMultipleFileUpload(@RequestParam("files") MultipartFile[] files) {
        String username = getUsernameFromAuth();

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "No files uploaded"));
        }

        List<String> uploadedFiles = new ArrayList<>();
        List<String> publicUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = username + "_" + file.getOriginalFilename(); // Prefix username to filename
                    BlobId blobId = BlobId.of(bucketName, fileName);
                    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                            .setContentType(file.getContentType())
                            .build();
                    storage.create(blobInfo, file.getBytes());

                    String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
                    uploadedFiles.add(fileName);
                    publicUrls.add(publicUrl);
                } catch (IOException e) {
                    // Continue processing the rest, but collect partial success
                    uploadedFiles.add("Failed: " + file.getOriginalFilename());
                    publicUrls.add("Error uploading");
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Processed files");
        response.put("uploadedFiles", uploadedFiles);
        response.put("publicUrls", publicUrls);
        response.put("count", uploadedFiles.size());

        return ResponseEntity.ok(response);
    }

    // Helper method to get the username from the JWT token
    private String getUsernameFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();

            // Log the entire JWT to check the claims
            System.out.println("JWT Claims: " + jwt.getClaims());

            // Use the appropriate claim (e.g., preferred_username, sub, email)
            String username = jwt.getClaimAsString("preferred_username"); // This may vary based on your token's structure
            if (username == null) {
                // Fallback to "sub" or "email" if preferred_username is not available
                username = jwt.getClaimAsString("sub");
            }
            if (username == null) {
                username = jwt.getClaimAsString("email"); // or fallback to email if neither "preferred_username" nor "sub" exists
            }

            return username != null ? username : "anonymous"; // Fallback to "anonymous" if no username is found
        }
        return "anonymous"; // Fallback if no authentication found
    }

}
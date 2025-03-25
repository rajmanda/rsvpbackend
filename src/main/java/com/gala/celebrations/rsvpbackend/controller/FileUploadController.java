package com.gala.celebrations.rsvpbackend.controller;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String fileName = file.getOriginalFilename();
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
}
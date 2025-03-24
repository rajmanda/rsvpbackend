package com.gala.celebrations.rsvpbackend.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    
    @Value("${gcs.bucket-name}")
    private String bucketName;

    @PostMapping("/picture-upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Get the file name
            String fileName = file.getOriginalFilename();

            // Create a BlobId
            BlobId blobId = BlobId.of(bucketName, fileName);

            // Create a BlobInfo
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

            // Upload the file to GCS
            storage.create(blobInfo, file.getBytes());

            return ResponseEntity.ok("File uploaded successfully to GCS: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file to GCS");
        }
    }
}
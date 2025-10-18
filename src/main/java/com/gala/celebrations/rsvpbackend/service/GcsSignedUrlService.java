package com.gala.celebrations.rsvpbackend.service;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class GcsSignedUrlService {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Value("${gcs.bucket-name}")
    private String bucketName;

    /**
     * Generates a signed URL for direct upload to GCS.
     * @param objectName path of the object in the bucket
     * @param contentType MIME type of the file
     * @return A time-limited signed URL (HTTP PUT)
     */
    public String generateSignedUploadUrl(String objectName, String contentType) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        return storage.signUrl(
                blobInfo,
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature()
        ).toString();
    }

    /**
     * Generates a signed URL for reading/downloading from GCS.
     * @param objectName path of the object in the bucket
     * @return A time-limited signed URL (HTTP GET) valid for 1 hour
     */
    public String generateSignedReadUrl(String objectName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();

        return storage.signUrl(
                blobInfo,
                1, TimeUnit.HOURS,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        ).toString();
    }
}


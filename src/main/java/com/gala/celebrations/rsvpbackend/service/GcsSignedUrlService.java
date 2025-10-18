package com.gala.celebrations.rsvpbackend.service;

import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.*;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GcsSignedUrlService {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final ServiceAccountSigner signer;
    private final String bucketName;

    @Autowired
    public GcsSignedUrlService(@Value("${gcs.bucket-name}") String bucketName,
                               @Value("${gcs.signer-sa-email}") String signerEmail) throws IOException {
        this.bucketName = bucketName;
        
        if (signerEmail == null || signerEmail.isBlank()) {
            throw new IllegalArgumentException("GCS signer service account email (gcs.signer-sa-email) must be configured for signing URLs.");
        }

        // Use Application Default Credentials to get the identity of the principal running the application.
        GoogleCredentials sourceCredentials = GoogleCredentials.getApplicationDefault();

        // Create impersonated credentials. This object will act as a signer, using the IAM API
        // to request signatures from the target service account. The principal (from sourceCredentials)
        // must have the "Service Account Token Creator" role on the target service account (signerEmail).
        this.signer = ImpersonatedCredentials.newBuilder()
                .setSourceCredentials(sourceCredentials)
                .setTargetPrincipal(signerEmail)
                .setScopes(ImmutableList.of("https://www.googleapis.com/auth/cloud-platform"))
                .build();
    }

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

        // Use the signer to create the V4 signature via the IAM API.
        return storage.signUrl(
                blobInfo,
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.signWith(this.signer)
        ).toString();
    }

    /**
     * Generates a signed URL for reading/downloading from GCS.
     * @param objectName path of the object in the bucket
     * @return A time-limited signed URL (HTTP GET) valid for 1 hour
     */
    public String generateSignedReadUrl(String objectName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();

        // Use the same signer for read URLs.
        return storage.signUrl(
                blobInfo,
                1, TimeUnit.HOURS,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.signWith(this.signer)
        ).toString();
    }
}

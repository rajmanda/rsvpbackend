# API Contracts - FileUploadController with GCS Signed URLs

## Base URL
```
{API_BASE_URL}/upload
```

---

## 1. Generate Single Upload URL

### Endpoint
```
POST /upload/generate-upload-url
```

### Request Headers
```
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
```

### Request Body
```json
{
  "fileName": "my-photo.jpg",
  "contentType": "image/jpeg",
  "event": "wedding-2024"
}
```

### Response (Success - 200 OK)
```json
{
  "signedUrl": "https://storage.googleapis.com/your-bucket/wedding-2024/user@example.com/my-photo.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=...",
  "blobPath": "wedding-2024/user@example.com/my-photo.jpg",
  "fileName": "my-photo.jpg"
}
```

### Response (Error - 400 Bad Request)
```json
{
  "error": "fileName is required"
}
```

### Response (Error - 500 Internal Server Error)
```json
{
  "error": "Failed to generate signed upload URL: {error_details}"
}
```

### Usage Flow
1. Frontend calls this endpoint with file metadata
2. Backend returns a signed URL valid for 15 minutes
3. Frontend uploads file directly to GCS using:
   ```
   PUT {signedUrl}
   Content-Type: {contentType}
   Body: {file_binary}
   ```

---

## 2. Generate Multiple Upload URLs

### Endpoint
```
POST /upload/generate-multi-upload-urls
```

### Request Headers
```
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
```

### Request Body
```json
{
  "files": [
    {
      "fileName": "photo1.jpg",
      "contentType": "image/jpeg"
    },
    {
      "fileName": "photo2.png",
      "contentType": "image/png"
    },
    {
      "fileName": "video1.mp4",
      "contentType": "video/mp4"
    }
  ],
  "event": "wedding-2024"
}
```

### Response (Success - 200 OK)
```json
{
  "uploadUrls": [
    {
      "fileName": "photo1.jpg",
      "signedUrl": "https://storage.googleapis.com/your-bucket/wedding-2024/user@example.com/photo1.jpg?X-Goog-Algorithm=...",
      "blobPath": "wedding-2024/user@example.com/photo1.jpg"
    },
    {
      "fileName": "photo2.png",
      "signedUrl": "https://storage.googleapis.com/your-bucket/wedding-2024/user@example.com/photo2.png?X-Goog-Algorithm=...",
      "blobPath": "wedding-2024/user@example.com/photo2.png"
    },
    {
      "fileName": "video1.mp4",
      "signedUrl": "https://storage.googleapis.com/your-bucket/wedding-2024/user@example.com/video1.mp4?X-Goog-Algorithm=...",
      "blobPath": "wedding-2024/user@example.com/video1.mp4"
    }
  ],
  "count": 3
}
```

### Response (Error - 400 Bad Request)
```json
{
  "error": "files array is required"
}
```

### Usage Flow
1. Frontend calls this endpoint with array of file metadata
2. Backend returns signed URLs for each file (valid for 15 minutes)
3. Frontend uploads each file directly to GCS in parallel
4. Files with missing fileName or contentType are skipped (check server logs)

---

## 3. List Images

### Endpoint
```
GET /upload/list-images
```

### Request Headers
```
Authorization: Bearer {JWT_TOKEN}
```

### Query Parameters
- `eventName` (optional): Filter images by event
- `user` (optional): Filter images by user (requires eventName)

### Examples
```
GET /upload/list-images
GET /upload/list-images?eventName=wedding-2024
GET /upload/list-images?eventName=wedding-2024&user=john@example.com
```

### Response (Success - 200 OK)
```json
{
  "images": [
    "https://storage.googleapis.com/your-bucket/wedding-2024/user@example.com/photo1.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=...&X-Goog-Expires=3600",
    "https://storage.googleapis.com/your-bucket/wedding-2024/user@example.com/photo2.png?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=...&X-Goog-Expires=3600"
  ],
  "count": 2
}
```

### Response (Error - 500 Internal Server Error)
```json
{
  "error": "Failed to list images from GCS: {error_details}"
}
```

### Important Notes
- Returns **signed URLs** valid for 1 hour (not public URLs)
- URLs will expire after 1 hour and need to be refreshed
- Directories are automatically excluded from results

---

## Security & Authentication

### JWT Claims
The backend extracts username from JWT token:
1. First tries `email` claim
2. Falls back to `sub` claim
3. Uses "anonymous" if no valid claim found

### File Path Structure
```
{event}/{username}/{sanitized_filename}

Examples:
- wedding-2024/john@example.com/photo1.jpg
- birthday-party/jane.doe@example.com/video_1.mp4
```

### Filename Sanitization
- Special characters are replaced with underscores
- Allowed characters: `a-z A-Z 0-9 . _ -`
- Example: `my photo (1).jpg` → `my_photo__1_.jpg`

---

## Signed URL Details

### Upload URLs (PUT)
- **Expiration**: 15 minutes
- **HTTP Method**: PUT
- **Version**: V4 signature
- **Content-Type**: Must match the contentType specified in request

### Read URLs (GET)
- **Expiration**: 1 hour
- **HTTP Method**: GET
- **Version**: V4 signature
- **Usage**: Direct browser access, img src, video src, etc.

---

## Error Handling

### Backend Errors
- **400 Bad Request**: Missing or invalid parameters
- **500 Internal Server Error**: GCS communication failure, configuration issues

### GCS Upload Errors
When uploading to signed URL, possible errors:
- **403 Forbidden**: URL expired or invalid
- **400 Bad Request**: Content-Type mismatch
- **401 Unauthorized**: Invalid signature

### Frontend Error Handling Strategy
```typescript
try {
  // Step 1: Get signed URL from backend
  const urlResponse = await getSignedUrl(...);
  
  // Step 2: Upload to GCS
  const gcsResponse = await fetch(urlResponse.signedUrl, {
    method: 'PUT',
    body: file,
    headers: { 'Content-Type': file.type }
  });
  
  if (!gcsResponse.ok) {
    throw new Error(`GCS upload failed: ${gcsResponse.status}`);
  }
  
  // Success
} catch (error) {
  // Handle both backend and GCS errors
  console.error('Upload failed:', error);
}
```

---

## Testing with cURL

### 1. Generate Upload URL
```bash
curl -X POST "http://localhost:8080/upload/generate-upload-url" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "fileName": "test.jpg",
    "contentType": "image/jpeg",
    "event": "test-event"
  }'
```

### 2. Upload to GCS (using signed URL from step 1)
```bash
curl -X PUT "SIGNED_URL_FROM_STEP_1" \
  -H "Content-Type: image/jpeg" \
  --data-binary "@/path/to/test.jpg"
```

### 3. List Images
```bash
curl -X GET "http://localhost:8080/upload/list-images?eventName=test-event" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Migration Checklist

### Backend ✅
- [x] GcsSignedUrlService integrated into FileUploadController
- [x] Old endpoints replaced with new signed URL endpoints
- [x] List images returns signed URLs instead of public URLs
- [x] Proper error handling and logging
- [x] Filename sanitization maintained

### Frontend (Required Changes)
- [ ] Update `file-upload.service.ts` with new API calls
- [ ] Implement two-step upload process (get URL → upload to GCS)
- [ ] Update component to handle new response format
- [ ] Test single file upload
- [ ] Test multiple file upload
- [ ] Test image listing with signed URLs
- [ ] Add error handling for both steps
- [ ] Handle URL expiration scenarios

### Testing
- [ ] Unit tests for new endpoints
- [ ] Integration tests for signed URL generation
- [ ] End-to-end tests for complete upload flow
- [ ] Performance testing (direct uploads should be faster)
- [ ] Security testing (expired URLs, invalid signatures)

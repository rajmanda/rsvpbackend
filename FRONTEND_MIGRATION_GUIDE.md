# Frontend Migration Guide: GCS Signed URL Implementation

## Overview
The backend has been updated to use GCS Signed URLs for file uploads. This document outlines the required frontend changes.

## API Changes

### 1. Single File Upload (REPLACED)

**OLD Endpoint:** `POST /upload/picture-upload`
- Content-Type: `multipart/form-data`
- Body: file binary + event

**NEW Endpoint:** `POST /upload/generate-upload-url`
- Content-Type: `application/json`
- Body:
  ```json
  {
    "fileName": "example.jpg",
    "contentType": "image/jpeg",
    "event": "wedding-2024"
  }
  ```
- Response:
  ```json
  {
    "signedUrl": "https://storage.googleapis.com/...",
    "blobPath": "wedding-2024/user@example.com/example.jpg",
    "fileName": "example.jpg"
  }
  ```

### 2. Multiple Files Upload (REPLACED)

**OLD Endpoint:** `POST /upload/multi-picture-upload`
- Content-Type: `multipart/form-data`
- Body: files[] binary + event

**NEW Endpoint:** `POST /upload/generate-multi-upload-urls`
- Content-Type: `application/json`
- Body:
  ```json
  {
    "files": [
      {"fileName": "img1.jpg", "contentType": "image/jpeg"},
      {"fileName": "img2.png", "contentType": "image/png"}
    ],
    "event": "wedding-2024"
  }
  ```
- Response:
  ```json
  {
    "uploadUrls": [
      {
        "fileName": "img1.jpg",
        "signedUrl": "https://storage.googleapis.com/...",
        "blobPath": "wedding-2024/user@example.com/img1.jpg"
      },
      {
        "fileName": "img2.png",
        "signedUrl": "https://storage.googleapis.com/...",
        "blobPath": "wedding-2024/user@example.com/img2.png"
      }
    ],
    "count": 2
  }
  ```

### 3. List Images (UPDATED)

**Endpoint:** `GET /upload/list-images?eventName={event}&user={user}`
- No change in request format
- Response now contains **signed URLs** (time-limited, 1 hour expiry) instead of public URLs
- Response format remains the same:
  ```json
  {
    "images": [
      "https://storage.googleapis.com/...?X-Goog-Signature=...",
      "https://storage.googleapis.com/...?X-Goog-Signature=..."
    ],
    "count": 2
  }
  ```

---

## Required Frontend Changes

### Update `file-upload.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, from, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { catchError, switchMap, tap } from 'rxjs/operators';

export interface SignedUrlResponse {
  signedUrl: string;
  blobPath: string;
  fileName: string;
}

export interface MultiUploadUrlResponse {
  uploadUrls: Array<{
    fileName: string;
    signedUrl: string;
    blobPath: string;
  }>;
  count: number;
}

export interface UploadResult {
  fileName: string;
  blobPath: string;
  success: boolean;
  error?: string;
}

export interface ListImagesResponse {
  images: string[];
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private fileUploadApiUrl = environment.apiBaseUrl + '/upload';

  constructor(private http: HttpClient) {}

  /**
   * Upload a single file using GCS signed URL (2-step process)
   * Step 1: Get signed URL from backend
   * Step 2: Upload file directly to GCS
   */
  uploadFile(file: File, event: string): Observable<UploadResult> {
    // Step 1: Get signed URL from backend
    return this.http.post<SignedUrlResponse>(
      `${this.fileUploadApiUrl}/generate-upload-url`,
      {
        fileName: file.name,
        contentType: file.type,
        event: event
      }
    ).pipe(
      switchMap(response => {
        // Step 2: Upload file directly to GCS using signed URL
        const headers = new HttpHeaders({
          'Content-Type': file.type
        });

        return from(
          fetch(response.signedUrl, {
            method: 'PUT',
            body: file,
            headers: {
              'Content-Type': file.type
            }
          }).then(gcsResponse => {
            if (!gcsResponse.ok) {
              throw new Error(`GCS upload failed: ${gcsResponse.statusText}`);
            }
            return {
              fileName: response.fileName,
              blobPath: response.blobPath,
              success: true
            };
          })
        );
      }),
      catchError(error => {
        console.error('Upload failed:', error);
        return throwError(() => ({
          fileName: file.name,
          blobPath: '',
          success: false,
          error: error.message || 'Upload failed'
        }));
      })
    );
  }

  /**
   * Upload multiple files using GCS signed URLs (2-step process)
   */
  uploadMultipleFiles(files: File[], event: string): Observable<UploadResult[]> {
    const fileInfos = files.map(file => ({
      fileName: file.name,
      contentType: file.type
    }));

    // Step 1: Get signed URLs for all files
    return this.http.post<MultiUploadUrlResponse>(
      `${this.fileUploadApiUrl}/generate-multi-upload-urls`,
      {
        files: fileInfos,
        event: event
      }
    ).pipe(
      switchMap(response => {
        // Step 2: Upload all files to GCS in parallel
        const uploadPromises = response.uploadUrls.map((urlInfo, index) => {
          const file = files[index];
          return fetch(urlInfo.signedUrl, {
            method: 'PUT',
            body: file,
            headers: {
              'Content-Type': file.type
            }
          }).then(gcsResponse => {
            if (!gcsResponse.ok) {
              throw new Error(`GCS upload failed for ${urlInfo.fileName}`);
            }
            return {
              fileName: urlInfo.fileName,
              blobPath: urlInfo.blobPath,
              success: true
            };
          }).catch(error => ({
            fileName: urlInfo.fileName,
            blobPath: urlInfo.blobPath,
            success: false,
            error: error.message
          }));
        });

        return from(Promise.all(uploadPromises));
      }),
      catchError(error => {
        console.error('Multi-upload failed:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * List images - No change needed, backend now returns signed URLs
   */
  listImages(eventName?: string, user?: string): Observable<ListImagesResponse> {
    const params: any = {};
    if (eventName) {
      params.eventName = eventName;
    }
    if (user) {
      params.user = user;
    }

    console.log('[FileUploadService] listImages - Request parameters:', {
      eventName,
      user,
      finalParams: params
    });

    return this.http.get<ListImagesResponse>(
      `${this.fileUploadApiUrl}/list-images`,
      { params }
    ).pipe(
      tap(response => {
        console.log('[FileUploadService] listImages - Response received with',
                   response.images?.length || 0, 'images (signed URLs)');
      }),
      catchError(error => {
        console.error('[FileUploadService] Error listing images:', error);
        throw error;
      })
    );
  }
}
```

### Update `file-upload.component.ts`

```typescript
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FileUploadService, UploadResult } from '../services/file-upload/file-upload.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent {
  selectedFile: File | null = null;
  uploadProgress: number = 0;
  isUploading: boolean = false;
  uploadMessage: string = '';
  eventId: string = 'default-event';

  constructor(private fileUploadService: FileUploadService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    console.log('File input changed. Files:', input.files);

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadProgress = 0;
      this.uploadMessage = '';

      console.group('Selected File Details');
      console.log('Name:', this.selectedFile.name);
      console.log('Type:', this.selectedFile.type);
      console.log('Size (bytes):', this.selectedFile.size);
      console.groupEnd();
    }
  }

  onUpload(event: Event): void {
    event.preventDefault();

    if (!this.selectedFile) return;

    this.isUploading = true;
    this.uploadProgress = 50; // Show intermediate progress
    this.uploadMessage = 'Uploading to GCS...';

    this.fileUploadService.uploadFile(this.selectedFile, this.eventId).subscribe({
      next: (result: UploadResult) => {
        this.isUploading = false;
        this.uploadProgress = 100;
        
        if (result.success) {
          this.uploadMessage = 'Upload successful!';
          console.log('Upload result:', result);
        } else {
          this.uploadMessage = `Upload failed: ${result.error}`;
        }

        // Reset file input
        this.selectedFile = null;
        const fileInput = document.getElementById('fileInput') as HTMLInputElement;
        if (fileInput) fileInput.value = '';
      },
      error: (error) => {
        this.isUploading = false;
        this.uploadMessage = 'Upload failed. Please try again.';
        console.error('Upload error:', error);
      }
    });
  }
}
```

---

## Key Changes Summary

### Upload Flow
**Before:**
```
Client → Backend (with file) → GCS
```

**After:**
```
Client → Backend (get signed URL) → Client → GCS (direct upload)
```

### Benefits
1. **Reduced server load** - Backend only generates URLs, doesn't handle file data
2. **Faster uploads** - Direct client-to-GCS connection
3. **Better scalability** - No server bandwidth consumed for file transfers
4. **Security** - Time-limited signed URLs (15 min for upload, 1 hour for read)

### Important Notes
1. **No progress tracking** - The new implementation uses `fetch()` API which doesn't support progress events. If you need progress tracking, you'll need to implement a custom solution using XMLHttpRequest.
2. **CORS** - GCS automatically handles CORS for signed URLs, no additional configuration needed.
3. **Error Handling** - Make sure to handle both backend errors (URL generation) and GCS errors (upload).
4. **Timeout** - Signed URLs expire in 15 minutes. If upload takes longer, you'll need to regenerate the URL.

---

## Testing Checklist

- [ ] Single file upload works end-to-end
- [ ] Multiple file upload works end-to-end
- [ ] List images returns and displays signed URLs correctly
- [ ] Images remain accessible for the full signed URL duration (1 hour)
- [ ] Error handling works for both backend and GCS failures
- [ ] File name sanitization works correctly
- [ ] Large file uploads complete within 15-minute window
- [ ] Network interruptions are handled gracefully

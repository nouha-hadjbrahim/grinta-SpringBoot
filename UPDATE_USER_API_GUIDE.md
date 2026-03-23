# UPDATE USER API - Complete Documentation

## Overview
This guide documents the new UPDATE USER feature with image upload functionality.

---

## 📝 Changes Made

### 1. **Photo Storage Logic**
- ✅ Changed from BLOB/byte[] to String
- ✅ Stores only filename: `"user123.png"`
- ✅ Files saved in `/uploads` directory
- ✅ Example in DB: `"550e8400-e29b-41d4-a716-446655440000_1710859200000.png"`

### 2. **New Components Created**
- `UpdateUserRequest.java` - DTO for update requests
- `FileUploadService.java` - File upload/deletion logic
- `WebMvcConfig.java` - Static file serving configuration
- Updated `UserService.java` - New `updateUser()` method
- Updated `UserController.java` - New PUT endpoint
- Updated `SecurityConfig.java` - Allow /uploads access

### 3. **File Upload Features**
✅ Unique filename generation (UUID + timestamp)
✅ File validation (size 5MB max, only jpg/jpeg/png/gif)
✅ Automatic old photo deletion
✅ Clean error handling
✅ Production-ready code

---

## 🔌 API Endpoint

### **Update User with Image**

**Method:** `PUT`  
**Endpoint:** `/api/user/users/{id}`  
**Authentication:** Required (USER or ADMIN)

---

## 📨 Request Format

### Headers
```
Content-Type: multipart/form-data
Authorization: Bearer YOUR_JWT_TOKEN
```

### Body (Form Data)
```
Field              Type           Required    Description
─────────────────  ─────────────  ─────────   ──────────────────────
name               string         No         User's full name
email              string         No         User's email
phoneNumber        string         No         Phone number (6-15 chars)
role               string         No         ADMIN or USER
status             string         No         ACTIVE or INACTIVE
file OR image      file           No         JPG/JPEG/PNG/GIF (max 5MB)
                                             Either field name works!
```

**Note:** The endpoint accepts **both** `file` and `image` as field names for the image upload. Use whichever works with your frontend framework.

---

## 📋 Example Requests

### 1️⃣ Update Everything Including Image (Form Data)
```bash
# Using "image" field name
curl -X PUT http://localhost:8000/api/user/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "name=John Doe" \
  -F "email=john@example.com" \
  -F "phoneNumber=+1234567890" \
  -F "role=ADMIN" \
  -F "status=ACTIVE" \
  -F "image=@/path/to/photo.jpg"

# OR using "file" field name (both work!)
curl -X PUT http://localhost:8000/api/user/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "name=John Doe" \
  -F "email=john@example.com" \
  -F "phoneNumber=+1234567890" \
  -F "role=ADMIN" \
  -F "status=ACTIVE" \
  -F "file=@/path/to/photo.jpg"
```

### 2️⃣ Update Only Name and Email (No Image)
```bash
curl -X PUT http://localhost:8000/api/user/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "name=John Doe" \
  -F "email=john@example.com"
```

### 3️⃣ Update Only Image (Keep Other Fields)
```bash
curl -X PUT http://localhost:8000/api/user/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "image=@/path/to/new-photo.jpg"
```

### 4️⃣ Update Status
```bash
curl -X PUT http://localhost:8000/api/user/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "status=INACTIVE"
```

---

## ✅ Success Response (200 OK)

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "+1234567890",
  "password": "$2a$10$...",
  "role": "ADMIN",
  "status": "ACTIVE",
  "photo": "550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg"
}
```

---

## ❌ Error Responses

### 404 - User Not Found
```json
{
  "statusCode": 404,
  "message": "User not found with id: 999",
  "success": false
}
```

### 400 - Bad Request (Duplicate Email)
```json
{
  "statusCode": 400,
  "message": "Email is already in use",
  "success": false
}
```

### 400 - Invalid File
```json
{
  "statusCode": 400,
  "message": "File size exceeds maximum allowed size of 5 MB",
  "success": false
}
```

OR

```json
{
  "statusCode": 400,
  "message": "File type not allowed. Allowed types: jpg, jpeg, png, gif",
  "success": false
}
```

### 500 - Server Error
```json
{
  "statusCode": 500,
  "message": "File upload failed: ...",
  "success": false
}
```

---

## 🎨 Frontend Integration (Angular/React)

### Angular Example (HttpClient)
```typescript
updateUserWithImage(userId: number, formData: FormData): Observable<any> {
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${this.getToken()}`
  });

  // Note: Don't set Content-Type header - browser will set it automatically
  return this.http.put(
    `http://localhost:8000/api/user/users/${userId}`,
    formData,
    { headers }
  );
}

// Usage in component
const formData = new FormData();
formData.append('name', 'John Doe');
formData.append('email', 'john@example.com');
formData.append('phoneNumber', '+1234567890');

// Add file with field name "file" or "image" - both work!
formData.append('file', fileInputElement.files[0]); // Use "file"
// OR
// formData.append('image', fileInputElement.files[0]); // OR use "image"

this.updateUserWithImage(userId, formData).subscribe(
  response => {
    console.log('User updated:', response);
    console.log('Photo URL:', `http://localhost:8000/uploads/${response.photo}`);
  },
  error => console.error('Update failed:', error)
);
```

### React Example (Fetch API)
```javascript
async function updateUserWithImage(userId, userData, imageFile) {
  const formData = new FormData();
  
  Object.keys(userData).forEach(key => {
    formData.append(key, userData[key]);
  });

  // Append file with field name "file" or "image"
  if (imageFile) {
    formData.append('file', imageFile); // Use "file"
    // OR formData.append('image', imageFile); // OR use "image"
  }

  const response = await fetch(`http://localhost:8000/api/user/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
      // Don't set Content-Type - browser will set it
    },
    body: formData
  });

  return response.json();
}

// Usage
const data = {
  name: 'John Doe',
  email: 'john@example.com',
  phoneNumber: '+1234567890'
};

const image = document.querySelector('input[type="file"]').files[0];
const result = await updateUserWithImage(userId, data, image);
console.log('Photo saved as:', result.photo);
```

### Important Notes for Frontend:
✅ **Field name accepts both:**
  - `file` - This is what the frontend usually sends
  - `image` - Also supported

✅ **Don't set Content-Type header** - Let the browser set it automatically when using FormData

✅ **Use FormData** - Never use JSON for file uploads, always use FormData

✅ **Access uploaded image:**
  ```
  http://localhost:8000/uploads/{response.photo}
  ```

---

Once uploaded, images are accessible at:

```
http://localhost:8000/uploads/550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg
```

**Use in HTML:**
```html
<img src="http://localhost:8000/uploads/550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg" alt="User Photo">
```

**Use in Angular/TypeScript:**
```typescript
const photoUrl = `http://localhost:8000/uploads/${user.photo}`;
// Or set in template
<img [src]="'http://localhost:8000/uploads/' + user.photo" alt="User">
```

---

## 🛡️ File Upload Validation

### Allowed File Types
- `jpg` or `jpeg`
- `png`
- `gif`

### Size Limits
- Maximum: **5 MB**
- Checked before saving

### Filename Generation
```
Format: UUID_TIMESTAMP.extension
Example: 550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg
```

---

## 📁 File Storage Structure

```
project-root/
├── uploads/
│   ├── 550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg
│   ├── 6ba7b810-9dad-11d1-80b4-00c04fd430c8_1710859300000.png
│   └── ...
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/...
│   │   └── resources/
│   └── test/
├── pom.xml
└── ...
```

---

## 🔄 Service Methods Called

### FileUploadService
```java
// Save file and return filename
String fileName = fileUploadService.saveFile(MultipartFile file)

// Delete file
boolean deleted = fileUploadService.deleteFile(String fileName)
```

### UserService
```java
// Update user with optional image
User updatedUser = userService.updateUser(
    Long id,
    UpdateUserRequest request,
    MultipartFile image
)
```

---

## 🧪 Testing in Postman

### Setup
1. Create a PUT request
2. URL: `http://localhost:8000/api/user/users/1`
3. Headers tab:
   - `Authorization: Bearer YOUR_TOKEN`

### Body Configuration
1. Click **Body** tab
2. Select **form-data**
3. Add fields:
   - `name` (text) = "John Doe"
   - `email` (text) = "john@example.com"
   - `phoneNumber` (text) = "+1234567890"
   - `image` (file) = Select your image file

4. Click **Send**

---

## 🔒 Security Features

✅ JWT Authentication required  
✅ File type validation  
✅ File size validation  
✅ Unique filename generation to prevent conflicts  
✅ Auto cleanup of old photos  
✅ Email uniqueness check (except own email)  
✅ Role-based validation  

---

## ⚙️ Configuration

### application.properties
No additional configuration needed! The system:
- Creates `/uploads` directory automatically
- Serves files from `/uploads/**` without authentication
- Caches images for 1 hour

---

## � Troubleshooting

### Issue: Photo is null in response even after upload

**Cause 1: Wrong field name**
- Frontend sends: `image` → Backend expects: `image` ✓
- Frontend sends: `file` → Backend expects: `file` ✓
- Both now work! If still not working, check the exact field name in your frontend

**Solution:**
```javascript
// Make sure you're using FormData correctly
const formData = new FormData();
formData.append('file', fileInputElement.files[0]); // Correct
// NOT: formData.append(fileInputElement); // Wrong!
```

**Cause 2: File validation failed**
- File is empty (0 bytes)
- File type not allowed (must be jpg, jpeg, png, or gif)
- File exceeds 5MB limit

**Solution:**
```javascript
// Check file before uploading
const file = fileInputElement.files[0];
console.log('File:', file.name, file.size, file.type);

if (file.size === 0) {
  alert('File is empty');
}

const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
if (!allowedTypes.includes(file.type)) {
  alert('File type not allowed. Use jpg, png, or gif');
}

if (file.size > 5 * 1024 * 1024) {
  alert('File exceeds 5MB limit');
}
```

**Cause 3: FormData is not being sent correctly**
- Content-Type header is set manually (breaks multipart upload)
- No Authorization header
- Request body is stringified instead of FormData

**Solution:**
```typescript
// CORRECT way
const formData = new FormData();
formData.append('name', 'John');
formData.append('file', file);

const headers = new HttpHeaders({
  'Authorization': `Bearer ${token}`
  // DO NOT set Content-Type!
});

this.http.put('/api/user/users/1', formData, { headers });

// WRONG way
// const formData = new FormData();
// formData.append('name', 'John');
// formData.append('file', file);
// const headers = { 'Content-Type': 'multipart/form-data' }; // WRONG!
// this.http.put('/api/user/users/1', JSON.stringify(formData)); // WRONG!
```

### Issue: 404 Not Found

**Cause:** User ID doesn't exist

**Solution:** Make sure the user ID is valid
```bash
# First get all users to check their IDs
curl -X GET http://localhost:8000/api/user/users \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Issue: 405 Method Not Allowed

**Cause:** Old endpoint path was used (`/api/user/{id}` instead of `/api/user/users/{id}`)

**Solution:** Use the correct endpoint path
```bash
# WRONG: PUT http://localhost:8000/api/user/1
# CORRECT: PUT http://localhost:8000/api/user/users/1
```

### Issue: Unauthorized (401)

**Cause:** Invalid or missing JWT token

**Solution:** Make sure:
1. Token is in the Authorization header
2. Token format is: `Bearer YOUR_TOKEN`
3. Token hasn't expired

```typescript
// Check token
const token = localStorage.getItem('token');
console.log('Token:', token);

// In request
headers: {
  'Authorization': `Bearer ${token}` // Correct format
}
```

---

1. **Backward Compatibility**: Existing users without photos will have `null` photo field
2. **Automatic Cleanup**: Old photos are automatically deleted when updated
3. **Cascading Delete**: Photos are deleted when user is deleted
4. **No Password Change**: Password cannot be changed via this endpoint
5. **Optional Fields**: Only include fields you want to update
6. **Email Validation**: Cannot change email to one already in use elsewhere

---

## 🚀 Next Steps

1. Test the endpoint using the examples above
2. Integrate into Angular frontend
3. Display images with: `<img [src]="'http://localhost:8000/uploads/' + user.photo">`
4. Handle file upload in forms
5. Add proper error messages to UI


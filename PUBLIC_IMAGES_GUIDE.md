# Using Public Images from /uploads Folder

## ✅ Images are NOW PUBLIC (No Authentication Required!)

The `/uploads` folder is configured as **public** and can be accessed directly from the browser without any JWT token.

---

## 🖼️ How to Display User Photos in Frontend

### HTML (Pure HTML)
```html
<!-- Display user photo -->
<img src="http://localhost:8000/uploads/550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg" 
     alt="User Photo"
     width="200">
```

### Angular
```typescript
// Component TypeScript
export class UserProfileComponent {
  user: any;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.getUser(1).subscribe(user => {
      this.user = user;
      // this.user.photo will have the filename
    });
  }

  // Method to get image URL
  getPhotoUrl(): string {
    if (this.user?.photo) {
      return `http://localhost:8000/uploads/${this.user.photo}`;
    }
    return 'assets/default-avatar.png'; // fallback
  }
}
```

### Angular Template
```html
<!-- Display with property binding -->
<img [src]="getPhotoUrl()" 
     alt="User Photo"
     class="user-avatar">

<!-- OR directly in template -->
<img [src]="'http://localhost:8000/uploads/' + user?.photo" 
     alt="User Photo"
     onerror="this.src='assets/default-avatar.png'">
```

### React
```jsx
import { useState, useEffect } from 'react';

export function UserProfile() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch('http://localhost:8000/api/user/users/1', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    .then(r => r.json())
    .then(data => setUser(data));
  }, []);

  const imageUrl = user?.photo 
    ? `http://localhost:8000/uploads/${user.photo}`
    : 'default-avatar.png';

  return (
    <img 
      src={imageUrl} 
      alt="User Photo"
      onError={(e) => e.target.src = 'default-avatar.png'}
    />
  );
}
```

### Vue
```vue
<template>
  <div>
    <!-- Display user photo -->
    <img :src="photoUrl" alt="User Photo" class="user-avatar">
    
    <!-- With fallback -->
    <img 
      :src="photoUrl" 
      alt="User Photo"
      @error="fallbackImage"
      class="user-avatar">
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: null
    }
  },
  computed: {
    photoUrl() {
      return this.user?.photo 
        ? `http://localhost:8000/uploads/${this.user.photo}`
        : '/default-avatar.png';
    }
  },
  methods: {
    fallbackImage(event) {
      event.target.src = '/default-avatar.png';
    }
  },
  mounted() {
    this.loadUser();
  }
}
</script>
```

---

## 🔑 Important: Image URLs for Different Scenarios

### During Update (Response has filename)
```json
{
  "id": 1,
  "name": "John Doe",
  "photo": "550e8400-e29b-41d4-a716-446655440000_1710859200000.jpg"
}
```

**Use in frontend:**
```javascript
const imageUrl = `http://localhost:8000/uploads/${response.photo}`;
```

### Displaying in List of Users
```javascript
users.forEach(user => {
  if (user.photo) {
    const url = `http://localhost:8000/uploads/${user.photo}`;
    console.log(`User ${user.name} photo:`, url);
  }
});
```

### With Fallback Image
```html
<!-- If photo is null, show default -->
<img 
  [src]="user?.photo 
    ? ('http://localhost:8000/uploads/' + user.photo) 
    : 'assets/default-avatar.png'"
  alt="User Photo">
```

---

## 🔐 Security Notes

### ✅ Public Access (No Token Needed)
```javascript
// This works WITHOUT token
fetch('http://localhost:8000/uploads/photo.jpg')
  .then(r => r.blob())
  .then(blob => {
    const url = URL.createObjectURL(blob);
    img.src = url;
  });
```

### ❌ No Authorization Header Needed
```javascript
// WRONG - don't send token for images
fetch('http://localhost:8000/uploads/photo.jpg', {
  headers: { 'Authorization': 'Bearer token' } // Not needed!
})

// CORRECT - just fetch directly
fetch('http://localhost:8000/uploads/photo.jpg')
```

### ✅ Browser Can Load Directly
```html
<!-- Browser loads this directly without any backend auth -->
<img src="http://localhost:8000/uploads/photo.jpg">
```

---

## 🎯 Best Practices

### 1. Use Environment Variable for Base URL
```typescript
// environment.ts
export const environment = {
  apiUrl: 'http://localhost:8000',
  imageUrl: 'http://localhost:8000/uploads'
};

// In component
import { environment } from 'environments/environment';

getPhotoUrl() {
  return `${environment.imageUrl}/${this.user.photo}`;
}
```

### 2. Create Image URL Helper Service
```typescript
@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private baseUrl = 'http://localhost:8000/uploads';

  getImageUrl(filename: string | null): string {
    if (!filename) {
      return 'assets/default-avatar.png';
    }
    return `${this.baseUrl}/${filename}`;
  }
}

// Usage
<img [src]="imageService.getImageUrl(user.photo)">
```

### 3. Handle Missing Photos Gracefully
```html
<img 
  [src]="user?.photo ? getPhotoUrl() : 'assets/default-avatar.png'"
  alt="User Photo"
  onError="this.src='assets/default-avatar.png'"
  class="user-avatar">
```

### 4. Lazy Load Images
```html
<!-- Lazy loading for performance -->
<img 
  [src]="getPhotoUrl()"
  alt="User Photo"
  loading="lazy"
  width="200"
  height="200">
```

---

## 🔄 Update User with Image in Frontend

### Angular Example (Complete)
```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-user-update',
  template: `
    <form (ngSubmit)="updateUser()">
      <input type="text" [(ngModel)]="user.name" name="name">
      <input type="file" (change)="onFileSelected($event)" #fileInput>
      <button type="submit">Update</button>
    </form>
    
    <!-- Display updated photo -->
    <img *ngIf="user?.photo" 
         [src]="'http://localhost:8000/uploads/' + user.photo"
         alt="User Photo">
  `
})
export class UserUpdateComponent {
  user: any = {};
  selectedFile: File | null = null;

  constructor(
    private http: HttpClient,
    private fb: FormBuilder
  ) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] || null;
  }

  updateUser() {
    const formData = new FormData();
    formData.append('name', this.user.name);
    
    if (this.selectedFile) {
      formData.append('file', this.selectedFile); // ← Public endpoint doesn't need file
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put(
      'http://localhost:8000/api/user/users/1',
      formData,
      { headers }
    ).subscribe({
      next: (response: any) => {
        this.user = response;
        console.log('Photo URL:', `http://localhost:8000/uploads/${response.photo}`);
        // Now the image can be displayed publicly!
      },
      error: (err) => console.error('Update failed:', err)
    });
  }
}
```

---

## 🧪 Test Public Access

### Using Browser DevTools Console
```javascript
// Test 1: Fetch image without token
fetch('http://localhost:8000/uploads/your-filename.jpg')
  .then(r => {
    console.log('Status:', r.status);
    console.log('Type:', r.headers.get('content-type'));
    return r.blob();
  })
  .then(blob => {
    console.log('✓ Image loaded successfully!');
    console.log('Size:', blob.size, 'bytes');
  })
  .catch(err => console.error('✗ Failed:', err));

// Test 2: Display image in img tag
const img = document.createElement('img');
img.src = 'http://localhost:8000/uploads/your-filename.jpg';
img.onload = () => console.log('✓ Image loaded!');
img.onerror = () => console.log('✗ Image failed to load');
document.body.appendChild(img);
```

### Using Postman
1. URL: `GET http://localhost:8000/uploads/your-filename.jpg`
2. Don't add any Authorization header
3. Should return 200 OK with image data

### Using cURL
```bash
# Should work WITHOUT any token
curl http://localhost:8000/uploads/photo.jpg -o photo.jpg
echo $?  # Should print 0 for success
```

---

## ✅ Configuration Status

| Component | Configuration | Status |
|-----------|---|---|
| **SecurityConfig** | `/uploads/**` → `permitAll()` | ✅ Public |
| **WebMvcConfig** | Maps `/uploads` to file system | ✅ Serving |
| **CorsConfig** | Allows `http://localhost:4200` | ✅ Enabled |
| **FileUploadService** | Saves files to `/uploads` | ✅ Working |

---

## 📝 Summary

✅ **Images are PUBLIC** - No token needed to view them  
✅ **Direct browser access** - `<img src="url">` works  
✅ **CORS enabled** - Frontend can fetch images  
✅ **Secure storage** - Files on disk, filenames in DB  

**Access pattern:**
```
1. User updates profile with image
2. Backend saves to: /uploads/unique-filename.jpg
3. Backend returns: { photo: "unique-filename.jpg" }
4. Frontend displays: <img src="http://localhost:8000/uploads/unique-filename.jpg">
5. Browser loads image WITHOUT authentication
```


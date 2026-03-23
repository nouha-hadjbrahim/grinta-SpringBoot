# API Testing Guide - JWT Authentication & RBAC

## Prerequisites
- Backend running on `http://localhost:8000`
- Database configured and running
- Maven dependencies installed

## 1. Registration

### Register a Regular User
```bash
curl -X POST http://localhost:8000/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response (201 Created):**
```json
{
  "user": {
    "id": 1,
    "email": "user@example.com",
    "role": "USER"
  }
}
```

### Register an Admin User
```bash
curl -X POST http://localhost:8000/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123",
    "role": "ADMIN"
  }'
```

**Response (201 Created):**
```json
{
  "user": {
    "id": 2,
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

## 2. Authentication (Login)

### Login as Regular User
```bash
curl -X POST http://localhost:8000/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2MzI0OTY4MjAsImV4cCI6MTYzMjU4MzIyMH0.signature",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "role": "USER"
  }
}
```

**Store the token for subsequent requests:**
```bash
export USER_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Login as Admin
```bash
curl -X POST http://localhost:8000/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

**Store admin token:**
```bash
export ADMIN_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Invalid Credentials
```bash
curl -X POST http://localhost:8000/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "wrongpassword"
  }'
```

**Response (401 Unauthorized):**
```
"Invalid email or password"
```

### Duplicate Email
```bash
curl -X POST http://localhost:8000/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "newpassword"
  }'
```

**Response (400 Bad Request):**
```
"Email already exists"
```

## 3. User Profile Endpoints

### Get Current User Profile (Authenticated)
```bash
curl -X GET http://localhost:8000/api/user/profile \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "password": "$2a$10$...", // Hashed password
  "role": "USER"
}
```

### Update Current User Profile
```bash
curl -X PUT http://localhost:8000/api/user/profile \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "password": "$2a$10$...",
  "role": "USER"
}
```

### Access Protected Endpoint Without Token
```bash
curl -X GET http://localhost:8000/api/user/profile
```

**Response (401 Unauthorized):**
```
No authentication found in SecurityContext
```

### Access Protected Endpoint with Invalid Token
```bash
curl -X GET http://localhost:8000/api/user/profile \
  -H "Authorization: Bearer invalid_token"
```

**Response (401 Unauthorized):**
No response body (silently fails due to invalid token)

## 4. Admin-Only Endpoints

### Access Admin Dashboard (ADMIN)
```bash
curl -X GET http://localhost:8000/api/admin/dashboard \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Response (200 OK):**
```json
{
  "totalUsers": 2,
  "adminUsers": 1,
  "regularUsers": 1
}
```

### Try Admin Dashboard as Regular User
```bash
curl -X GET http://localhost:8000/api/admin/dashboard \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Response (403 Forbidden):**
```
Access Denied
```

### Get All Users (ADMIN)
```bash
curl -X GET http://localhost:8000/api/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "password": "$2a$10$...",
    "role": "USER"
  },
  {
    "id": 2,
    "email": "admin@example.com",
    "password": "$2a$10$...",
    "role": "ADMIN"
  }
]
```

### Get User by ID (ADMIN)
```bash
curl -X GET http://localhost:8000/api/admin/users/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "password": "$2a$10$...",
  "role": "USER"
}
```

### Update User Role (ADMIN)
```bash
curl -X PUT http://localhost:8000/api/admin/users/1/role \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d 'role=ADMIN'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "password": "$2a$10$...",
  "role": "ADMIN"
}
```

### Delete User (ADMIN)
```bash
curl -X DELETE http://localhost:8000/api/admin/users/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Response (204 No Content):**
(Empty response body)

### Get User Statistics (ADMIN)
```bash
curl -X GET http://localhost:8000/api/admin/statistics \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Response (200 OK):**
```json
{
  "total": 1,
  "admins": 1,
  "users": 0
}
```

## 5. Get User by Email (Any Authenticated User)

### Get User by Email
```bash
curl -X GET http://localhost:8000/api/user/email/user@example.com \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "password": "$2a$10$...",
  "role": "USER"
}
```

## 6. JWT Token Inspection

### Decode JWT Token (using jq or online tool)
```bash
echo $USER_TOKEN | cut -d'.' -f2 | base64 -d | jq .
```

**Output:**
```json
{
  "sub": "user@example.com",
  "role": "USER",
  "iat": 1632496820,
  "exp": 1632583220
}
```

### Check Token Expiration
Token expires 24 hours after creation.

## 7. Error Scenarios

### Missing Authorization Header
```bash
curl -X GET http://localhost:8000/api/admin/dashboard
```

**Response (401 Unauthorized)**

### Expired Token
```bash
curl -X GET http://localhost:8000/api/user/profile \
  -H "Authorization: Bearer $EXPIRED_TOKEN"
```

**Response (401 Unauthorized)**

### Invalid Role for Endpoint
```bash
curl -X GET http://localhost:8000/api/admin/users \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Response (403 Forbidden)**

## 8. Testing with Postman

### 1. Create new Request
- Method: POST
- URL: http://localhost:8000/api/auth/sign-in
- Body (raw, JSON):
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### 2. Save token to environment variable
- In Postman, go to Tests tab
- Add:
```javascript
pm.environment.set("token", pm.response.json().token);
```

### 3. Use token in subsequent requests
- Authorization tab: Select "Bearer Token"
- Token field: {{token}}

## 9. Common Issues & Solutions

### Issue: "No authentication found in SecurityContext"
- **Cause**: Token not sent or invalid
- **Solution**: Ensure Authorization header is present with "Bearer " prefix

### Issue: "403 Forbidden"
- **Cause**: User role doesn't match endpoint requirement
- **Solution**: Use admin token for /api/admin/** endpoints

### Issue: "401 Unauthorized"
- **Cause**: Invalid or expired token
- **Solution**: Login again to get a fresh token

### Issue: "Email already exists"
- **Cause**: Attempting to register with an email that's already in the database
- **Solution**: Use a different email or delete the user first

### Issue: "Invalid email or password"
- **Cause**: Incorrect credentials
- **Solution**: Verify email and password match database records

## Testing Checklist

- [ ] Register user successfully
- [ ] Register with duplicate email fails
- [ ] Login with valid credentials returns token
- [ ] Login with invalid credentials fails
- [ ] Access protected endpoint with invalid token
- [ ] Access admin endpoint as regular user (403)
- [ ] Access admin endpoint as admin (200)
- [ ] Update user profile
- [ ] Decode JWT token and verify role claim
- [ ] Token contains expiration time
- [ ] Access endpoint without authorization header (401)

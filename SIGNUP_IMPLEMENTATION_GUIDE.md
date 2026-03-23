# Sign-Up (Registration) Feature - Complete Implementation Guide

## Overview
A complete, production-ready sign-up feature has been implemented following best practices with proper validation, error handling, and JWT auto-login.

---

## Files Modified/Created

### 1. **DTOs (Data Transfer Objects)**

#### ✅ [RegisterRequest.java](src/main/java/com/esprit/backendsport/DTOs/RegisterRequest.java) - NEW
Request DTO for user registration with comprehensive validation:
- **Fields:**
  - `name` - Required, 2-100 characters
  - `email` - Required, valid email format
  - `phoneNumber` - Required, valid phone format (8-15 digits)
  - `password` - Required, 6-100 characters
  - `confirmPassword` - Required, must match password
- **Validation:** Uses Jakarta Bean Validation (JSR 380)

#### ✅ [LoginRequest.java](src/main/java/com/esprit/backendsport/DTOs/LoginRequest.java) - UPDATED
Added validation annotations for consistency:
- **Validations:** Email format, non-blank fields

#### ✅ [AuthResponse.java](src/main/java/com/esprit/backendsport/DTOs/AuthResponse.java) - UPDATED
Extended UserDto to include all user information:
- `id` - User identifier
- `name` - User full name
- `email` - User email
- `phoneNumber` - User phone number
- `role` - User role (USER or ADMIN)

---

### 2. **Entities**

#### ✅ [User.java](src/main/java/com/esprit/backendsport/Entities/User/User.java) - UPDATED
Enhanced User entity with complete profile fields:
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;                    // NEW
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String phoneNumber;             // NEW
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
}
```

#### ✅ [Role.java](src/main/java/com/esprit/backendsport/Entities/User/Role.java)
Enum for user roles:
```java
public enum Role {
    USER,    // Default for new registrations
    ADMIN    // Administrative privileges
}
```

---

### 3. **Services**

#### ✅ [AuthService.java](src/main/java/com/esprit/backendsport/Services/Auth/AuthService.java) - UPDATED
Enhanced authentication service with complete registration logic:

**`authenticate(LoginRequest)`** - Login method
- Validates email exists and password matches
- Generates JWT token
- Returns AuthResponse with user details

**`register(RegisterRequest)`** - Registration method (NEW)
- ✅ Validates password == confirmPassword
- ✅ Checks if email already exists
- ✅ Encodes password using BCrypt
- ✅ Assigns default role = USER
- ✅ Saves user to database
- ✅ Generates JWT token (auto-login)
- ✅ Returns AuthResponse with token and user details

**Error Handling:**
- `InvalidRequestException` - For validation errors (400)
- `ResourceAlreadyExistsException` - For duplicate email (409)

---

### 4. **Controllers**

#### ✅ [AuthController.java](src/main/java/com/esprit/backendsport/Controller/User/AuthController.java) - UPDATED

**POST /api/auth/sign-up** - Registration endpoint
```
Request:  RegisterRequest (name, email, phoneNumber, password, confirmPassword)
Response: AuthResponse (token, user details)
Status:   201 CREATED
Errors:   400 (validation), 409 (email exists)
```

**POST /api/auth/sign-in** - Login endpoint
```
Request:  LoginRequest (email, password)
Response: AuthResponse (token, user details)
Status:   200 OK
Errors:   400 (invalid credentials)
```

---

### 5. **Exception Handling**

#### ✅ [ResourceAlreadyExistsException.java](src/main/java/com/esprit/backendsport/Exception/ResourceAlreadyExistsException.java) - NEW
Custom exception for duplicate emails (409 Conflict)

#### ✅ [InvalidRequestException.java](src/main/java/com/esprit/backendsport/Exception/InvalidRequestException.java) - NEW
Custom exception for validation errors (400 Bad Request)

#### ✅ [GlobalExceptionHandler.java](src/main/java/com/esprit/backendsport/Exception/GlobalExceptionHandler.java) - UPDATED
Centralized exception handling:
- `ResourceAlreadyExistsException` → 409 Conflict
- `InvalidRequestException` → 400 Bad Request
- `MethodArgumentNotValidException` → 400 Bad Request (detailed field errors)
- Generic `RuntimeException` → 400 Bad Request

---

### 6. **Security Configuration**

#### ✅ [SecurityConfig.java](src/main/java/com/esprit/backendsport/Security/SecurityConfig.java)
Already configured to:
- ✅ Allow public access to `/api/auth/**` endpoints
- ✅ Use BCryptPasswordEncoder for password encoding
- ✅ Stateless JWT-based authentication
- ✅ CORS enabled for localhost:4200

---

### 7. **Repository**

#### ✅ [UserRepository.java](src/main/java/com/esprit/backendsport/Repository/User/UserRepository.java)
Already includes:
- `Optional<User> findByEmail(String email)` - Find user by email

---

### 8. **Dependencies**

#### ✅ [pom.xml](pom.xml) - UPDATED
Added validation dependency:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## API Endpoint Reference

### Register (Sign-Up)
```http
POST /api/auth/sign-up
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "+216 98 765 432",
  "password": "SecurePassword123",
  "confirmPassword": "SecurePassword123"
}
```

**Success Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+216 98 765 432",
    "role": "USER"
  }
}
```

**Validation Error (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid",
    "password": "Password must be between 6 and 100 characters",
    "confirmPassword": "Password and confirm password do not match"
  }
}
```

**Email Already Exists (409 Conflict):**
```json
{
  "status": 409,
  "message": "Email already registered"
}
```

---

### Login (Sign-In)
```http
POST /api/auth/sign-in
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+216 98 765 432",
    "role": "USER"
  }
}
```

---

## Validation Rules

### Name
- ✅ Required
- ✅ Minimum 2 characters
- ✅ Maximum 100 characters

### Email
- ✅ Required
- ✅ Valid email format

### Phone Number
- ✅ Required
- ✅ Valid phone format (8-15 digits, supports +, -, space)

### Password
- ✅ Required
- ✅ Minimum 6 characters
- ✅ Maximum 100 characters
- ✅ Must match confirmPassword

### Role
- ✅ Automatically set to `USER` for new registrations
- ✅ Cannot be changed by user input

---

## Database Schema

The `users` table will be created with:
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  phone_number VARCHAR(20) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'USER'
);
```

---

## Features Implemented

✅ **User Registration with Validation**
- Comprehensive input validation
- Password confirmation matching
- Unique email constraint

✅ **Password Security**
- BCrypt hashing (industry standard)
- Password never stored in plain text

✅ **JWT Auto-Login**
- Automatic token generation after registration
- No need for separate login step
- Token included in registration response

✅ **Error Handling**
- Clear, granular error messages
- Appropriate HTTP status codes (400, 409)
- Field-level validation error details

✅ **User Roles**
- Default USER role for new registrations
- Role-based access control ready
- Extensible for future role additions

✅ **CORS Support**
- Configured for Angular frontend (localhost:4200)
- Cross-origin requests enabled

✅ **Security**
- Stateless JWT authentication
- Password encryption with BCrypt
- No session storage (REST best practice)

---

## Testing the Feature

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phoneNumber": "98765432",
    "password": "Password123",
    "confirmPassword": "Password123"
  }'
```

### 2. Test with Postman/Thunder Client
- URL: `POST http://localhost:8080/api/auth/sign-up`
- Body (raw JSON):
```json
{
  "name": "Test User",
  "email": "test@example.com",
  "phoneNumber": "+216987654321",
  "password": "TestPass123",
  "confirmPassword": "TestPass123"
}
```

### 3. Login with Registered Email
```bash
curl -X POST http://localhost:8080/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "Password123"
  }'
```

---

## Next Steps (Optional Enhancements)

1. **Email Verification**
   - Send confirmation email after registration
   - Require click-through before account activation

2. **Rate Limiting**
   - Prevent brute force attacks
   - Limit registration attempts per IP

3. **User Profile Endpoints**
   - GET /api/user/profile
   - PUT /api/user/profile

4. **Account Recovery**
   - Password reset functionality
   - Email change verification

5. **Two-Factor Authentication (2FA)**
   - Optional SMS or email verification
   - Enhanced security for critical accounts

---

## Best Practices Applied

✅ **Input Validation** - All inputs validated at DTO level
✅ **Error Handling** - Custom exceptions for specific error cases
✅ **Security** - BCrypt password encoding, JWT tokens
✅ **Code Organization** - Separation of concerns (Controllers, Services, DTOs)
✅ **Documentation** - Comprehensive inline comments and guide
✅ **RESTful Design** - Proper HTTP methods and status codes
✅ **Dependency Injection** - Spring's @Autowired pattern
✅ **Builder Pattern** - Lombok @Builder for object creation

---

**Implementation Status: ✅ COMPLETE**
Ready for production use with all requirements satisfied.

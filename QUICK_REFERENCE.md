# JWT Authentication & RBAC - Quick Reference

## What Was Implemented

### ✅ Completed Components

1. **User Entity** - Added `role` field (ENUM: ADMIN, USER)
2. **Role Enum** - `public enum Role { USER, ADMIN }`
3. **JWT Token** - Includes role in claims, 24-hour expiration
4. **Authentication Filter** - Intercepts requests, extracts Bearer token
5. **Security Configuration** - Stateless, CORS enabled, role-based authorization
6. **DTOs** - LoginRequest, AuthResponse (structured response)
7. **Auth Service** - Business logic for login/register
8. **Exception Handler** - Global error handling
9. **Admin Controller** - Protected /api/admin/** endpoints
10. **User Controller** - Protected /api/user/** endpoints with profile management

## Project Files Created/Modified

### New Files
- `src/main/java/com/esprit/backendsport/Entities/User/Role.java`
- `src/main/java/com/esprit/backendsport/DTOs/LoginRequest.java`
- `src/main/java/com/esprit/backendsport/DTOs/AuthResponse.java`
- `src/main/java/com/esprit/backendsport/Security/JwtAuthenticationFilter.java`
- `src/main/java/com/esprit/backendsport/Security/SecurityConfig.java`
- `src/main/java/com/esprit/backendsport/Security/CorsConfig.java`
- `src/main/java/com/esprit/backendsport/Services/Auth/AuthService.java`
- `src/main/java/com/esprit/backendsport/Exception/GlobalExceptionHandler.java`
- `src/main/java/com/esprit/backendsport/Controller/Admin/AdminController.java`
- Documentation files (guides for API testing and Angular integration)

### Modified Files
- `src/main/java/com/esprit/backendsport/Entities/User/User.java` - Added role field
- `src/main/java/com/esprit/backendsport/Security/JwtUtil.java` - Added role claims
- `src/main/java/com/esprit/backendsport/Controller/User/AuthController.java` - Refactored for clean architecture
- `src/main/java/com/esprit/backendsport/Controller/User/UserController.java` - Enhanced with role-based endpoints
- `pom.xml` - Added spring-boot-starter-security dependency

## API Endpoints

### Public Endpoints (No Authentication Required)
```
POST /api/auth/sign-in       - Login user
POST /api/auth/sign-up       - Register new user
```

### User Endpoints (Requires USER or ADMIN role)
```
GET  /api/user/profile       - Get current user profile
PUT  /api/user/profile       - Update current user profile
GET  /api/user/email/{email} - Get user by email
```

### Admin Endpoints (ADMIN only)
```
GET    /api/admin/dashboard       - Dashboard statistics
GET    /api/admin/users           - List all users
GET    /api/admin/users/{id}      - Get user by ID
PUT    /api/admin/users/{id}/role - Update user role
DELETE /api/admin/users/{id}      - Delete user
GET    /api/admin/statistics      - User statistics
```

## Login Flow

```
1. POST /api/auth/sign-in
   Request: { "email": "user@example.com", "password": "password123" }
   Response: {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "user": {
       "id": 1,
       "email": "user@example.com",
       "role": "ADMIN"
     }
   }

2. Angular stores token: localStorage.setItem('token', response.token)

3. Subsequent requests include: Authorization: Bearer <TOKEN>

4. JwtAuthenticationFilter verifies token and sets authentication

5. SecurityConfig checks user role against endpoint requirements
```

## JWT Token Structure

```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: {
  "sub": "user@example.com",
  "role": "ADMIN",
  "iat": 1632496820,
  "exp": 1632583220
}
Signature: HS256 (256-bit secret key)
```

## Security Features

| Feature | Implementation |
|---------|-----------------|
| Password Encryption | BCrypt (strength 10) |
| Token Generation | HS256 Algorithm |
| Token Expiration | 24 hours |
| Session Management | Stateless (JWT-based) |
| CSRF Protection | Disabled (safe with JWT) |
| CORS | Enabled for localhost:4200 |
| Authorization | Role-based (RBAC) |
| Token Validation | Signature and expiration check |

## Angular Integration Checklist

- [ ] Create AuthService with login/register/logout methods
- [ ] Create JwtInterceptor to add Authorization header
- [ ] Create AuthGuard for route protection
- [ ] Create RoleGuard for role-based route access
- [ ] Configure HTTP_INTERCEPTORS in AppModule
- [ ] Setup routing with guards
- [ ] Create login component with form validation
- [ ] Create navigation menu with role-based links
- [ ] Store token in localStorage on login
- [ ] Clear token/user on logout
- [ ] Handle 401/403 errors and redirect to login
- [ ] Show role-specific UI elements

## Testing Commands

### Register User
```bash
curl -X POST http://localhost:8000/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### Login
```bash
curl -X POST http://localhost:8000/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8000/api/user/profile \
  -H "Authorization: Bearer <TOKEN>"
```

### Try Admin Endpoint with User Token
```bash
curl -X GET http://localhost:8000/api/admin/users \
  -H "Authorization: Bearer <USER_TOKEN>"
# Returns 403 Forbidden
```

## Configuration

### SecurityConfig Rules
```java
authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()           // Public
    .requestMatchers("/api/public/**").permitAll()         // Public
    .requestMatchers("/api/admin/**").hasRole("ADMIN")     // ADMIN only
    .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")  // USER + ADMIN
    .anyRequest().authenticated()                          // Require auth
)
```

### CORS Configuration
- Allowed Origins: http://localhost:4200
- Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Max Age: 3600 seconds

### JWT Configuration
- Secret Key: Generated from `Keys.secretKeyFor(SignatureAlgorithm.HS256)`
- Expiration: 86400000 ms (24 hours)

## Common Error Responses

| Status | Error | Cause |
|--------|-------|-------|
| 400 | "Email already exists" | Duplicate email on signup |
| 400 | Invalid email or password | Wrong credentials on login |
| 401 | No error body | Missing/invalid token |
| 403 | Access Denied | User role doesn't match endpoint |
| 500 | (varies) | Server error |

## File Structure Summary

```
com.esprit.backendsport/
├── Entities.User/
│   ├── User.java          ✏️ Added role field
│   └── Role.java           ✨ New
├── DTOs/
│   ├── LoginRequest.java   ✨ New
│   └── AuthResponse.java   ✨ New
├── Security/
│   ├── JwtUtil.java        ✏️ Updated with role
│   ├── JwtAuthenticationFilter.java  ✨ New
│   ├── SecurityConfig.java ✨ New
│   └── CorsConfig.java     ✨ New
├── Services/
│   ├── User/
│   │   ├── IUserService.java
│   │   └── UserService.java
│   └── Auth/
│       └── AuthService.java ✨ New
├── Controller/
│   ├── User/
│   │   ├── AuthController.java ✏️ Refactored
│   │   └── UserController.java ✏️ Enhanced
│   └── Admin/
│       └── AdminController.java ✨ New
├── Exception/
│   └── GlobalExceptionHandler.java ✨ New
└── Repository.User/
    └── UserRepository.java
```

## Next Steps for Angular Development

1. Install dependencies: `npm install`
2. Create AuthService (see ANGULAR_INTEGRATION_GUIDE.md)
3. Create JwtInterceptor
4. Create route guards
5. Setup routing configuration
6. Create login/register components
7. Add role-based navigation
8. Handle authentication state
9. Test all flows with API_TESTING_GUIDE.md

## Documentation Files

1. **AUTHENTICATION_GUIDE.md** - Complete architecture & auth flow overview
2. **API_TESTING_GUIDE.md** - All endpoint examples with curl commands
3. **ANGULAR_INTEGRATION_GUIDE.md** - Complete Angular implementation examples
4. **jwtauth-implementation.md** - (memory) Quick reference key facts

## Key Architectural Decisions

✅ **Clean Architecture**: Separated concerns (Controller → Service → Repository)
✅ **DTOs**: No entity exposure in API responses
✅ **Stateless**: JWT-based, no session storage
✅ **Security**: BCrypt for passwords, HS256 for tokens
✅ **RBAC**: Role-based authorization at method level
✅ **CORS**: Configured for Angular dev server
✅ **Error Handling**: Global exception handler
✅ **Best Practices**: Observable pattern, dependency injection, Spring annotations

## Performance & Scalability Considerations

- JWT tokens don't require database lookup on every request
- Stateless design allows horizontal scaling
- Single secret key for all tokens (can be enhanced with refresh tokens)
- No session storage overhead
- Filter-based authentication reduces method-level annotation overhead

## Security Considerations & Recommendations

### Current Strengths
✅ Password hashing with BCrypt
✅ JWT with HMAC-SHA256 signing
✅ Role-based access control
✅ CORS restrictions
✅ Stateless architecture

### Recommended Future Enhancements
📋 Implement refresh tokens for better security
📋 Add token blacklist/revocation mechanism
📋 Implement rate limiting on auth endpoints
📋 Add email verification for new registrations
📋 Implement password reset functionality
📋 Add audit logging for security events
📋 Consider OAuth2 integration
📋 Implement multi-factor authentication

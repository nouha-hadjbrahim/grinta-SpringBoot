# JWT Authentication & Role-Based Access Control - Implementation Guide

## Overview
This document describes the complete JWT-based authentication system with role-based access control (RBAC) implemented in the Spring Boot backend.

## Architecture Components

### 1. **User Entity** (`User.java`)
- `id`: Long (Primary Key)
- `email`: String (Unique, Not Null)
- `password`: String (Not Null, BCrypt Encoded)
- `role`: Role Enum (ADMIN or USER, default: USER)

### 2. **Role Enum** (`Role.java`)
```
- USER: Regular user access
- ADMIN: Administrative access
```

### 3. **DTOs** (Data Transfer Objects)

#### LoginRequest
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### AuthResponse
```json
{
  "token": "JWT_TOKEN",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "role": "USER"
  }
}
```

## Security Components

### 1. **JwtUtil** (`JwtUtil.java`)
Handles JWT token generation and validation:
- `generateToken(email, role)`: Creates JWT with role claim
- `extractEmail(token)`: Extracts email from token
- `extractRole(token)`: Extracts role from token
- `validateToken(token)`: Validates token signature and expiration

**Token Expiration**: 24 hours

### 2. **JwtAuthenticationFilter** (`JwtAuthenticationFilter.java`)
- Intercepts all HTTP requests
- Extracts JWT from Authorization header (Bearer token)
- Validates token
- Sets authentication in SecurityContext with role authority

### 3. **SecurityConfig** (`SecurityConfig.java`)
Configures Spring Security with:
- **CORS**: Enabled for localhost:4200 (Angular)
- **CSRF**: Disabled (stateless JWT auth)
- **Session**: STATELESS (no session storage)
- **Authorization Rules**:
  - `/api/auth/**`: All can access (public)
  - `/api/public/**`: All can access (public)
  - `/api/admin/**`: Only ADMIN can access
  - `/api/user/**`: USER or ADMIN can access
  - All others: Require authentication

### 4. **CorsConfig** (`CorsConfig.java`)
Configures CORS for cross-origin requests from Angular frontend:
- Allowed origins: http://localhost:4200
- Allowed methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Allowed headers: All
- Max age: 3600 seconds

## Authentication Flow

### Login Process
```
1. User sends LoginRequest to POST /api/auth/sign-in
2. AuthService validates email and password
3. If valid:
   - Generate JWT token with user role
   - Return AuthResponse with token and user info
4. Angular stores token in localStorage
5. Angular includes token in Authorization header for subsequent requests
```

### Registration Process
```
1. User sends User object to POST /api/auth/sign-up
2. AuthService:
   - Checks if email already exists
   - Encodes password using BCrypt
   - Sets default role to USER if not provided
   - Saves user to database
3. Returns AuthResponse with user info (no token on signup)
```

### Subsequent Requests
```
1. Angular includes: Authorization: Bearer <JWT_TOKEN>
2. JwtAuthenticationFilter intercepts request
3. Extracts and validates token
4. Sets authentication with role authority
5. SecurityConfig checks role against endpoint requirements
6. If authorized, request proceeds; otherwise 403 Forbidden
```

## API Endpoints

### Authentication Endpoints

#### Login
```
POST /api/auth/sign-in
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "role": "USER"
  }
}

Response 401:
"Invalid email or password"
```

#### Register
```
POST /api/auth/sign-up
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "password123",
  "role": "USER" (optional, defaults to USER)
}

Response 201:
{
  "user": {
    "id": 2,
    "email": "newuser@example.com",
    "role": "USER"
  }
}

Response 400:
"Email already exists"
```

## Angular Integration

### 1. Store Token in localStorage
```typescript
// After successful login
const response = await this.authService.login(credentials).toPromise();
localStorage.setItem('token', response.token);
localStorage.setItem('userRole', response.user.role);
```

### 2. Create HTTP Interceptor
```typescript
export class JwtInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpHttpEvent<any>> {
    const token = localStorage.getItem('token');
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(req);
  }
}
```

### 3. Role-Based Route Guard
```typescript
export class RoleGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const userRole = localStorage.getItem('userRole');
    const requiredRole = route.data['role'];

    if (userRole === requiredRole || userRole === 'ADMIN') {
      return true;
    }

    this.router.navigate(['/unauthorized']);
    return false;
  }
}
```

### 4. Route Configuration
```typescript
const routes: Routes = [
  { path: '', component: UserComponent },
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [RoleGuard],
    data: { role: 'ADMIN' }
  },
  { 
    path: 'user', 
    component: UserProfileComponent,
    canActivate: [RoleGuard],
    data: { role: 'USER' }
  }
];
```

### 5. Navigation Based on Role
```typescript
export class AppComponent implements OnInit {
  userRole: string | null = null;

  ngOnInit() {
    this.userRole = localStorage.getItem('userRole');
  }

  navigateBasedOnRole() {
    if (this.userRole === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else if (this.userRole === 'USER') {
      this.router.navigate(['/user']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
```

## Security Features

1. **Password Encryption**: BCrypt with strength 10
2. **JWT Signing**: HS256 algorithm with 256-bit secret key
3. **Token Expiration**: 24 hours
4. **Stateless Architecture**: No session storage on server
5. **Role-Based Access**: Method-level authorization
6. **CORS Protection**: Only specific origins allowed
7. **CSRF Protection**: Disabled for stateless API (JWT in bearertoken is CSRF-safe)

## Project Dependencies

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

## Testing the Implementation

### 1. Register a User
```bash
curl -X POST http://localhost:8000/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8000/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### 3. Access Protected Endpoint
```bash
curl -X GET http://localhost:8000/api/user/profile \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 4. Try Admin Endpoint with User Token
```bash
curl -X GET http://localhost:8000/api/admin/dashboard \
  -H "Authorization: Bearer <USER_JWT_TOKEN>"
# Should return 403 Forbidden
```

## File Structure

```
src/main/java/com/esprit/backendsport/
├── Controller/
│   └── User/
│       └── AuthController.java
├── DTOs/
│   ├── AuthResponse.java
│   └── LoginRequest.java
├── Entities/
│   └── User/
│       ├── Role.java
│       └── User.java
├── Exception/
│   └── GlobalExceptionHandler.java
├── Security/
│   ├── CorsConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
├── Services/
│   ├── Auth/
│   │   └── AuthService.java
│   └── User/
│       ├── IUserService.java
│       └── UserService.java
└── Repository/
    └── User/
        └── UserRepository.java
```

## Troubleshooting

### Token Not Working
- Verify token is sent in Authorization header with "Bearer " prefix
- Check token hasn't expired (24 hours)
- Ensure SECRET_KEY in JwtUtil matches between generation and validation

### 403 Forbidden on Protected Endpoints
- Check user role matches endpoint requirement
- Verify role is correctly stored in database
- Check JWT token contains correct role claim

### CORS Issues
- Verify Angular app is running on http://localhost:4200
- Check CORS configuration in CorsConfig
- Look for browser console CORS errors

### Login Failures
- Verify email is unique in database
- Ensure password is correctly hashed (check BCrypt encoding)
- Verify user exists and credentials are correct

## Future Enhancements

1. Refresh token implementation for better security
2. Token blacklist/revocation mechanism
3. Role hierarchy (ADMIN inherits from USER)
4. Permission-based access control
5. Multi-factor authentication
6. Email verification for registration
7. Password reset functionality

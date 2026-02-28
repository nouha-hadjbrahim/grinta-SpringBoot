# GRINTA - Backend Sport Project Notes

## Project Structure
```
src/main/java/com/esprit/backendsport/
├── BackendSportApplication.java
│
├── Security/
│   └── JwtUtil.java                   # JWT token generation & validation
│
├── Entities/
│   ├── User/
│   ├── bookings/
│   ├── coaching/
│   ├── community/
│   ├── events/
│   ├── gyms/
│   ├── nutrition/
│   └── workouts/
│
├── Repository/
│   ├── User/
│   ├── bookings/
│   ├── coaching/
│   ├── community/
│   ├── events/
│   ├── gyms/
│   ├── nutrition/
│   └── workouts/
│
├── Services/
│   ├── User/
│   │   ├── IUserService.java
│   │   └── UserService.java
│   ├── bookings/
│   ├── coaching/
│   ├── community/
│   ├── events/
│   ├── gyms/
│   ├── nutrition/
│   └── workouts/
│
└── Controller/
    ├── User/
    │   ├── UserController.java        # REST endpoints (GET, DELETE)
    │   └── AuthController.java        # sign-in, sign-up with BCrypt + JWT
    ├── bookings/
    ├── coaching/
    ├── community/
    ├── events/
    ├── gyms/
    ├── nutrition/
    └── workouts/

src/main/resources/
└── application.properties
```

## Configuration (application.properties)
| Property | Value |
|----------|-------|
| Server port | `8000` |
| Database | MySQL on port `3300` |
| Database name | `sport` |
| DDL auto | `create` |
| Show SQL | `true` |

## Entities
### `User.java`
| Field | Type | Constraint |
|-------|------|------------|
| `id` | `Long` | PK, Auto-generated |
| `email` | `String` | Unique, Not null |
| `password` | `String` | Not null (BCrypt hashed) |

## Repository — `UserRepository`
- Extends `JpaRepository<User, Long>`
- Custom method: `findByEmail(String email)` → `Optional<User>`

## Service
### `IUserService` (interface)
- `save(User user)`
- `findByEmail(String email)`
- `findAll()`
- `deleteById(Long id)`

### `UserService` (implementation)
- Annotated with `@Service`
- Uses `@RequiredArgsConstructor` for dependency injection

## Controller — `UserController`
Base URL: `http://localhost:8000/api/users`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/email/{email}` | Get user by email |
| `DELETE` | `/api/users/{id}` | Delete user by ID |

> ⚠️ Use `/api/auth/sign-up` to create users

## Controller — `AuthController`
Base URL: `http://localhost:8000/api/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/sign-up` | Register user (BCrypt hashes password) |
| `POST` | `/api/auth/sign-in` | Login → returns `{ token, email, id }` |

## Security — `JwtUtil`
- Generates JWT token on sign-in (expires in **24 hours**)
- Validates token
- Extracts email from token
- Uses `HS256` algorithm

## Key Decisions
- ✅ Organized all features into sub-packages (`Controller/user`, `Entities/events`, etc.)
- ✅ Used **interface + implementation** pattern for service layer
- ✅ Used **Lombok** to reduce boilerplate
- ✅ Used **BCrypt** to hash passwords on sign-up and verify on sign-in
- ✅ Used **JWT** (`jjwt 0.11.5`) — token returned on sign-in, stored on frontend
- ✅ Removed `POST /api/users` — user creation only through `/api/auth/sign-up`

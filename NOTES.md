# GRINTA - Backend Sport Project Notes

## Project Structure
```
src/main/java/com/esprit/backendsport/
├── BackendSportApplication.java       # Spring Boot entry point
│
├── Entities/
│   └── User.java                      # User entity (id, email, password)
│
├── Repository/
│   └── UserRepository.java            # Extends JpaRepository, custom findByEmail
│
├── Services/
│   ├── IUserService.java              # Service interface (contract)
│   └── UserService.java               # Service implementation
│
└── Controller/
    ├── UserController.java            # REST endpoints for User (GET, DELETE)
    └── AuthController.java            # Auth endpoints (login, register) with BCrypt

src/main/resources/
└── application.properties             # DB config, port, JPA settings
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
| `password` | `String` | Not null |

## Repository — `UserRepository`
- Extends `JpaRepository<User, Long>` (free CRUD operations)
- Custom method: `findByEmail(String email)` → `Optional<User>`

## Service
### `IUserService` (interface)
- `save(User user)`
- `findByEmail(String email)`
- `findAll` 
- `deleteById(Long id)`

### `UserService` (implementation)
- Annotated with `@Service`
- Uses `@RequiredArgsConstructor` for dependency injection

## Controller — `UserController`
Base URL: `http://localhost:8000/api/users`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users?page=0&size=10` | Get users (paginated) |
| `GET` | `/api/users/email/{email}` | Get user by email |
| `DELETE` | `/api/users/{id}` | Delete user by ID |

> ⚠️`/api/auth/register` (create user)

- `@CrossOrigin(origins = "http://localhost:4200")` enabled for Angular

## Controller — `AuthController`
Base URL: `http://localhost:8000/api/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/sign-up` | Register new user (BCrypt hashes password) |
| `POST` | `/api/auth/sign-in` | Login (BCrypt verifies password) |

## Key Decisions
- ✅ Used **pagination** (`Pageable`) instead of `findAll()` to avoid loading unnecessary data
- ✅ Used **interface + implementation** pattern for the service layer
- ✅ Used **Lombok** (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`) to reduce boilerplate
- ✅ Used **BCrypt** (`spring-security-crypto`) to hash passwords on register and verify on login
- ✅ Removed `POST /api/users` — user creation only through `/api/auth/register`
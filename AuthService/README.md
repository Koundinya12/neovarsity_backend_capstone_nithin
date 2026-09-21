# AuthService

AuthService is a Spring Boot microservice that provides user authentication and authorization using JWT. It supports user registration and login, issues signed access tokens, and protects endpoints via a stateless security filter chain.

## Features
- User registration and login
- Password hashing with Spring Security
- JWT token issuance and validation
- Stateless authentication via JWT filter
- Centralized exception handling
- Pluggable user persistence via Spring Data JPA
- Configurable JWT properties
- Integration-friendly (Feign client scaffold present)

## Tech Stack
- Java 17+ (recommended)
- Spring Boot (Web, Security)
- Spring Data JPA
- JJWT (or equivalent) for token handling via JwtService
- Maven build
- JUnit 5 + Spring Boot Test

## Project Structure
```
src/main/java/com/auth
├── AuthServiceApplication.java          # Spring Boot entrypoint
├── configuration/
│   ├── FeignConfig.java                 # Feign configuration scaffold
│   └── UserClient.java                  # Example Feign client
├── controlleradvice/
│   └── GlobalExceptionHandler.java      # Centralized error handling
├── dtos/
│   ├── AuthResponse.java                # Token/response DTO
│   ├── LoginRequest.java                # Login payload
│   ├── RegisterRequest.java             # Registration payload
│   └── UserResponseDto.java             # Public user DTO
├── exceptions/
│   └── UserNameAlreadyExists.java       # Domain exception
├── models/
│   ├── Role.java                        # Role enum/model
│   └── User.java                        # User entity
├── repositories/
│   └── UserRepository.java              # User persistence
└── security/
    ├── AuthController.java              # /api/auth endpoints
    ├── AuthService.java                 # Business logic for auth
    ├── CustomUserDetailsService.java    # Spring Security adapter
    ├── JwtAuthFilter.java               # JWT validation filter
    ├── JwtProperties.java               # Configuration props (secret, ttl)
    ├── JwtService.java                  # Token creation/validation
    └── SecurityConfig.java              # Security filter chain

src/test/java/com/auth/security
└── AuthControllerTest.java              # API tests
```

## Configuration
Set properties in `src/main/resources/application.properties`:

```
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/auth
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=change-me-to-a-long-random-string
jwt.expiration=3600000  # 1 hour in ms
```

Notes:
- Ensure `jwt.secret` is a strong, base64-safe secret. For HS256, use at least 256-bit entropy.
- Configure your actual datasource; for local development you can switch to H2:
  - `spring.datasource.url=jdbc:h2:mem:auth;DB_CLOSE_DELAY=-1`
  - `spring.jpa.hibernate.ddl-auto=update`
  - Add H2 dependency if needed.

## API
Base path: `/api/auth`

- POST `/register`
  - Request: RegisterRequest { username, password, ... }
  - Response: UserResponseDto
  - Errors: 409 if username already exists

- POST `/login`
  - Request: LoginRequest { username, password }
  - Response: AuthResponse { accessToken, tokenType }
  - Errors: 401 for bad credentials

JWT
- Authorization header: `Authorization: Bearer <token>`
- Protected endpoints are configured in `SecurityConfig` and enforced by `JwtAuthFilter`.

## Build and Run
Prerequisites: Java 17+, Maven, a running database (unless using H2)

- Build
```
mvn -q -DskipTests package
```

- Run (local)
```
mvn spring-boot:run
```
Or run the generated jar:
```
java -jar target/authservice-*.jar
```

The service starts on `http://localhost:8080` by default.

## Testing
Run unit/integration tests:
```
mvn test
```

AuthControllerTest contains API-level tests for authentication flows.

## Example Usage
- Register
```
curl -s -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"StrongP@ssw0rd"}'
```

- Login
```
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"StrongP@ssw0rd"}' | jq -r .accessToken)
```

- Call a protected endpoint
```
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/protected
```

## Troubleshooting
- 401 Unauthorized: verify Authorization header format and token validity; ensure clock skew not excessive.
- 403 Forbidden: user lacks required roles/authorities; check SecurityConfig.
- Bean creation or circular dependency errors: verify component scanning and constructor injection in security components.
- Invalid JWT signature: ensure `jwt.secret` matches between issuer and verifier instances.
- Database connection failures: confirm datasource URL/credentials; for local dev, consider H2.

## Extending
- Add refresh tokens: create RefreshToken entity, issue long-lived refresh + short-lived access tokens.
- Role-based access control: expand Role enum and method-level security with `@PreAuthorize`.
- Logout/blacklist: maintain token revocation list (cache/DB) if necessary for your threat model.
- Observability: add Spring Boot Actuator and structured logging.

## License
This project is provided as-is; add a license file if you plan to distribute.

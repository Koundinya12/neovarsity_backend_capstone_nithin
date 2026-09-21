# UserService

A Spring Boot microservice for managing users in an e-commerce system. It provides REST APIs for user registration, authentication (JWT), retrieval and management, with layered architecture, global exception handling, and unit/integration tests.

## Tech Stack
- Java 17+ (compatible with 11 if configured)
- Spring Boot 3.x
- Spring Web, Spring Security, JWT
- Spring Data JPA (with an in-memory H2 or configured RDBMS)
- Maven
- JUnit 5, Mockito

## Project Structure
- src/main/java/com/ecom/userservice
  - configuration: App-wide beans and configuration
  - controllers: REST controllers (UserController, TestController)
  - controlleradvice: GlobalExceptionHandler
  - dtos: Response DTOs
  - exceptions: Domain-specific exceptions
  - models: JPA entities (User, Address, Role, BaseClass)
  - repositories: Spring Data repositories (UserRepository)
  - security: JWT services, filters, and security configuration
  - services: Service interfaces and implementations
  - UserServiceApplication: Spring Boot main class
- src/test/java/...: Unit and slice tests
- src/main/resources/application.properties: runtime configuration
- pom.xml: Maven dependencies and plugins

## Getting Started

### Prerequisites
- Java 17 installed and on PATH: `java -version`
- Maven 3.8+ (wrapper included: `./mvnw`)

### Clone and Build
```
git clone <repo-url>
cd UserService
./mvnw clean install
```

### Run the Service
- Using Maven:
```
./mvnw spring-boot:run
```
- Or with the built jar:
```
java -jar target/userservice-*.jar
```
Default server port: 8080 (configurable).

## Configuration
Edit `src/main/resources/application.properties` (and `src/test/resources/application.properties` for tests). Common properties:

- Server
  - `server.port=8080`
- Datasource (H2 by default or configure your RDBMS)
  - `spring.datasource.url=jdbc:h2:mem:users;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
  - `spring.datasource.driverClassName=org.h2.Driver`
  - `spring.datasource.username=sa`
  - `spring.datasource.password=`
  - `spring.jpa.hibernate.ddl-auto=update` (or validate/create)
- JWT
  - `jwt.secret=change-me` (or see JwtProperties if mapped to `application.properties`)
  - `jwt.expiration=3600000` (example 1h)

Check `security/JwtProperties.java` for exact property keys used.

## API Overview

Base path: `/api/v1/users` (verify in `UserController` mappings)

- Auth
  - POST `/auth/register` – Register a new user
  - POST `/auth/login` – Authenticate and receive JWT
- Users
  - GET `/` – List users (secured)
  - GET `/{id}` – Get user by id (secured)
  - POST `/` – Create user (secured/role-based)
  - PUT `/{id}` – Update user (secured)
  - DELETE `/{id}` – Delete user (secured/role-based)

Note: Exact endpoints and request/response schemas are defined in `controllers/UserController.java` and DTOs. JWT token must be provided in `Authorization: Bearer <token>` header for secured endpoints.

### Example Requests
- Register:
```
curl -X POST http://localhost:8080/api/v1/users/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"P@ssw0rd","email":"alice@example.com"}'
```
- Login:
```
curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"P@ssw0rd"}'
```
- Get users:
```
curl http://localhost:8080/api/v1/users \
  -H 'Authorization: Bearer <JWT>'
```

## Security
- Spring Security with JWT bearer tokens
- `SecurityConfig.java` defines HTTP security, authentication manager, and filters
- `JwtAuthenticationFilter.java` processes and validates tokens using `JwtService`
- Roles and authorities modeled in `models/Role.java` and `User.java`

## Error Handling
- Centralized via `controlleradvice/GlobalExceptionHandler.java`
- Custom exceptions in `exceptions/` map to proper HTTP responses

## Testing
Run all tests:
```
./mvnw test
```
Key tests:
- `services/UserServiceImplTest.java` – service logic
- `controllers/UserControllerTest.java` – controller layer
- `repositories/UserRepositoryTest.java` – repository layer

## Development Notes
- Follow service-interface-implementation pattern (`UserService` / `UserServiceImpl`)
- Use DTOs for external responses (`dtos/`)
- Entities extend `BaseClass` for common fields (e.g., id, timestamps)
- Keep controller slim; business logic belongs in services

## Building a Docker Image (optional)
If you add a Dockerfile, build and run with:
```
docker build -t userservice:latest .
docker run --rm -p 8080:8080 -e JAVA_OPTS="-Xms256m -Xmx512m" userservice:latest
```

## Troubleshooting
- Ensure `jwt.secret` is set to a sufficiently long random value in production
- Verify DB connection properties if not using H2
- If CORS issues occur, configure allowed origins in `SecurityConfig`
- Use `--debug` flag with Spring Boot for auto-configuration insights

## License
Add your license here (e.g., MIT, Apache-2.0).

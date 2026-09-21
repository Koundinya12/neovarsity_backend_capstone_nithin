# CartService

A Spring Boot microservice that manages shopping carts for an e-commerce platform. It provides REST APIs to create a cart, add/remove items, view cart contents, and compute totals. The service integrates with a Product service (via OpenFeign) to validate products and retrieve pricing details. JWT-based authentication is configured with Spring Security, and tests are provided across controller, service, and repository layers.

## Features
- Create and fetch carts for users
- Add, update quantity, and remove items from carts
- Compute cart totals
- Validation and error handling via ControllerAdvice
- JWT authentication with configurable properties
- Integration with external Product service using OpenFeign

## Tech Stack
- Java 17 (recommended)
- Spring Boot 3.x
  - Spring Web
  - Spring Data JPA
  - Spring Security (JWT)
  - OpenFeign
- H2 (or your configured DB) for development/testing
- Maven for build
- JUnit 5, Spring Test, Mockito for testing

## Project Structure
- src/main/java/com/ecom/cartservice
  - controllers/ CartController.java
  - services/ CartService.java, CartServiceImpl.java
  - repositories/ CartRepository.java, CartItemRepository.java
  - models/ Cart.java, CartItem.java, BaseClass.java
  - dtos/ CartItemDto.java, CartResponseDTO.java, ProductDTO.java
  - configuration/ ApplicationConfiguration.java, FeignConfig.java, ProductClient.java
  - security/ SecurityConfig.java, JwtService.java, JwtAuthenticationFilter.java, JwtProperties.java
  - controlleradvice/ GlobalExceptionHandler.java
  - exceptions/ EmptyCartException.java, CartNotFoundException.java, ProductNotInCartException.java
  - utils/ AuthUtils.java
  - CartServiceApplication.java
- src/test/java/com/ecom/cartservice
  - contollers/ CartControllerTest.java
  - service/ CartServiceImplTest.java
  - repository/ CartRepositoryTest.java, CartItemRepositoryTest.java

Note: The test package for controllers is named "contollers" (typo) to match current directory structure.

## Configuration
Application properties are in:
- src/main/resources/application.properties

Key configurations typically include:
- Server port
- Datasource (H2/Postgres/etc.)
- JPA/Hibernate
- JWT properties (secret, expiration)
- Product service base URL for Feign client

Example (adjust to your environment):

server.port=8081
spring.datasource.url=jdbc:h2:mem:cartdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# JWT
cart.jwt.secret=change-me
cart.jwt.expiration=3600000

# Product service (Feign)
product.service.url=http://localhost:8082

Placeholders above may not reflect your exact configuration. Use the real keys present in JwtProperties and Feign/Product client configuration if they differ.

## Prerequisites
- JDK 17+
- Maven 3.9+

Verify versions:
- java -version
- mvn -v

## Build
- mvn -q -DskipTests clean package

This produces a jar at target/cartservice-<version>.jar

## Run
- mvn spring-boot:run

Or run the jar:
- java -jar target/cartservice-<version>.jar

If using H2 in-memory DB, data resets on restart.

## API Overview
Controller: CartController exposes REST endpoints such as:
- POST /api/v1/cart/items — add item to current user's cart
- PATCH /api/v1/cart/items/{productId} — update quantity
- DELETE /api/v1/cart/items/{productId} — remove item
- GET /api/v1/cart — fetch current cart summary

Consult CartController.java for exact paths, request/response models (CartItemDto, CartResponseDTO), and status codes. GlobalExceptionHandler maps domain exceptions to appropriate HTTP responses.

## Security
- JWT-based authentication with a filter (JwtAuthenticationFilter) configured in SecurityConfig
- JwtService handles token generation/validation
- For local testing of controllers, tests can disable filters using:
  @AutoConfigureMockMvc(addFilters = false)
This mirrors the snippet present in CartControllerTest.java and allows hitting endpoints in tests without JWT setup.

## Tests
Run all tests:
- mvn -q test

Layers covered:
- Controller tests with MockMvc (CartControllerTest)
- Service tests with Mockito (CartServiceImplTest)
- Repository tests leveraging Spring Data JPA test slice (CartRepositoryTest, CartItemRepositoryTest)

Tip: If security interferes with controller tests, ensure @AutoConfigureMockMvc(addFilters = false) is present to disable security filters for the test slice.

## Error Handling
Custom exceptions:
- CartNotFoundException
- EmptyCartException
- ProductNotInCartException

GlobalExceptionHandler translates these to structured error responses with appropriate HTTP status codes.

## External Integrations
- ProductClient (OpenFeign) fetches product details from Product service. Feign configuration lives in FeignConfig and ApplicationConfiguration. Ensure the Product service URL is configured and reachable.

## Development Notes
- AuthUtils may extract the authenticated user details (subject) from SecurityContext/JWT; service methods rely on the current user when manipulating carts.
- Entities extend BaseClass for common fields (e.g., id, timestamps).
- Keep DTOs free of JPA annotations; use models for persistence, DTOs for transport.

## Common Commands
- Formatting/cleanup: mvn -q -DskipTests clean verify
- Run with a specific profile: mvn spring-boot:run -Dspring-boot.run.profiles=dev
- Show dependency tree: mvn -q dependency:tree

## Troubleshooting
- 401/403 when calling endpoints: ensure Authorization header contains a valid JWT or disable filters in local tests.
- Feign client errors: verify product.service.url and that the Product service is running.
- DB issues on startup: check datasource URL/driver and JPA ddl-auto. For H2 in-memory, ensure the URL includes DB_CLOSE_DELAY=-1 to keep the DB alive during the JVM session.

## License
Add your project license here.

## Maintainers
- Team CartService

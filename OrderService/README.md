# OrderService

OrderService is a Spring Boot microservice responsible for managing orders in an e-commerce system. It integrates with external services (Cart, User, Payment) via OpenFeign and secures endpoints using JWT-based authentication. Kafka consumer integration is provided for order-related events.

## Features
- Create and fetch orders
- Order and order-item persistence via Spring Data JPA
- DTO mapping layer for request/response separation
- Global exception handling with descriptive error responses
- Security using Spring Security + JWT filter
- External integrations using OpenFeign:
  - CartClient
  - UserClient
  - PaymentClient
- Kafka consumer for order events
- Unit and slice tests for controllers, services, and repositories

## Tech Stack
- Java 17+
- Spring Boot 3+
- Spring Web, Validation, Security
- Spring Data JPA, Hibernate
- OpenFeign
- Kafka (consumer)
- JUnit 5, Mockito
- Maven

## Getting Started

### Prerequisites
- JDK 17 or newer
- Maven 3.8+
- Running instances or reachable endpoints for dependent services (Cart, User, Payment) if you intend to call them
- Kafka broker (if you want to test Kafka consumption)

### Clone and Build
```
git clone <repo-url>
cd OrderService
./mvnw clean install
```

### Run
You can run the application using Maven or your IDE.
```
./mvnw spring-boot:run
```
Alternatively, build a jar and run it:
```
./mvnw clean package
java -jar target/orderservice-*.jar
```

### Configuration
All configuration is located in `src/main/resources/application.properties`. Key properties include:
- Server and datasource configuration
- JWT settings (see `security.JwtProperties`)
- Feign client configuration (see `configuration.FeignConfig`)
- Kafka consumer configuration (see `KafkaConfig.OrderConsumer` and application properties)

Update these according to your environment. Typical properties you may need to provide:
- spring.datasource.url, username, password
- spring.jpa.hibernate.ddl-auto
- jwt.secret, jwt.expiration (if applicable)
- feign timeouts and base URLs for Cart/User/Payment services
- spring.kafka.bootstrap-servers

### Security
JWT authentication is implemented via:
- `security.JwtAuthenticationFilter`
- `security.SecurityConfig`
- `security.JwtService`

Attach a valid `Authorization: Bearer <token>` header when accessing secured endpoints.

## API Overview
Controller class: `controllers.OrderController`

Common endpoints typically include:
- POST /api/orders — create a new order from the user cart
- GET /api/orders/{id} — fetch order by id
- GET /api/orders — list current user orders

Request/Response DTOs:
- Requests: `dtos.OrderRequestDTO`, `dtos.PaymentRequestDto`
- Responses: `dtos.OrderResponseDTO`, `dtos.PaymentResponseDTO`, `dtos.CartResponseDTO`, `dtos.UserResponseDto`

Mapper:
- `mappers.OrderMapper` handles mapping between entities and DTOs.

Entities:
- `models.Order`, `models.OrderItem`, `models.OrderStatus`, `models.PaymentStatus`

Repositories:
- `repositories.OrderRepository`, `repositories.OrderItemRepository`

Service:
- `services.OrderService`, `services.OrderServiceImpl`

Global Exception Handling:
- `controlleradvice.GlobalExceptionHandler`
- Custom exceptions under `exceptions/` (e.g., `EmptyCartException`, `ProductNotFoundException`)

Kafka:
- `KafkaConfig.OrderConsumer` for consuming messages and reacting to order events.

OpenFeign configuration and clients:
- `configuration.FeignConfig`
- `configuration.CartClient`
- `configuration.UserClient`
- `configuration.PaymentClient`

## Running Tests
Run the full test suite:
```
./mvnw test
```

Tests include:
- Controller tests: `src/test/java/.../controllers/OrderControllerTest.java`
- Service tests: `src/test/java/.../service/OrderServiceImplTest.java`
- Repository tests: `src/test/java/.../repositories/*`

## Local Development Tips
- Use an in-memory DB (e.g., H2) for quick local testing by setting `spring.datasource.*` properties accordingly.
- If using Kafka locally, consider Docker Compose to run a local broker.
- For Feign clients, you can stub dependent services or use WireMock during development and testing.

## Project Structure (high level)
```
src/main/java/com/ecom/orderservice
  ├── controllers
  ├── controlleradvice
  ├── configuration
  ├── dtos
  ├── exceptions
  ├── KafkaConfig
  ├── mappers
  ├── models
  ├── repositories
  ├── security
  ├── services
  ├── utils
  └── OrderServiceApplication.java
```

## Building for Production
- Ensure proper database configuration and migrations (Flyway/Liquibase recommended though not included here).
- Provide strong JWT secrets and rotate regularly.
- Configure Feign client timeouts and retries appropriately.
- Tune Kafka consumer settings for your throughput and reliability targets.

## Troubleshooting
- 401/403 errors: verify JWT token, header format, and security rules in `SecurityConfig`.
- 5xx on order creation: check Cart/User/Payment service endpoints and Feign configuration.
- Persistence issues: validate datasource URL, credentials, and JPA settings.
- Kafka not consuming: verify `spring.kafka.bootstrap-servers` and topic/subscription configuration.

## License
This project is provided as-is. Add a license here if required (e.g., MIT, Apache-2.0).

# PaymentService

PaymentService is a Spring Boot microservice that handles payment initiation and processing for an e-commerce platform. It integrates with multiple payment gateways (e.g., Stripe, Razorpay), publishes payment events to Kafka, and persists payment records.

## Features
- RESTful API for initiating and confirming payments
- Pluggable payment gateways (Stripe, Razorpay)
- JWT-based authentication for protected endpoints
- Kafka producer to emit payment events
- Persistence of payment records and statuses
- Global exception handling and structured error responses
- Unit and slice tests for controller, service, and repository layers

## Tech Stack
- Java 17 (or project-specified in pom.xml)
- Spring Boot (Web, Security)
- Spring Data JPA
- Kafka (Producer)
- JWT for authentication
- Maven for build and dependency management

## Project Structure
```
src/
  main/
    java/com/ecom/paymentservice/
      controller/              # REST controllers
      controlleradvice/        # Global exception handler
      dto/                     # Request/Response DTOs
      Gateway/                 # Payment gateway abstraction + implementations
      KafkaConfig/             # Kafka producer
      models/                  # JPA entities and enums
      repositories/            # Spring Data repositories
      security/                # JWT config and filters
      service/                 # Service interfaces/impls
      utils/                   # Utility classes (e.g., auth)
      PaymentServiceApplication.java
    resources/
      application.properties   # Configuration
  test/
    java/com/ecom/paymentservice/
      controller/              # Controller tests
      repository/              # Repository tests
      service/                 # Service tests
      PaymentServiceApplicationTests.java
```

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+
- Kafka broker (optional for local dev; disable or mock if not available)
- A relational database supported by Spring Data JPA. H2 can be used for local/testing or configure your own (e.g., PostgreSQL/MySQL) via application.properties

### Configuration
Edit `src/main/resources/application.properties` to set the following (keys may already exist):

- Server and application:
  - server.port

- Database (choose one):
  - spring.datasource.url
  - spring.datasource.username
  - spring.datasource.password
  - spring.jpa.hibernate.ddl-auto

- Kafka:
  - spring.kafka.bootstrap-servers
  - payment.kafka.topic

- JWT:
  - jwt.secret
  - jwt.expiration

- Payment gateways:
  - stripe.api.key
  - razorpay.api.key
  - razorpay.api.secret

Provide real values or environment overrides for production deployments.

### Build
```
./mvnw clean package
```

### Run
```
./mvnw spring-boot:run
```
Or run the packaged jar:
```
java -jar target/paymentservice-*.jar
```

## API Overview

Base path: `/api/payments`

- POST `/initiate`
  - Description: Initiate a payment with the selected gateway.
  - Auth: Bearer token (JWT) if security is enabled.
  - Body: PaymentRequestDto or InitiatePaymentRequestDto
  - Response: PaymentResponseDTO

- POST `/confirm/{paymentId}` (if applicable)
  - Description: Confirm or capture a previously initiated payment.
  - Auth: Bearer token
  - Response: Updated PaymentResponseDTO

- GET `/{paymentId}`
  - Description: Fetch payment details and status.
  - Auth: Bearer token
  - Response: PaymentResponseDTO

- Additional endpoints may exist (see `PaymentController`).

Refer to controller and DTO classes for exact schemas:
- `controller/PaymentController.java`
- `dto/PaymentRequestDto.java`
- `dto/InitiatePaymentRequestDto.java`
- `dto/PaymentResponseDTO.java`

## Security
- JWT-based authentication is configured via `security/` package.
- Set `jwt.secret` and `jwt.expiration` in application properties.
- `JwtAuthenticationFilter` extracts and validates tokens on incoming requests.

## Gateways
- Abstraction: `Gateway/PaymentGateway.java`
- Implementations: `Gateway/StripeGateway.java`, `Gateway/RazorPayGateway.java`
- External clients: `PaymentGatewayClients/StripeClient.java`, `PaymentGatewayClients/RazorPayClient.java`
- Switch gateway based on request payload or configuration. Ensure API keys are configured in properties or environment variables.

## Events
- Kafka producer publishes payment lifecycle events using `KafkaConfig/PaymentProducer.java`.
- Configure `spring.kafka.bootstrap-servers` and topic name `payment.kafka.topic`.

## Persistence
- Entity: `models/Payment.java`
- Enum: `models/PaymentStatus.java`
- Repository: `repositories/PaymentRepostiory.java`

Note: The repository class name contains a likely typo (`PaymentRepostiory`). Be consistent when referencing it or consider renaming in a future refactor.

## Error Handling
- Centralized exception handling in `controlleradvice/GlobalExceptionHandler.java`.
- Domain exceptions like `Exceptions/PaymentException.java` map to proper HTTP responses.

## Testing
- Run all tests:
```
./mvnw test
```
- Notable tests:
  - `test/java/com/ecom/paymentservice/controller/PaymentControllerTest.java`
  - `test/java/com/ecom/paymentservice/service/PaymentServiceImplTest.java`
  - `test/java/com/ecom/paymentservice/repository/PaymentRepositoryTest.java`

## Local Development Tips
- Use H2 in-memory DB for quick iteration:
  - Add to properties: `spring.datasource.url=jdbc:h2:mem:paydb;DB_CLOSE_DELAY=-1` and `spring.jpa.hibernate.ddl-auto=update`
- Disable security during early development by permitting all in `SecurityConfig` (only locally).
- If Kafka is unavailable, guard producer calls or use a local Kafka via Docker.

## Environment Variables Example
```
export SERVER_PORT=8082
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/payments
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export SPRING_JPA_HIBERNATE_DDL_AUTO=update
export SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export PAYMENT_KAFKA_TOPIC=payment-events
export JWT_SECRET=replace-with-strong-secret
export JWT_EXPIRATION=3600000
export STRIPE_API_KEY=sk_live_...
export RAZORPAY_API_KEY=rzp_test_...
export RAZORPAY_API_SECRET=...
```

## Build and Run with Profiles
You can externalize environment-specific configs using Spring profiles:
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
java -jar target/paymentservice-*.jar --spring.profiles.active=prod
```

## Contribution Guidelines
- Follow standard Spring naming conventions and package structure
- Write unit tests for new logic and keep coverage reasonable
- Keep controller lean; put business logic into services
- Prefer constructor injection over field injection
- Handle exceptions through `GlobalExceptionHandler`
- Validate request DTOs using Bean Validation where applicable

## License
This project is currently unlicensed. Add a LICENSE file if you intend to open source it.

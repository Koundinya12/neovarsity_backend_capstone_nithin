# Ecommerce Monorepo

This repository contains all the microservices that make up the Ecommerce platform. It follows a Spring Boot / Spring Cloud microservices architecture, with a central service registry, an API gateway as the single entry point, and independent domain services communicating over REST (OpenFeign) and Kafka.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Repository Structure](#repository-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Inter-Service Communication](#inter-service-communication)
- [Security](#security)
- [Testing](#testing)
- [Docker](#docker)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Architecture Overview

Requests from clients enter through the **API Gateway**, which routes them to the appropriate downstream microservice. Services register themselves with, and discover one another through, the **Service Discovery** (Eureka) server. Synchronous service-to-service calls use **OpenFeign**; asynchronous, event-driven communication (e.g., order and payment events) uses **Kafka**. Authentication across services is handled with **JWT** bearer tokens.

```
                         ┌──────────────────┐
                         │  Service Discovery │  (Eureka, :8761)
                         └─────────▲──────────┘
                                   │ register/discover
                                   │
   Client ──▶ API Gateway (:8080) ─┼──▶ AuthService
                                   ├──▶ UserService
                                   ├──▶ ProductService
                                   ├──▶ CartService ───▶ ProductService (Feign)
                                   ├──▶ OrderService ──▶ CartService, UserService, PaymentService (Feign)
                                   │                 └─▶ Kafka (consumer)
                                   └──▶ PaymentService ─▶ Stripe / Razorpay
                                                       └─▶ Kafka (producer)
```

## Services

| Service | Purpose | Default Port | Key Dependencies |
|---|---|---|---|
| [`ServiceDiscovery`](#servicediscovery) | Eureka registry for service discovery | 8761 | — |
| [`APIGatewayService`](#apigatewayservice) | Single entry point; routes to downstream services | 8080 | ServiceDiscovery |
| [`AuthService`](#authservice) | Registration, login, JWT issuance | 8080* | Spring Data JPA |
| [`UserService`](#userservice) | User management (CRUD, roles) | 8080* | Spring Data JPA, JWT |
| [`ProductService`](#productservice) | Product & category CRUD | 8080* | Spring Data JPA, JWT |
| [`CartService`](#cartservice) | Shopping cart management | 8081 | ProductService (Feign), JWT |
| [`OrderService`](#orderservice) | Order creation and retrieval | 8083* | CartService, UserService, PaymentService (Feign), Kafka (consumer) |
| [`PaymentService`](#paymentservice) | Payment initiation via Stripe/Razorpay | 8082 | Kafka (producer), JWT |

\* Several services default to Spring Boot's standard port 8080. When running more than one locally at the same time, override `server.port` per service (see [Configuration](#configuration)) so they don't collide.

### ServiceDiscovery
Netflix Eureka server. All other services register with it on startup; it does not register or fetch a registry itself. Exposes the Eureka dashboard at `http://localhost:8761/`.

### APIGatewayService
Spring Boot API Gateway and single entry point for external traffic. Applies cross-cutting concerns (routing, CORS, resiliency) and forwards requests to the registered downstream services.

### AuthService
Handles registration and login, issuing signed JWT access tokens. Endpoints are exposed under `/api/auth` (`/register`, `/login`).

### UserService
Manages user records, roles and addresses. Exposes registration/login endpoints alongside secured CRUD endpoints for users under `/api/v1/users`.

### ProductService
Manages products and categories with full CRUD under `/api/categories` and `/api/products`.

### CartService
Manages per-user shopping carts: add/update/remove items, view contents, compute totals. Calls ProductService via Feign (`ProductClient`) to validate products and fetch pricing. Endpoints live under `/api/v1/cart`.

### OrderService
Creates and retrieves orders, orchestrating calls to CartService, UserService and PaymentService via Feign, and consumes order-related Kafka events. Endpoints live under `/api/orders`.

### PaymentService
Initiates and confirms payments through pluggable gateways (Stripe, Razorpay) and publishes payment lifecycle events to Kafka. Endpoints live under `/api/payments`.

## Tech Stack

Common across services:
- **Java 17+**
- **Spring Boot 3.x** (Web, Validation, Security)
- **Spring Data JPA** / Hibernate, with H2 for local development and Postgres/MySQL for other environments
- **Maven**, with the wrapper (`./mvnw` / `mvnw.cmd`) checked into each service so a local Maven install isn't required
- **JUnit 5** and **Mockito** for testing

Service-specific additions:
- **Spring Cloud Netflix Eureka** — ServiceDiscovery (server) and client registration in every other service
- **OpenFeign** — CartService, OrderService (calls to Cart/User/Payment), AuthService (Feign scaffold)
- **Apache Kafka** — OrderService (consumer), PaymentService (producer)
- **JWT** (JJWT or equivalent) — AuthService, UserService, ProductService, CartService, OrderService, PaymentService
- **Stripe / Razorpay SDKs** — PaymentService gateway integrations

## Repository Structure

```
.
├── ServiceDiscovery/
├── APIGatewayService/
├── AuthService/
├── UserService/
├── ProductService/
├── CartService/
├── OrderService/
└── PaymentService/
```

Each service is a self-contained Maven project with its own `pom.xml`, Maven wrapper, `src/main` and `src/test` trees, and `application.properties`. There is no shared parent POM or shared code module today — each service manages its own dependency versions independently.

## Prerequisites
- JDK 17 or later
- Maven 3.8+ (optional — each service bundles the Maven wrapper)
- A relational database if not using the default H2 in-memory database (PostgreSQL/MySQL)
- A running Kafka broker if you want to exercise OrderService/PaymentService event flows
- Network access to Stripe/Razorpay if testing real payment gateway calls

## Getting Started

Because services discover each other through Eureka and the gateway routes by service name, bring the platform up in this order:

1. **Start ServiceDiscovery first**
   ```
   cd ServiceDiscovery
   ./mvnw spring-boot:run
   ```
   Confirm it's up at `http://localhost:8761/`.

2. **Start the domain services** (any order), each from its own directory:
   ```
   cd AuthService && ./mvnw spring-boot:run
   cd UserService && ./mvnw spring-boot:run
   cd ProductService && ./mvnw spring-boot:run
   cd CartService && ./mvnw spring-boot:run
   cd PaymentService && ./mvnw spring-boot:run
   cd OrderService && ./mvnw spring-boot:run
   ```
   Override `server.port` for any service that would otherwise collide on 8080, e.g.:
   ```
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8084"
   ```

3. **Start APIGatewayService last**, once downstream services are registered:
   ```
   cd APIGatewayService
   ./mvnw spring-boot:run
   ```

4. Verify registration on the Eureka dashboard (`http://localhost:8761/`) before sending traffic through the gateway.

To build any individual service into a runnable jar:
```
cd <ServiceName>
./mvnw clean package
java -jar target/<servicename>-*.jar
```

## Configuration

Each service is configured independently via its own `src/main/resources/application.properties`, overridable with environment variables, `-D` system properties, or Spring profiles (`--spring.profiles.active=<profile>`). Commonly configured properties across services include:

- `server.port` — HTTP port for the service
- `spring.datasource.*` / `spring.jpa.hibernate.ddl-auto` — database connection and schema handling
- `jwt.secret` / `jwt.expiration` (naming varies slightly by service, e.g. `cart.jwt.secret`) — token signing and lifetime
- `spring.kafka.bootstrap-servers` — Kafka broker address (OrderService, PaymentService)
- `eureka.client.service-url.defaultZone` — Eureka server URL for client registration
- `stripe.api.key`, `razorpay.api.key`, `razorpay.api.secret` — PaymentService gateway credentials

Use a strong, unique JWT secret per environment and never commit real credentials — prefer environment variables or a secrets manager for production.

## Inter-Service Communication

- **Synchronous (REST via OpenFeign):** CartService → ProductService; OrderService → CartService, UserService, PaymentService; AuthService ships a Feign client scaffold for future use.
- **Asynchronous (Kafka):** PaymentService publishes payment lifecycle events; OrderService consumes order-related events. A local broker can be run via Docker Compose for development.
- **Service discovery:** every service other than ServiceDiscovery itself registers with Eureka and resolves peers by application name rather than hardcoded URLs where Feign clients are Eureka-aware.

## Security

Authentication is JWT-based and consistent across services: clients authenticate via AuthService (or a service's own `/auth` endpoints) to obtain a bearer token, then send `Authorization: Bearer <token>` on subsequent requests. Each service validates tokens locally via its own `JwtService`/`JwtAuthenticationFilter`, so the JWT secret used to sign tokens must be consistent wherever tokens need to be verified across service boundaries.

## Testing

Run the test suite for an individual service from its own directory:
```
cd <ServiceName>
./mvnw test
```
Each service includes controller (MockMvc), service (Mockito), and repository (Spring Data JPA test slice) tests. There is currently no aggregated command to run tests across all services at once — run them per-service, or script a loop over the service directories if needed.

## Docker

None of the services ship a Dockerfile by default, but each can be containerized with a minimal image such as:
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/<service-jar>.jar app.jar
EXPOSE <service-port>
ENTRYPOINT ["java","-jar","/app/app.jar"]
```
There is no repository-wide `docker-compose.yml` today; if you containerize the platform, ensure the container network lets services reach ServiceDiscovery and each other, and that Kafka/database dependencies are reachable from the containers.

## Troubleshooting
- **Service not visible in Eureka:** confirm `eureka.client.service-url.defaultZone` points at the running ServiceDiscovery instance and that the service started without errors.
- **Port conflicts:** several services default to 8080 — override `server.port` per service when running multiple locally.
- **401/403 from a downstream service:** verify the JWT was issued with a secret that the receiving service is also configured with, and check the `Authorization` header format.
- **Feign calls failing:** ensure the target service is registered with Eureka (or its base URL is correctly configured) and reachable on its configured port.
- **Kafka errors:** confirm `spring.kafka.bootstrap-servers` and topic names match between PaymentService (producer) and OrderService (consumer), and that a broker is actually running.

## Contributing
- Keep controllers thin; put business logic in services.
- Use DTOs for request/response payloads and mappers to convert to/from entities — don't leak JPA entities through the API.
- Prefer constructor injection over field injection.
- Route errors through each service's `GlobalExceptionHandler` rather than ad-hoc try/catch in controllers.
- Add or update tests alongside any new controller/service/repository logic.


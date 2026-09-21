# API Gateway Service

A Spring Boot API Gateway for the e-commerce system. This service acts as the single entry point, routing external requests to internal microservices and applying cross-cutting concerns such as security, CORS, and resiliency.

## Features
- Centralized routing to downstream services
- Environment-driven configuration
- Production-ready build via Maven wrapper
- Container-friendly (profiles and externalized config)

## Tech Stack
- Java 17+ (configurable via Maven)
- Spring Boot
- Maven (mvnw wrapper included)

## Project Structure
```
APIGatewayService/
├─ src/
│  ├─ main/
│  │  ├─ java/com/ecom/apigateway/ApiGatewayServiceApplication.java
│  │  └─ resources/application.properties
│  └─ test/
│     └─ java/.../ApiGatewayServiceApplicationTests.java
├─ pom.xml
├─ mvnw, mvnw.cmd
└─ .mvn/wrapper
```

## Prerequisites
- JDK 17 or higher installed
- Internet access to download Maven dependencies

Note: You can use the included Maven wrapper (`./mvnw`) without installing Maven globally.

## Configuration
All configuration is externalized via `src/main/resources/application.properties` and environment variables.

Default application properties:
- `server.port` — HTTP port the gateway will bind to (defaults to 8080 if not set)
- `spring.application.name` — Service name used for discovery/metrics

Override properties by:
- Setting environment variables (e.g., `SERVER_PORT=9090`)
- Creating an `application-{profile}.properties` and running with `--spring.profiles.active={profile}`
- Supplying `-D` system properties (e.g., `-Dserver.port=9090`)

## Build
Using Maven wrapper:
- Clean and build (run tests):
  - macOS/Linux: `./mvnw clean package`
  - Windows (PowerShell/CMD): `mvnw.cmd clean package`

Artifacts:
- `target/*.jar` — executable Spring Boot fat jar

## Run
Run from source:
- macOS/Linux: `./mvnw spring-boot:run`
- Windows: `mvnw.cmd spring-boot:run`

Run the built jar:
- `java -jar target/apigatewayservice-*.jar`

Change port:
- `SERVER_PORT=9090 ./mvnw spring-boot:run`
- or `java -jar target/apigatewayservice-*.jar --server.port=9090`

Activate profile:
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- or `java -jar target/apigatewayservice-*.jar --spring.profiles.active=dev`

## Development
- Format/validate: `./mvnw verify`
- Run tests: `./mvnw test`
- Skip tests: `./mvnw clean package -DskipTests`

## Observability & Health
Spring Boot actuator can be enabled to expose health and metrics. Add to `pom.xml` and configure if needed:
- Dependency: `spring-boot-starter-actuator`
- Properties: `management.endpoints.web.exposure.include=health,info,metrics,prometheus`
- Health: `GET /actuator/health`

## Docker (optional)
A minimal Dockerfile example if you choose to containerize:
```
# Dockerfile
FROM eclipse-temurin:17-jre
ARG JAR=target/*.jar
COPY ${JAR} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```
Build and run:
- `./mvnw clean package -DskipTests`
- `docker build -t apigateway:latest .`
- `docker run --rm -p 8080:8080 apigateway:latest`

## Troubleshooting
- Port already in use: change `server.port` or stop the conflicting process
- Dependency download issues: check network/proxy; try `./mvnw -U clean package`
- Memory errors during build: `MAVEN_OPTS="-Xmx1G" ./mvnw clean package`

## License
This project is provided as-is. Add your license information here if required.

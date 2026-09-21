# ServiceDiscovery

A Spring Boot Netflix Eureka Server for service discovery in a microservices architecture. This application acts as a central registry where services register themselves and discover other services.

## Features
- Eureka Server (service registry)
- Spring Boot application with Maven build
- Profiles for local development

## Tech Stack
- Java 17+ (adjust to your installed JDK)
- Spring Boot
- Netflix Eureka Server
- Maven

## Project Structure
- src/main/java/.../ServiceDiscoveryApplication.java: Main Spring Boot application class
- src/main/resources/application.properties: Application configuration (Eureka server, ports, etc.)
- pom.xml: Maven configuration and dependencies

## Prerequisites
- JDK 17 or later installed
- Maven 3.8+ (or use the provided Maven wrapper `mvnw`/`mvnw.cmd`)

Verify versions:
- `java -version`
- `./mvnw -v`

## Configuration
Configuration is managed via `src/main/resources/application.properties`. Key properties typically include:
- server.port: Port where the Eureka server runs
- spring.application.name: Application name (e.g., `service-discovery`)
- eureka.client.register-with-eureka=false (server should not register itself)
- eureka.client.fetch-registry=false

Adjust these as needed for your environment.

## Build
Using Maven Wrapper (recommended):
- Unix/macOS: `./mvnw clean package`
- Windows: `mvnw.cmd clean package`

This produces an executable jar in `target/`.

## Run
Using Maven:
- `./mvnw spring-boot:run`

Or using the jar:
- `java -jar target/ServiceDiscovery-0.0.1-SNAPSHOT.jar`

To specify a different port or profile:
- `./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8761 --spring.profiles.active=local"`

## Access
Once running, open the Eureka dashboard:
- http://localhost:8761/

You should see the Eureka status page and a list of registered instances (empty initially).

## Registering Clients
Client services must include the Eureka Client dependency and point to this server. Typical Spring Boot client config:
- Dependency: `spring-cloud-starter-netflix-eureka-client`
- application.properties examples:
  - `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/`
  - `spring.application.name=<your-service-name>`

Ensure network access from clients to the server host/port.

## Health and Actuator
If using Spring Boot Actuator, health endpoints can be enabled to help Eureka determine service availability. Configure in the client services as needed.

## Testing
Run tests:
- `./mvnw test`

## Common Issues
- Port already in use: Change `server.port` or stop the conflicting process.
- Clients not showing up: Verify `defaultZone` URL, network connectivity, and that the client is running with Eureka Client enabled.
- Self-preservation mode: Eureka may delay removing instances; adjust Eureka server settings if needed for dev/testing.

## Packaging and Deployment
- Build the jar and run as a system service or containerize.
- For Docker, expose the Eureka port (e.g., 8761) and configure clients to reach the container host.

Example Dockerfile (outline):
```
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/ServiceDiscovery-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

## Versioning
Update the artifact version in `pom.xml` as needed. The generated jar name in the Run section should match your actual version.

## License
This project is provided as-is. Add your license information here if applicable.

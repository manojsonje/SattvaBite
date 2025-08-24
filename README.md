# SattvaBite - Restaurant Management System

SattvaBite is a microservices-based restaurant management system built with Spring Boot and Maven.

## Project Structure

This is a multi-module Maven project with the following modules:

1. **order-service**: Handles order processing and management
2. **food-catalog-service**: Manages food items and menu
3. **restaurant-service**: Manages restaurant information
4. **api-gateway**: API Gateway for routing requests
5. **service-registry**: Service discovery with Eureka
6. **food-delivery-frontend**: Frontend application

## Prerequisites

- Java 11 or later
- Maven 3.6.0 or later
- MongoDB (for order-service)
- MySQL (for food-catalog-service and restaurant-service)

## Building the Project

To build the entire project, run the following command from the root directory:

```bash
mvn clean install
```

To build a specific module, navigate to the module directory and run:

```bash
mvn clean install
```

## Running the Services

Each service can be run independently using Spring Boot Maven plugin:

```bash
# From the root directory
mvn spring-boot:run -pl <module-name>

# Example:
# mvn spring-boot:run -pl order-service
```

Or you can run the generated JAR files:

```bash
java -jar <module-name>/target/<module-name>-1.0.0-SNAPSHOT.jar
```

## Configuration

Each service has its own configuration in `src/main/resources/application.yml`. Update the database connections and other properties as needed.

## API Documentation

API documentation is available at:
- Order Service: http://localhost:8082/swagger-ui.html
- Food Catalogue Service: http://localhost:8081/swagger-ui.html
- Restaurant Listing Service: http://localhost:8080/swagger-ui.html

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

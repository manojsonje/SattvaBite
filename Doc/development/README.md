# Development Guide

Welcome to the SattvaBite development guide! This document will help you set up your development environment and understand our development workflow.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Project Structure](#project-structure)
3. [Coding Standards](#coding-standards)
4. [Testing](#testing)
5. [Debugging](#debugging)
6. [API Development](#api-development)
7. [Database Migrations](#database-migrations)
8. [Code Review Process](#code-review-process)
9. [Troubleshooting](#troubleshooting)
10. [FAQs](#faqs)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker 20.10+
- Node.js 16+ (for frontend development)
- IntelliJ IDEA (recommended) or VS Code

### Setting Up the Development Environment

1. **Clone the repository**
   ```bash
   git clone https://github.com/sattvabite/sattvabite.git
   cd sattvabite
   ```

2. **Set up pre-commit hooks**
   ```bash
   # Install pre-commit
   pip install pre-commit
   
   # Set up git hooks
   pre-commit install
   ```

3. **Start development services**
   ```bash
   docker-compose -f deployment/docker-compose.dev.yml up -d
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   # Run order service
   cd order-service
   mvn spring-boot:run
   ```

## Project Structure

```
sattvabite/
├── api-gateway/             # API Gateway service
├── order-service/           # Order management service
├── food-catalogue-service/  # Food catalog service
├── restaurant-service/      # Restaurant management service
├── service-registry/        # Service discovery
├── deployment/              # Deployment configurations
│   ├── docker/              # Docker configurations
│   ├── k8s/                 # Kubernetes manifests
│   └── helm/                # Helm charts
├── docs/                    # Documentation
└── scripts/                 # Utility scripts
```

## Coding Standards

### Java

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Always use `@Override` annotation when applicable
- Use `final` for method parameters and local variables when they shouldn't be reassigned

### Naming Conventions

- **Classes**: `PascalCase` (e.g., `OrderService`)
- **Methods**: `camelCase` (e.g., `createOrder`)
- **Variables**: `camelCase` (e.g., `orderId`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`)
- **Packages**: `lowercase` (e.g., `com.sattvabite.orders`)

### Documentation

- Document all public APIs using JavaDoc
- Include `@param`, `@return`, and `@throws` in method documentation
- Document complex business logic with inline comments
- Update the README.md when adding new features

## Testing

### Unit Tests

- Place test classes in `src/test/java`
- Use JUnit 5 and AssertJ for assertions
- Follow the naming convention: `{ClassName}Test`
- Use `@DisplayName` for descriptive test names

Example:
```java
@DisplayName("Order Service Unit Tests")
class OrderServiceTest {
    
    @Test
    @DisplayName("Should create order with valid input")
    void shouldCreateOrderWithValidInput() {
        // Test implementation
    }
}
```

### Integration Tests

- Place integration tests in `src/test/java` with `@SpringBootTest`
- Use `@Testcontainers` for database tests
- Use `@DirtiesContext` when modifying the application context

### Test Coverage

- Aim for at least 80% code coverage
- Run coverage reports with:
  ```bash
  mvn clean test jacoco:report
  ```
- View the report at `target/site/jacoco/index.html`

## Debugging

### Remote Debugging

1. Start the application in debug mode:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
   ```

2. In IntelliJ:
   - Go to `Run` > `Edit Configurations`
   - Add a new `Remote JVM Debug` configuration
   - Set port to `5005`
   - Apply and start debugging

### Logging

- Use SLF4J for logging
- Follow the logging levels:
  - `ERROR`: System is in distress, immediate attention needed
  - `WARN`: Not an error, but indicates a potential issue
  - `INFO`: Important business process has finished
  - `DEBUG`: Detailed information for debugging
  - `TRACE`: Very detailed debugging information

Example:
```java
private static final Logger log = LoggerFactory.getLogger(OrderService.class);

public void processOrder(Order order) {
    log.info("Processing order: {}", order.getId());
    try {
        // Process order
    } catch (Exception e) {
        log.error("Failed to process order: {}", order.getId(), e);
        throw e;
    }
}
```

## API Development

### Adding a New Endpoint

1. Create a new controller or add to an existing one:
   ```java
   @RestController
   @RequestMapping("/api/v1/orders")
   @RequiredArgsConstructor
   public class OrderController {
       
       private final OrderService orderService;
       
       @PostMapping
       @ResponseStatus(HttpStatus.CREATED)
       public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
           return orderService.createOrder(request);
       }
   }
   ```

2. Create DTOs for request/response:
   ```java
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   public class CreateOrderRequest {
       @NotBlank
       private String restaurantId;
       
       @Valid
       @NotEmpty
       private List<OrderItemDto> items;
   }
   ```

3. Document the endpoint with Swagger/OpenAPI:
   ```java
   @Operation(
       summary = "Create a new order",
       description = "Creates a new food order with the given items"
   )
   @ApiResponses({
       @ApiResponse(responseCode = "201", description = "Order created successfully"),
       @ApiResponse(responseCode = "400", description = "Invalid input")
   })
   @PostMapping
   public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
       // Implementation
   }
   ```

## Database Migrations

### Using Flyway

1. Create a new migration file in `src/main/resources/db/migration`:
   ```
   V1__create_orders_table.sql
   V2__add_order_status_index.sql
   ```

2. Write SQL for the migration:
   ```sql
   -- V1__create_orders_table.sql
   CREATE TABLE orders (
       id BIGSERIAL PRIMARY KEY,
       user_id BIGINT NOT NULL,
       restaurant_id BIGINT NOT NULL,
       status VARCHAR(50) NOT NULL,
       created_at TIMESTAMP NOT NULL,
       updated_at TIMESTAMP NOT NULL
   );
   ```

3. The migration will run automatically on application startup

## Code Review Process

1. Create a feature branch from `develop`
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make your changes and commit with a descriptive message:
   ```bash
   git add .
   git commit -m "Add feature to process orders asynchronously"
   ```

3. Push your changes and create a pull request:
   ```bash
   git push origin feature/your-feature-name
   ```

4. Request review from at least one team member

5. Address review comments and push updates

6. After approval, squash and merge your PR

## Troubleshooting

### Common Issues

1. **Build Failures**
   - Run `mvn clean install -U` to update dependencies
   - Check for compilation errors
   - Verify Java version compatibility

2. **Database Connection Issues**
   - Check if the database is running
   - Verify connection string in `application.yml`
   - Check database logs for errors

3. **Service Discovery Issues**
   - Verify Eureka server is running
   - Check service registration logs
   - Verify network connectivity between services

### Debugging Tips

1. **Check Application Logs**
   ```bash
   tail -f order-service/logs/application.log
   ```

2. **Inspect HTTP Traffic**
   - Use Postman or cURL to test endpoints
   - Enable request/response logging

3. **Profile Application**
   - Use JProfiler or YourKit for performance analysis
   - Enable Spring Boot Actuator endpoints for metrics

## FAQs

### How do I add a new dependency?

1. Add the dependency to the appropriate `pom.xml`
2. Run `mvn clean install`
3. Update the dependency version in the parent `pom.xml` if needed

### How do I run a specific test?

```bash
# Run a single test class
mvn test -Dtest=OrderServiceTest

# Run a specific test method
mvn test -Dtest=OrderServiceTest#shouldCreateOrderWithValidInput
```

### How do I reset the database?

```bash
# Stop the database container
docker-compose -f deployment/docker-compose.dev.yml down -v

# Start the database container
docker-compose -f deployment/docker-compose.dev.yml up -d postgres
```

## Support

For development support, contact:
- Email: dev@sattvabite.com
- Slack: #dev-support
- Documentation: https://docs.sattvabite.com/development

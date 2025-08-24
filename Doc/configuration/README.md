# Configuration Guide

This guide provides detailed information about configuring the SattvaBite platform, including environment variables, feature flags, and other configuration options.

## Table of Contents

1. [Environment Variables](#environment-variables)
2. [Application Properties](#application-properties)
3. [Feature Flags](#feature-flags)
4. [Rate Limiting](#rate-limiting)
5. [Caching](#caching)
6. [Logging](#logging)
7. [Security](#security)
8. [Database](#database)
9. [Message Broker](#message-broker)
10. [Tracing and Monitoring](#tracing-and-monitoring)

## Environment Variables

### Core Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | No | `dev` | Active Spring profiles |
| `SERVER_PORT` | No | `8080` | Application server port |
| `SERVER_SERVLET_CONTEXT_PATH` | No | `/` | Application context path |

### Database

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | Yes | - | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | Yes | - | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Yes | - | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | No | `validate` | Hibernate DDL strategy |
| `SPRING_JPA_SHOW_SQL` | No | `false` | Show SQL queries in logs |

### Redis

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_REDIS_HOST` | Yes | `localhost` | Redis server host |
| `SPRING_REDIS_PORT` | No | `6379` | Redis server port |
| `SPRING_REDIS_PASSWORD` | No | - | Redis password |

### Kafka

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `KAFKA_BOOTSTRAP_SERVERS` | Yes | `localhost:9092` | Kafka bootstrap servers |
| `KAFKA_CONSUMER_GROUP_ID` | No | `sattvabite-group` | Consumer group ID |
| `KAFKA_SECURITY_PROTOCOL` | No | `PLAINTEXT` | Security protocol |

## Application Properties

### Server Configuration

```yaml
server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
    min-response-size: 1KB
```

### Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sattvabite
    username: user
    password: password
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      idle-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Redis Configuration

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD:}
    timeout: 5000
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

## Feature Flags

### Configuration

```yaml
app:
  features:
    flags:
      new-checkout-flow: true
      experimental-feature: false
      maintenance-mode: false
      enable-promotions: true
      enable-reviews: true
```

### Usage in Code

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final FeatureFlagConfig featureFlags;
    
    public void processOrder(Order order) {
        if (featureFlags.isEnabled("new-checkout-flow")) {
            processOrderNewFlow(order);
        } else {
            processOrderLegacy(order);
        }
    }
}
```

## Rate Limiting

### Configuration

```yaml
app:
  rate-limiting:
    enabled: true
    default-limit:
      capacity: 100
      duration: 60
    authenticated:
      capacity: 1000
      duration: 60
    admin:
      capacity: 5000
      duration: 60
    exclude-paths:
      - /actuator/**
      - /v3/api-docs/**
      - /swagger-ui/**
```

### Headers

| Header | Description |
|--------|-------------|
| X-RateLimit-Limit | Maximum number of requests allowed |
| X-RateLimit-Remaining | Remaining number of requests |
| X-RateLimit-Reset | Time when the rate limit resets (UNIX timestamp) |
| Retry-After | Time to wait before making another request (in seconds) |

## Caching

### Configuration

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600s
      key-prefix: sattvabite_
      cache-null-values: false

app:
  cache:
    ttl:
      orders: 3600
      menu-items: 1800
      restaurants: 3600
      user-preferences: 86400
```

### Cache Annotations

```java
@Cacheable(value = "orders", key = "#orderId")
public Order getOrder(String orderId) {
    // Implementation
}

@CachePut(value = "orders", key = "#order.id")
public Order updateOrder(Order order) {
    // Implementation
}

@CacheEvict(value = "orders", key = "#orderId")
public void deleteOrder(String orderId) {
    // Implementation
}
```

## Logging

### Configuration

```yaml
logging:
  level:
    root: INFO
    com.sattvabite: DEBUG
    org.springframework.web: WARN
    org.hibernate.SQL: DEBUG
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 7
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### Logback Configuration

```xml
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <root level="${LOG_LEVEL:-INFO}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Security

### JWT Configuration

```yaml
app:
  security:
    jwt:
      secret: your-secret-key
      expiration: 86400000 # 24 hours
      issuer: SattvaBite
      audience: sattvabite-app
      header: Authorization
      prefix: Bearer
```

### CORS Configuration

```yaml
app:
  cors:
    allowed-origins: 
      - http://localhost:3000
      - https://app.sattvabite.com
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    exposed-headers: "X-Total-Count"
    allow-credentials: true
    max-age: 3600
```

## Database

### Flyway Migrations

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 1
    validate-on-migrate: true
    out-of-order: false
    placeholders:
      table-prefix: sb_
```

### Multi-tenancy

```yaml
app:
  multi-tenancy:
    enabled: true
    type: SCHEMA
    default-tenant: public
```

## Message Broker

### Kafka Configuration

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: sattvabite-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.sattvabite"
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
    template:
      default-topic: orders
```

### Kafka Topics

```yaml
app:
  kafka:
    topics:
      order-created: order-created
      order-updated: order-updated
      order-cancelled: order-cancelled
      payment-processed: payment-processed
      notification: notification
```

## Tracing and Monitoring

### OpenTelemetry Configuration

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
    info:
      env:
        enabled: true

opentelemetry:
  service:
    name: order-service
    namespace: sattvabite
    version: 1.0.0
  traces:
    exporter:
      jaeger:
        endpoint: http://localhost:14250
      otlp:
        endpoint: http://localhost:4317
  metrics:
    exporter:
      prometheus:
        enabled: true
        endpoint: /actuator/prometheus
```

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health information |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus metrics |
| `/actuator/info` | Application information |
| `/actuator/env` | Environment properties |
| `/actuator/httptrace` | HTTP request traces |
| `/actuator/loggers` | Configure log levels |
| `/actuator/caches` | Manage caches |

## Configuration Management

### Externalized Configuration

1. **Bootstrap Properties**
   ```yaml
   spring:
     application:
       name: order-service
     cloud:
       config:
         uri: ${CONFIG_SERVER_URI:http://localhost:8888}
         fail-fast: true
         retry:
           max-attempts: 6
           max-interval: 2000
   ```

2. **Profile-specific Properties**
   - `application-dev.yml` - Development profile
   - `application-staging.yml` - Staging profile
   - `application-prod.yml` - Production profile

### Vault Integration

```yaml
spring:
  cloud:
    vault:
      uri: ${VAULT_URI:http://localhost:8200}
      authentication: TOKEN
      token: ${VAULT_TOKEN}
      kv:
        enabled: true
        backend: secret
        application-name: order-service
      fail-fast: true
```

## Best Practices

1. **Sensitive Data**
   - Never commit sensitive data to version control
   - Use environment variables or a secure vault for secrets
   - Encrypt sensitive data at rest

2. **Configuration Management**
   - Externalize all configuration
   - Use profiles for environment-specific settings
   - Document all configuration options

3. **Security**
   - Use HTTPS in production
   - Enable CORS only for trusted origins
   - Validate all input data
   - Use prepared statements to prevent SQL injection
   - Implement proper error handling

4. **Performance**
   - Enable caching for frequently accessed data
   - Use connection pooling for database connections
   - Monitor and optimize slow queries
   - Use batch operations for bulk data

## Troubleshooting

### Common Issues

1. **Configuration Not Loading**
   - Check for typos in property names
   - Verify the active profile
   - Check for conflicting properties

2. **Connection Issues**
   - Verify the service is running
   - Check network connectivity
   - Verify credentials and permissions

3. **Performance Problems**
   - Check database indexes
   - Monitor memory and CPU usage
   - Review slow query logs

### Debugging Tips

1. **Enable Debug Logging**
   ```yaml
   logging:
     level:
       root: DEBUG
   ```

2. **Check Application Events**
   ```bash
   curl -X GET "http://localhost:8080/actuator/events"
   ```

3. **Dump Environment**
   ```bash
   curl -X GET "http://localhost:8080/actuator/env"
   ```

## Support

For configuration support, contact:
- Email: devops@sattvabite.com
- Slack: #devops-support
- Documentation: https://docs.sattvabite.com/configuration

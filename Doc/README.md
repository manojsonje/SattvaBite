# SattvaBite - Food Ordering Platform

SattvaBite is a modern, scalable food ordering platform built with microservices architecture. This documentation provides comprehensive information about the system's architecture, APIs, deployment, and development guidelines.

## Documentation Structure

### 1. [Architecture](architecture/README.md)
- [System Overview](architecture/system-overview.md)
- [Microservices](architecture/microservices.md)
- [Data Flow](architecture/data-flow.md)
- [Security](architecture/security.md)

### 2. [API Documentation](api/README.md)
- [Order Service API](api/order-service.md)
- [Food Catalog Service API](api/food-catalog-service.md)
- [Restaurant Service API](api/restaurant-service.md)
- [API Versioning](api/versioning.md)

### 3. [Deployment](deployment/README.md)
- [Local Development](deployment/local-development.md)
- [Docker Setup](deployment/docker.md)
- [Kubernetes Deployment](deployment/kubernetes.md)
- [CI/CD Pipeline](deployment/ci-cd.md)

### 4. [Development](development/README.md)
- [Getting Started](development/getting-started.md)
- [Coding Standards](development/coding-standards.md)
- [Testing](development/testing.md)
- [Debugging](development/debugging.md)

### 5. [Configuration](configuration/README.md)
- [Environment Variables](configuration/environment-variables.md)
- [Feature Flags](configuration/feature-flags.md)
- [Rate Limiting](configuration/rate-limiting.md)
- [Logging](configuration/logging.md)

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker 20.10+
- Kubernetes (for production deployment)
- Redis (for caching and rate limiting)
- PostgreSQL (for data persistence)

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/sattvabite.git
   cd sattvabite
   ```

2. Start required services using Docker Compose:
   ```bash
   docker-compose -f deployment/docker-compose.dev.yml up -d
   ```

3. Build and run services:
   ```bash
   mvn clean install
   cd order-service && mvn spring-boot:run
   ```

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please contact support@sattvabite.com or open an issue in our issue tracker.

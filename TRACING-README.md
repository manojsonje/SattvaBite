# Distributed Tracing Setup Guide

This guide explains how to set up and test distributed tracing in the SattvaBite microservices using Zipkin.

## Prerequisites

- Docker and Docker Compose
- Java 17+
- Maven
- jq (for pretty-printing JSON responses in the test script)

## Setup Instructions

1. **Start the infrastructure services** (Zipkin, MySQL, MongoDB, Redis):
   ```bash
   docker-compose up -d
   ```

2. **Build all services**:
   ```bash
   mvn clean install -DskipTests
   ```

3. **Start the services in order**:
   1. Service Registry:
      ```bash
      cd service-registry
      mvn spring-boot:run -Dspring-boot.run.profiles=tracing
      ```
   
   2. API Gateway:
      ```bash
      cd ../api-gateway
      mvn spring-boot:run -Dspring-boot.run.profiles=tracing
      ```
   
   3. Other services (in any order):
      ```bash
      # In separate terminals
      cd ../order-service
      mvn spring-boot:run -Dspring-boot.run.profiles=tracing
      
      cd ../food-catalogue-service
      mvn spring-boot:run -Dspring-boot.run.profiles=tracing
      
      cd ../restaurant-listing-service
      mvn spring-boot:run -Dspring-boot.run.profiles=tracing
      ```

## Testing the Tracing Setup

1. **Run the test script** to generate some traffic:
   ```bash
   chmod +x test-tracing.sh
   ./test-tracing.sh
   ```

2. **View traces in Zipkin UI**:
   - Open http://localhost:9411 in your browser
   - Click "Find Traces" to see the captured traces
   - Click on a trace to see the detailed request flow across services

## Understanding the Traces

- Each request is assigned a unique `traceId`
- Each service operation creates a `span` within the trace
- The `spanId` represents individual operations
- The `parentId` shows the relationship between spans

## Troubleshooting

1. **No traces in Zipkin?**
   - Ensure all services are running with the `tracing` profile
   - Check service logs for any errors
   - Verify Zipkin is accessible at http://localhost:9411

2. **Missing spans?**
   - Check if the request actually passed through all expected services
   - Verify that the `spring.sleuth.sampler.probability` is set to 1.0

3. **High latency?**
   - Check the timing information in the Zipkin UI
   - Look for slow database queries or external API calls

## Next Steps

- Adjust the sampling rate in production (e.g., 0.1 for 10% sampling)
- Set up alerting for slow traces
- Integrate with a metrics system like Prometheus
- Configure log correlation with ELK stack or similar

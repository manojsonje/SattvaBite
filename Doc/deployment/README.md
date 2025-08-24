# Deployment Guide

This guide provides comprehensive instructions for deploying the SattvaBite platform in various environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development](#local-development)
3. [Docker Deployment](#docker-deployment)
4. [Kubernetes Deployment](#kubernetes-deployment)
5. [CI/CD Pipeline](#ci-cd-pipeline)
6. [Scaling](#scaling)
7. [Monitoring](#monitoring)
8. [Backup and Recovery](#backup-and-recovery)
9. [Troubleshooting](#troubleshooting)

## Prerequisites

### Hardware Requirements

| Component | Development | Staging | Production |
|-----------|-------------|---------|------------|
| CPU | 4 cores | 8 cores | 16+ cores |
| RAM | 8GB | 16GB | 32GB+ |
| Storage | 20GB | 100GB | 500GB+ |
| Network | 100Mbps | 1Gbps | 10Gbps |

### Software Requirements

- Docker 20.10+
- Docker Compose 1.29+
- Kubernetes 1.21+
- Helm 3.8+
- kubectl (matching your cluster version)
- PostgreSQL 14+
- Redis 6.2+
- Kafka 3.2+

## Local Development

### Setting Up the Development Environment

1. **Clone the repository**
   ```bash
   git clone https://github.com/sattvabite/sattvabite.git
   cd sattvabite
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env file with your configuration
   ```

3. **Start dependencies**
   ```bash
   docker-compose -f deployment/docker-compose.dev.yml up -d
   ```

4. **Build and run services**
   ```bash
   # Build all services
   mvn clean install
   
   # Run order service
   cd order-service
   mvn spring-boot:run
   ```

### Development Workflow

1. Create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make your changes and commit
   ```bash
   git add .
   git commit -m "Add your feature"
   ```

3. Push your changes and create a pull request
   ```bash
   git push origin feature/your-feature-name
   ```

## Docker Deployment

### Building Docker Images

```bash
# Build all services
./mvnw spring-boot:build-image -DskipTests

# Or build a specific service
cd order-service
./mvnw spring-boot:build-image -DskipTests
```

### Running with Docker Compose

1. **Production configuration**
   ```bash
   docker-compose -f deployment/docker-compose.prod.yml up -d
   ```

2. **Scaling services**
   ```bash
   docker-compose -f deployment/docker-compose.prod.yml up -d --scale order-service=3
   ```

### Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | No | `dev` | Active Spring profiles |
| `SPRING_DATASOURCE_URL` | Yes | - | Database connection URL |
| `SPRING_REDIS_HOST` | Yes | - | Redis host |
| `KAFKA_BOOTSTRAP_SERVERS` | Yes | - | Kafka bootstrap servers |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | Yes | - | Eureka server URL |

## Kubernetes Deployment

### Prerequisites

- A running Kubernetes cluster
- kubectl configured to access your cluster
- Helm installed

### Deploying with Helm

1. **Add the Helm repository**
   ```bash
   helm repo add sattvabite https://charts.sattvabite.com
   helm repo update
   ```

2. **Install the chart**
   ```bash
   helm install sattvabite sattvabite/sattvabite \
     --namespace sattvabite \
     --create-namespace \
     -f deployment/values.yaml
   ```

### Kubernetes Manifests

Example deployment for the order service:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: sattvabite
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
      - name: order-service
        image: sattvabite/order-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        resources:
          requests:
            cpu: "500m"
            memory: "512Mi"
          limits:
            cpu: "1000m"
            memory: "1Gi"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
```

## CI/CD Pipeline

The CI/CD pipeline is configured using GitHub Actions. The workflow includes:

1. **Build** - Compile and test the code
2. **Test** - Run unit and integration tests
3. **Build Docker Images** - Create production-ready Docker images
4. **Scan** - Security scanning of dependencies and images
5. **Deploy to Staging** - Automated deployment to staging environment
6. **Manual Approval** - Required before production deployment
7. **Deploy to Production** - Manual trigger for production deployment

### Manual Deployment

```bash
# Deploy to staging
kubectl apply -f k8s/staging/

# Promote to production
kubectl apply -f k8s/production/
```

## Scaling

### Horizontal Pod Autoscaling

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: order-service
  namespace: sattvabite
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### Database Scaling

- Read replicas for read-heavy operations
- Connection pooling with HikariCP
- Query optimization and indexing

## Monitoring

### Prometheus and Grafana

```bash
# Install monitoring stack
helm install monitoring stable/prometheus-operator \
  --namespace monitoring \
  --create-namespace

# Access Grafana
kubectl port-forward -n monitoring svc/monitoring-grafana 3000:80
```

### Logging with ELK Stack

```bash
# Install ELK stack
helm install elk elastic/elasticsearch \
  --namespace logging \
  --create-namespace

# Access Kibana
kubectl port-forward -n logging svc/kibana-kibana 5601:5601
```

## Backup and Recovery

### Database Backups

```bash
# Create backup
kubectl exec -it postgres-0 -- pg_dump -U postgres sattvabite > backup.sql

# Restore from backup
cat backup.sql | kubectl exec -i postgres-0 -- psql -U postgres sattvabite
```

### Persistent Volume Backups

```yaml
apiVersion: velero.io/v1
kind: Schedule
metadata:
  name: daily-backup
  namespace: velero
spec:
  schedule: "@daily"
  template:
    includedNamespaces:
    - sattvabite
    ttl: 720h0m0s
```

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   - Check if the database is running
   - Verify connection string and credentials
   - Check network policies

2. **Service Discovery Issues**
   - Verify Eureka server is running
   - Check service registration
   - Verify network connectivity

3. **Performance Issues**
   - Check resource utilization
   - Review application logs
   - Check database query performance

### Logs and Debugging

```bash
# View pod logs
kubectl logs -f <pod-name> -n sattvabite

# Get pod status
kubectl get pods -n sattvabite

# Describe pod
kubectl describe pod <pod-name> -n sattvabite

# Access container shell
kubectl exec -it <pod-name> -n sattvabite -- /bin/sh
```

## Support

For deployment support, contact:
- Email: devops@sattvabite.com
- Slack: #devops-support
- Documentation: https://docs.sattvabite.com/deployment

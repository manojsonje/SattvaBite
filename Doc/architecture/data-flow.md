# Data Flow

## Overview

This document describes the flow of data through the SattzaBite system, including request/response patterns, event-driven communication, and data synchronization between services.

## Request/Response Flow

### 1. User Places an Order

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant O as Order Service
    participant F as Food Catalog
    participant P as Payment Service
    participant N as Notification Service
    
    C->>G: POST /api/orders
    G->>O: Forward request (with auth)
    O->>F: Validate menu items
    F-->>O: Item details & availability
    O->>O: Create order (PENDING)
    O->>P: Process payment
    P-->>O: Payment confirmation
    O->>O: Update order status (CONFIRMED)
    O->>N: Send order confirmation
    O-->>G: Order created response
    G-->>C: 201 Created
```

### 2. Restaurant Updates Order Status

```mermaid
sequenceDiagram
    participant R as Restaurant Client
    participant G as API Gateway
    participant O as Order Service
    participant K as Kafka
    participant N as Notification Service
    participant U as User Service
    
    R->>G: PUT /api/orders/{id}/status (PREPARING)
    G->>O: Forward request
    O->>O: Update status
    O->>K: Publish OrderStatusUpdatedEvent
    K->>N: Consume event
    N->>U: Get user preferences
    N->>N: Send status update notification
    K->>U: Consume event (update user's order history)
    O-->>G: 200 OK
    G-->>R: Status updated
```

## Event-Driven Communication

### 1. Order Lifecycle Events

```mermaid
graph LR
    A[Order Created] --> B[Payment Processed]
    B --> C[Order Confirmed]
    C --> D[Order Preparing]
    D --> E[Order Ready]
    E --> F[Order Picked Up/Out for Delivery]
    F --> G[Order Delivered]
    
    style A fill:#d4f1f9,stroke:#333
    style B fill:#d4f1f9,stroke:#333
    style C fill:#d4f1f9,stroke:#333
    style D fill:#d4f1f9,stroke:#333
    style E fill:#d4f1f9,stroke:#333
    style F fill:#d4f1f9,stroke:#333
    style G fill:#d4f1f9,stroke:#333
```

### 2. Event Types and Consumers

| Event Type | Produced By | Consumed By | Description |
|------------|-------------|-------------|-------------|
| OrderCreated | Order Service | Notification, Analytics | Triggered when a new order is created |
| OrderStatusUpdated | Order Service | Notification, User Service | When order status changes |
| PaymentProcessed | Payment Service | Order, Notification | Payment completion event |
| MenuItemUpdated | Food Catalog | Search, Cache | When menu item details change |
| RestaurantStatusChanged | Restaurant Service | Search, Notification | Restaurant open/close status |
| UserPreferenceUpdated | User Service | Recommendation | When user updates preferences |

## Data Synchronization

### 1. CQRS Pattern

```mermaid
flowchart LR
    subgraph Write Model
        A[Order Service] -->|Events| B[(Event Store)]
    end
    
    subgraph Read Model
        B -->|Projection| C[(Read DB)]
        C --> D[Order Queries]
    end
    
    style A fill:#e1f5fe,stroke:#333
    style B fill:#f3e5f5,stroke:#333
    style C fill:#e8f5e9,stroke:#333
    style D fill:#e1f5fe,stroke:#333
```

### 2. Cache Invalidation Flow

```mermaid
sequenceDiagram
    participant S as Service
    participant C as Cache (Redis)
    participant DB as Database
    
    S->>C: Get data (key)
    alt Cache Hit
        C-->>S: Return cached data
    else Cache Miss
        S->>DB: Query database
        DB-->>S: Return data
        S->>C: Cache data (with TTL)
    end
    
    Note right of S: On data modification
    S->>DB: Update data
    S->>C: Invalidate cache (key)
    S->>K: Publish cache invalidation event
```

## Cross-Service Data Access

### 1. Service-to-Service Communication

**Synchronous (REST/HTTP):**
- Service discovery via Eureka
- Load balanced with Spring Cloud LoadBalancer
- Circuit breakers for fault tolerance

**Asynchronous (Kafka):**
- Event-driven architecture
- Exactly-once delivery semantics
- Dead letter queues for failed messages

### 2. Data Consistency Patterns

**Saga Pattern:**
- For distributed transactions
- Compensating transactions for rollback
- Example: Order cancellation after payment

**Outbox Pattern:**
- Reliable event publishing
- Prevents dual-write problem
- Transactional outbox table

## Real-time Updates

### 1. WebSocket Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant W as WebSocket Service
    participant K as Kafka
    
    C->>G: HTTP Upgrade (WebSocket)
    G->>W: Forward connection
    W-->>C: 101 Switching Protocols
    
    Note over C,W: WebSocket connection established
    
    C->>W: Subscribe to /topic/orders/{orderId}
    W-->>C: Subscription confirmed
    
    alt Order status update
        W->>K: Publish OrderStatusUpdatedEvent
        K->>W: Consume event
        W->>C: Send WebSocket message
    end
```

### 2. Server-Sent Events (SSE)

Used for:
- Order status updates
- Promotions and offers
- System notifications

## File and Media Handling

### 1. Image Upload Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant S as Storage Service
    participant DB as Database
    
    C->>G: POST /api/storage/upload (multipart/form-data)
    G->>S: Forward upload request
    S->>S: Validate file type/size
    S->>S: Generate unique filename
    S->>S: Upload to S3/Blob Storage
    S->>DB: Store metadata
    DB-->>S: Confirmation
    S-->>G: File URL and metadata
    G-->>C: 201 Created
```

## Search and Analytics

### 1. Search Indexing Flow

```mermaid
flowchart LR
    A[Service] -->|Publish| B[(Kafka)]
    B --> C[Search Consumer]
    C -->|Index| D[(Elasticsearch)]
    
    E[Client] -->|Search| F[API Gateway]
    F --> G[Search Service]
    G -->|Query| D
    D -->|Results| G
    G -->|Response| F
    F --> E
```

## Security Data Flow

### 1. Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant A as Auth Service
    
    C->>G: POST /auth/login (credentials)
    G->>A: Forward login request
    A->>A: Validate credentials
    A->>A: Generate JWT
    A-->>G: JWT token
    G-->>C: Set HTTP-only cookie with JWT
    
    Note over C,G: Subsequent requests include JWT in Authorization header
    
    C->>G: GET /api/orders (with JWT)
    G->>G: Validate JWT
    G->>G: Check permissions
    G->>Order Service: Forward request (with user context)
```

## Error Handling and Retries

### 1. Retry Mechanism

```mermaid
flowchart LR
    A[Service] --> B{Make Request}
    B -->|Success| C[Process Response]
    B -->|Failure| D{Retry Count < Max?}
    D -->|Yes| E[Wait & Increment]
    E --> B
    D -->|No| F[Circuit Breaker]
    F -->|Open| G[Fail Fast]
    F -->|Half-Open| H[Test Request]
    H -->|Success| I[Close Circuit]
    H -->|Failure| G
```

## Monitoring and Logging

### 1. Log Aggregation

```
[Service] -> [Logstash] -> [Elasticsearch] <-> [Kibana]
                     |
                     v
              [Archive (S3)]
```

### 2. Metrics Collection

```
[Service (Micrometer)] -> [Prometheus] <-> [Grafana]
```

## Conclusion

This data flow documentation provides a comprehensive view of how data moves through the SattzaBite system. The architecture is designed to be:

1. **Scalable**: Horizontal scaling of stateless services
2. **Resilient**: Circuit breakers, retries, and fallbacks
3. **Maintainable**: Clear separation of concerns
4. **Observable**: Comprehensive logging and monitoring
5. **Secure**: End-to-end encryption and proper authentication/authorization

Understanding these data flows is crucial for development, debugging, and optimizing the system's performance.

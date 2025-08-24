# API Documentation

## Overview

This section contains the comprehensive API documentation for all SattzaBite microservices. The APIs follow RESTful principles and use standard HTTP methods and status codes.

## Base URLs

| Environment | Base URL |
|-------------|----------|
| Production | `https://api.sattzabite.com/v1` |
| Staging | `https://staging-api.sattzabite.com/v1` |
| Development | `http://localhost:8080/api/v1` |

## Authentication

All API requests require authentication using JWT (JSON Web Tokens). Include the token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

## Common Headers

| Header | Description | Example |
|--------|-------------|---------|
| `Accept` | Response format | `application/json` |
| `Content-Type` | Request body format | `application/json` |
| `X-Request-ID` | Unique request identifier | `550e8400-e29b-41d4-a716-446655440000` |
| `X-Client-Version` | Client application version | `1.0.0` |

## Error Handling

### Standard Error Response

```json
{
  "timestamp": "2025-08-23T01:10:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/v1/orders/123",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Error Codes

| Status Code | Error Type | Description |
|-------------|------------|-------------|
| 400 | Bad Request | Invalid request parameters |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource conflict |
| 422 | Unprocessable Entity | Validation failed |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Server error |
| 503 | Service Unavailable | Service temporarily unavailable |

## Rate Limiting

- **Anonymous**: 100 requests per minute
- **Authenticated Users**: 1000 requests per minute
- **Partners/Developers**: 5000 requests per minute

Response headers include rate limit information:

```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 987
X-RateLimit-Reset: 1624500000
```

## Pagination

All list endpoints support pagination using query parameters:

- `page`: Page number (default: 0)
- `size`: Items per page (default: 20, max: 100)
- `sort`: Sort criteria (e.g., `name,asc` or `createdAt,desc`)

### Pagination Response

```json
{
  "content": [],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": false,
  "totalPages": 5,
  "totalElements": 100,
  "first": true,
  "numberOfElements": 20,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "size": 20,
  "number": 0,
  "empty": false
}
```

## Filtering

Use query parameters to filter results:

```
GET /api/v1/items?name=Burger&minPrice=5&maxPrice=15&category=MAIN
```

## Field Selection

Use the `fields` parameter to limit response fields:

```
GET /api/v1/items/123?fields=id,name,price
```

## Common Data Types

### Address

```json
{
  "street": "123 Main St",
  "city": "San Francisco",
  "state": "CA",
  "postalCode": "94105",
  "country": "USA",
  "latitude": 37.7749,
  "longitude": -122.4194
}
```

### Time Range

```json
{
  "startTime": "2025-08-23T12:00:00Z",
  "endTime": "2025-08-23T13:00:00Z"
}
```

### Money

```json
{
  "amount": 19.99,
  "currency": "USD"
}
```

## API Versioning

API versioning is handled through:

1. URL Path: `/api/v1/...`
2. Accept Header: `Accept: application/vnd.sattzabite.v1+json`

## Deprecation Policy

- APIs are supported for at least 6 months after deprecation
- Deprecated endpoints include a `Deprecation` header
- Migration guides will be provided for breaking changes

## Service-Specific Documentation

- [Order Service API](order-service.md)
- [Food Catalog Service API](food-catalog-service.md)
- [Restaurant Service API](restaurant-service.md)
- [User Service API](user-service.md)

## SDKs and Client Libraries

Official SDKs are available for:

- [JavaScript/TypeScript](https://github.com/sattzabite/sdk-js)
- [Python](https://github.com/sattzabite/sdk-python)
- [Java](https://github.com/sattzabite/sdk-java)

## Support

For API support, please contact:

- **Email**: api-support@sattzabite.com
- **Slack**: #api-support
- **Documentation**: https://docs.sattzabite.com/api

## Changelog

### v1.0.0 (2025-08-23)
- Initial public API release
- Core order management functionality
- Menu and restaurant management
- User authentication and authorization

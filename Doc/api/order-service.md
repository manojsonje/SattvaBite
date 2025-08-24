# Order Service API Documentation

## Overview
The Order Service is responsible for managing the complete order lifecycle in the SattvaBite platform. It handles order creation, status updates, and order history.

## Base URL
```
https://api.sattvabite.com/orders
```

## Authentication
All endpoints require authentication using a JWT token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

## API Versioning
The API uses semantic versioning (e.g., v1, v2). You can specify the version using one of these methods:

1. URL Path: `/v1/orders`
2. Header: `X-API-Version: 1.0`
3. Query Parameter: `/orders?api-version=1.0`

## Rate Limiting
- 100 requests per minute per IP for unauthenticated users
- 1000 requests per minute per user for authenticated users
- 5000 requests per minute for admin users

## Error Handling
All error responses follow this format:
```json
{
  "timestamp": "2025-08-23T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 123",
  "path": "/v1/orders/123"
}
```

## Endpoints

### Create Order
```
POST /v1/orders
```

**Request Body**
```json
{
  "restaurantId": "rest-123",
  "items": [
    {
      "menuItemId": "item-456",
      "quantity": 2,
      "specialInstructions": "No onions, please"
    }
  ],
  "deliveryAddress": {
    "street": "123 Main St",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560001",
    "country": "India"
  },
  "paymentMethod": "CREDIT_CARD"
}
```

**Response**
```json
{
  "orderId": "order-789",
  "status": "PENDING",
  "estimatedDeliveryTime": "2025-08-23T13:30:00Z",
  "totalAmount": 24.99,
  "createdAt": "2025-08-23T12:00:00Z"
}
```

### Get Order Status
```
GET /v1/orders/{orderId}
```

**Response**
```json
{
  "orderId": "order-789",
  "status": "PREPARING",
  "restaurantName": "Sattva Bite",
  "items": [
    {
      "name": "Vegetable Biryani",
      "quantity": 2,
      "price": 12.49
    }
  ],
  "totalAmount": 24.99,
  "estimatedDeliveryTime": "2025-08-23T13:30:00Z",
  "createdAt": "2025-08-23T12:00:00Z",
  "updatedAt": "2025-08-23T12:05:00Z"
}
```

### Update Order Status
```
PATCH /v1/orders/{orderId}/status
```

**Request Body**
```json
{
  "status": "OUT_FOR_DELIVERY",
  "statusMessage": "Your order is on its way!"
}
```

### Cancel Order
```
POST /v1/orders/{orderId}/cancel
```

**Response**
```json
{
  "orderId": "order-789",
  "status": "CANCELLED",
  "cancelledAt": "2025-08-23T12:10:00Z"
}
```

### Get Order History
```
GET /v1/orders/history
```

**Query Parameters**
- `page` (optional, default: 0)
- `size` (optional, default: 10)
- `from` (optional, ISO date string)
- `to` (optional, ISO date string)
- `status` (optional, enum: PENDING, CONFIRMED, PREPARING, etc.)

**Response**
```json
{
  "content": [
    {
      "orderId": "order-789",
      "restaurantName": "Sattva Bite",
      "status": "DELIVERED",
      "totalAmount": 24.99,
      "orderDate": "2025-08-23T12:00:00Z",
      "deliveryDate": "2025-08-23T13:25:00Z"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

## Webhooks

### Order Status Update
```
POST /v1/webhooks/order-updates
```

**Headers**
- `X-Signature`: HMAC-SHA256 signature of the payload

**Payload**
```json
{
  "eventType": "ORDER_STATUS_UPDATED",
  "orderId": "order-789",
  "newStatus": "DELIVERED",
  "timestamp": "2025-08-23T13:25:00Z",
  "metadata": {
    "deliveryPerson": {
      "name": "John Doe",
      "phone": "+919876543210"
    }
  }
}
```

## Error Codes

| Status Code | Error Code | Description |
|-------------|------------|-------------|
| 400 | INVALID_REQUEST | Invalid request parameters |
| 401 | UNAUTHORIZED | Authentication required |
| 403 | FORBIDDEN | Insufficient permissions |
| 404 | ORDER_NOT_FOUND | Order not found |
| 409 | INVALID_ORDER_STATE | Invalid order state transition |
| 422 | VALIDATION_ERROR | Request validation failed |
| 429 | TOO_MANY_REQUESTS | Rate limit exceeded |
| 500 | INTERNAL_SERVER_ERROR | Internal server error |

## Rate Limit Headers

| Header | Description |
|--------|-------------|
| X-RateLimit-Limit | Maximum number of requests allowed |
| X-RateLimit-Remaining | Remaining number of requests |
| X-RateLimit-Reset | Time when the rate limit resets (UNIX timestamp) |
| Retry-After | Time to wait before making another request (in seconds) |

## Best Practices

1. Always check the response status code before processing the response
2. Implement retry logic for failed requests (with exponential backoff)
3. Cache responses when appropriate
4. Use the `If-None-Match` header for conditional requests
5. Handle rate limiting gracefully in your application

## SDKs

Official SDKs are available for the following platforms:

- [JavaScript/TypeScript](https://github.com/sattvabite/sdk-js)
- [Android](https://github.com/sattvabite/sdk-android)
- [iOS](https://github.com/sattvabite/sdk-ios)
- [Python](https://github.com/sattvabite/sdk-python)

## Support

For API support, please contact:
- Email: api-support@sattvabite.com
- Slack: #api-support
- Documentation: https://docs.sattvabite.com/api

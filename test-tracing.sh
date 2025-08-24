#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Function to make HTTP requests and print the response
make_request() {
    local method=$1
    local url=$2
    local data=$3
    
    echo -e "${GREEN}Making $method request to: $url${NC}"
    if [ -z "$data" ]; then
        curl -X $method "$url" -H "Content-Type: application/json" -s | jq .
    else
        echo "Request data: $data"
        curl -X $method "$url" -H "Content-Type: application/json" -d "$data" -s | jq .
    fi
    echo -e "\n${GREEN}----------------------------------------${NC}\n"
}

# Test API Gateway endpoints
BASE_URL="http://localhost:8080"

# 1. Get all restaurants
make_request GET "$BASE_URL/restaurant/restaurants"

# 2. Get menu for a restaurant (assuming restaurant with ID 1 exists)
make_request GET "$BASE_URL/restaurant/menu/1"

# 3. Create an order
ORDER_DATA='{
  "restaurantId": 1,
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2
    }
  ],
  "customerInfo": {
    "name": "Test User",
    "email": "test@example.com",
    "phone": "+1234567890"
  },
  "deliveryAddress": "123 Test St, Test City"
}'
make_request POST "$BASE_URL/order" "$ORDER_DATA"

echo -e "${GREEN}Test requests completed. Check Zipkin UI at http://localhost:9411 to view traces.${NC}"

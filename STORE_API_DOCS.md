# 🏪 Store Details API

## Endpoint
```
GET /stores/{storeId}
```

## Description
Retrieves detailed information about a specific store by its ID.

## Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| storeId | Long | Yes | The unique identifier of the store |

## Response
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "My Store",
    "phone": "+91-9876543210",
    "upiId": "mystorename@upi",
    "currency": "INR",
    "isActive": true,
    "createdAt": "2026-05-06T00:30:00"
  },
  "message": null,
  "error": null
}
```

## Response Fields
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique store identifier |
| name | String | Store name |
| phone | String | Store contact phone number |
| upiId | String | UPI ID for payments |
| currency | String | Currency code (default: INR) |
| isActive | Boolean | Whether the store is active |
| createdAt | LocalDateTime | Store creation timestamp |

## Error Responses

### Store Not Found (404)
```json
{
  "success": false,
  "data": null,
  "message": null,
  "error": "Store not found"
}
```

## Example Usage

### CURL
```bash
curl -X GET "http://localhost:8080/stores/1"
```

### JavaScript/Fetch
```javascript
fetch('http://localhost:8080/stores/1')
  .then(response => response.json())
  .then(data => console.log(data));
```

## Implementation Details

- **Controller**: `StoreController.getStoreDetails()`
- **Service**: `StoreService.getStoreDetails()`
- **Repository**: Uses JPA `findById()` method
- **Exception Handling**: Throws `RuntimeException` for not found stores
- **Response Format**: Wrapped in `ApiResponse.success()`

## Notes

- This endpoint does not require authentication (storeId is public)
- All store fields are returned except sensitive internal data
- Timestamps are in ISO 8601 format
- Currency defaults to "INR" if not specified

---

**Status**: ✅ IMPLEMENTED & TESTED
**Date**: May 6, 2026

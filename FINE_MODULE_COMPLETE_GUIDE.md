# Fine Module - Complete Architecture & Postman Testing Guide

## 📋 Table of Contents
1. [Module Overview](#module-overview)
2. [Data Flow & Architecture](#data-flow--architecture)
3. [Entity Relationships](#entity-relationships)
4. [Key Concepts Explained](#key-concepts-explained)
5. [API Endpoints](#api-endpoints)
6. [Postman Testing Guide](#postman-testing-guide)
7. [Response Examples](#response-examples)
8. [Error Handling](#error-handling)

---

## 🎯 Module Overview

The **Fine Module** manages library penalties/fines for users in the Library Management System. It handles:
- Creating fines for overdue books, damages, or lost items
- Processing fine payments via payment gateways (Stripe)
- Waiving fines (by administrators)
- Retrieving fine history and status
- Filtering fines by status and type

### Key Statistics
- **Status**: PENDING → PARTIALLY_PAID → PAID or WAIVED
- **Type**: OVERDUE, DAMAGE, LOST, PROCESSING
- **Payment Gateway**: Stripe integration
- **Currency**: Multi-currency support (default: INR)

---

## 🔄 Data Flow & Architecture

### Component Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     FINE MODULE                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐                                             │
│  │  Controller  │─────────┐                                   │
│  │ (FineCtrl)   │         │                                   │
│  └──────────────┘         │                                   │
│         │                 │                                   │
│         ▼                 ▼                                   │
│  ┌──────────────────────────────────┐                         │
│  │     Service Layer                │                         │
│  │  (FineService/Impl)              │                         │
│  └──────────────────────────────────┘                         │
│         │                                                     │
│         ▼                                                     │
│  ┌──────────────────────────────────┐                         │
│  │    Repository Layer              │                         │
│  │  (FineRepository)                │                         │
│  └──────────────────────────────────┘                         │
│         │                                                     │
│         ▼                                                     │
│  ┌──────────────────────────────────┐                         │
│  │     Database                     │                         │
│  │  (Fine Table)                    │                         │
│  └──────────────────────────────────┘                         │
│                                                               │
│  ┌──────────────────────────────────┐                         │
│  │   External Integrations          │                         │
│  │  - PaymentService (Stripe)       │                         │
│  │  - UserService                   │                         │
│  └──────────────────────────────────┘                         │
└─────────────────────────────────────────────────────────────┘
```

### Request/Response Flow for Create Fine

```
Client Request
    │
    ▼
┌─────────────────────────────┐
│  POST /api/fines/create-fine│
│  Body: {                    │
│    bookLoanId, fineType,    │
│    amount, reason, notes    │
│  }                          │
└─────────────────────────────┘
    │
    ▼
┌─────────────────────────────┐
│   FineController            │
│   createFine()              │
└─────────────────────────────┘
    │
    ▼
┌─────────────────────────────┐
│   FineServiceImpl            │
│   createFine()              │
│ 1. Fetch BookLoan by ID     │
│ 2. Create Fine object       │
│ 3. Save to DB               │
└─────────────────────────────┘
    │
    ▼
┌─────────────────────────────┐
│   FineRepository.save()     │
│   Insert into DB            │
└─────────────────────────────┘
    │
    ▼
┌─────────────────────────────┐
│   FineMapper.toDto()        │
│   Convert Entity → DTO      │
└─────────────────────────────┘
    │
    ▼
┌─────────────────────────────┐
│ Response 200 OK             │
│ Body: FineDto               │
└─────────────────────────────┘
```

### Payment Processing Flow

```
Client initiates payment
    │
    ▼
POST /api/fines/{fineId}/pay
    │
    ▼
FineService.payFine()
    ├─ Validate fine exists
    ├─ Check if not already paid/waived
    ├─ Get current user
    └─ Create PaymentInitiateRequest
        │
        ▼
    PaymentService.initiatePayment()
        ├─ Validate payment request
        ├─ Create Payment record
        └─ Initiate with Stripe
            │
            ▼
        Stripe API Response
            ├─ Payment Intent
            └─ Client Secret
            │
            ▼
        Return PaymentInitiateResponse
            │
            ▼
Response: { paymentIntentId, clientSecret, ... }
```

---

## 🔗 Entity Relationships

### Fine Entity Structure

```
┌──────────────────────────────────────┐
│           Fine Entity                │
├──────────────────────────────────────┤
│ id (PK)                              │
│ user_id (FK) → User                  │
│ book_loan_id (FK) → BookLoan         │
│ fineType (ENUM)                      │
│ amount (Long)                        │
│ fineStatus (ENUM)                    │
│ reason (String)                      │
│ note (String)                        │
│ waived_by_id (FK) → User (Optional)  │
│ waived_at (LocalDateTime)            │
│ waived_reason (String)               │
│ paid_at (LocalDateTime)              │
│ processed_by_user_id (FK, LAZY)      │
│ transaction_id (String)              │
│ createdAt (Timestamp)                │
│ updatedAt (Timestamp)                │
└──────────────────────────────────────┘
```

### Relationship Diagram

```
┌─────────────┐         ┌──────────────┐
│    User     │◄────────│    Fine      │
│  (owns)     │    Many │  (Many→One)  │
└─────────────┘         └──────────────┘
                               │
                               │
                        ┌──────┘
                        │ (Many)
                        │
                   ┌────────────┐
                   │  BookLoan  │
                   │ (Referenced)
                   └────────────┘

Special Relationships:
├─ Fine.user ─────────► User (who has the fine)
├─ Fine.waivedBy ──────► User (admin who waived)
└─ Fine.processedBy ──► User (admin who processed) [LAZY LOADED]
```

### Why LAZY Loading for processedBy?

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "processed_by_user_id")
private User processedBy;
```

**Reason**: The `processedBy` field is only sometimes populated (only after payment processing). Using LAZY loading:
- ✅ Avoids unnecessary DB queries for early-stage fines
- ✅ Improves performance for listing fines
- ⚠️ Requires open transaction context to access later

---

## 💡 Key Concepts Explained

### 1. Fine Status Lifecycle

```
┌─────────┐
│ PENDING │  ◄── Initial status when fine created
└────┬────┘
     │
     ├─────────────────────────────────┐
     │                                 │
     ▼                                 ▼
┌──────────────┐              ┌──────────┐
│PARTIALLY_PAID│  (Future)    │  WAIVED  │  ◄── Admin action
└──────────────┘              └──────────┘
     │
     ▼
 ┌────────┐
 │  PAID  │  ◄── Payment completed
 └────────┘
```

**Status Values:**
- **PENDING**: Fine created but not paid
- **PARTIALLY_PAID**: Some payment received (future implementation)
- **PAID**: Full payment received
- **WAIVED**: Forgiven by administrator

### 2. Fine Types

```
OVERDUE
  └─ Charged for books returned after due date
  └ Amount: Calculated based on days overdue

DAMAGE
  └─ Charged for damaged books
  └─ Amount: % of book price or fixed amount

LOST
  └─ Charged for lost books
  └─ Amount: Full book price + processing fee

PROCESSING
  └─ Processing fees for administrative work
  └─ Amount: Fixed fee
```

### 3. Amount Storage

```
Fine.amount = Long (in smallest currency unit)
Example:
  - 100 PKR = 10000 (paisa)
  - 50 USD = 5000 (cents)
  - 25 INR = 2500 (paise)

Conversion: amount / 100 = actual currency value
```

### 4. User Roles in Fine System

```
┌─────────────────────────────────────────┐
│ Regular User                            │
│ ├─ Can view their own fines             │
│ ├─ Can pay their fines                  │
│ └─ Cannot waive fines                   │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ Admin User                              │
│ ├─ Can view all fines                   │
│ ├─ Can create fines (manually)          │
│ ├─ Can waive fines                      │
│ └─ Can view fine processing details     │
└─────────────────────────────────────────┘
```

---

## 📡 API Endpoints

### 1. Create Fine (Admin Only)
```
POST /api/fines/create-fine
Content-Type: application/json

Request Body:
{
  "bookLoanId": 1,
  "fineType": "OVERDUE",
  "amount": 50000,          // 500 PKR/units
  "reason": "Book returned 5 days late",
  "notes": "User contacted, accepted fine"
}

Response: 200 OK
{
  "id": 1,
  "bookLoanId": 1,
  "bookTitle": "The Great Gatsby",
  "userId": 5,
  "userName": "Ahmed Ali",
  "fineType": "OVERDUE",
  "amount": 50000,
  "fineStatus": "PENDING",
  ...
}
```

### 2. Initiate Fine Payment
```
POST /api/fines/{fineId}/pay?transactionId=optional_txn_id
Content-Type: application/json

Path Params:
- fineId: ID of fine to pay (e.g., 1)

Query Params:
- transactionId: Optional external transaction reference

Response: 200 OK
{
  "paymentIntentId": "pi_1234567890",
  "clientSecret": "pi_1234567890_secret_1234567890",
  "status": "requires_payment_method",
  "amount": 50000,
  "currency": "pkr"
}
```

### 3. Mark Fine as Paid (Payment Webhook/Confirmation)
```
POST /api/fines/{fineId}/mark-paid
Content-Type: application/json

Path Params:
- fineId: 1

Query Params:
- transactionID: stripe_txn_123456789

Request Body:
50000

Response: 200 OK
(Empty response or success message)
```

### 4. Waive Fine (Admin Only)
```
POST /api/fines/waive-fine
Content-Type: application/json

Request Body:
{
  "fineId": 1,
  "reason": "User is financially disadvantaged, waived as humanitarian gesture"
}

Response: 200 OK
{
  "id": 1,
  "userId": 5,
  "userName": "Ahmed Ali",
  "fineStatus": "WAIVED",
  "waivedByUserId": 2,
  "waivedByUserName": "Admin User",
  "waivedAt": "2026-04-07T14:35:22.123456",
  "waivedReason": "User is financially disadvantaged...",
  ...
}
```

### 5. Get My Fines (Current User)
```
GET /api/fines/my-fines?status=PENDING&type=OVERDUE

Query Params:
- status: Filter by status (PENDING, PAID, WAIVED, PARTIALLY_PAID) - Optional
- type: Filter by type (OVERDUE, DAMAGE, LOST, PROCESSING) - Optional

Response: 200 OK
[
  {
    "id": 1,
    "bookLoanId": 1,
    "bookTitle": "The Great Gatsby",
    "userId": 5,
    "fineType": "OVERDUE",
    "amount": 50000,
    "fineStatus": "PENDING",
    "createdAt": "2026-03-15T10:30:00"
  },
  ...
]
```

### 6. Get All Fines (Admin Only) - Paginated
```
GET /api/fines/all?status=PENDING&type=OVERDUE&userId=5&page=0&size=10

Query Params:
- status: Filter by status - Optional
- type: Filter by type - Optional
- userId: Filter by specific user ID - Optional
- page: Page number (0-indexed) - Default: 0
- size: Page size - Default: 10

Response: 200 OK
{
  "content": [
    {
      "id": 1,
      "bookLoanId": 1,
      "userId": 5,
      "fineType": "OVERDUE",
      "amount": 50000,
      "fineStatus": "PENDING",
      ...
    },
    ...
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 23,
  "totalPages": 3,
  "isLast": false,
  "isFirst": true,
  "isEmpty": false
}
```

---

## 🧪 Postman Testing Guide

### Setup Prerequisites

1. **Base URL**: `http://localhost:8080`
2. **Authentication**: Bearer token in Authorization header
3. **Content-Type**: application/json

### Postman Environment Variables

Create these variables in your Postman environment:

```
baseUrl = http://localhost:8080
token = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... (your JWT token)
fineId = 1
userId = 5
bookLoanId = 1
```

### Test Case 1: Create a Fine (Admin)

**Description**: Admin creates a fine for an overdue book

**Request**:
```
POST {{baseUrl}}/api/fines/create-fine
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "bookLoanId": 1,
  "fineType": "OVERDUE",
  "amount": 50000,
  "reason": "Book returned 5 days after due date",
  "notes": "User confirmed. Will be paid next week."
}
```

**Expected Response**: 
- Status: `200 OK`
- Body contains: id, userId, fineStatus = "PENDING", amount = 50000

**Validation Steps**:
1. ✅ Response status is 200
2. ✅ fineStatus is "PENDING"
3. ✅ amount matches request (50000)
4. ✅ createdAt is populated
5. ✅ Save response id as `fineId` for next tests

**Common Errors**:
- 404: BookLoan not found → Verify bookLoanId exists
- 403: Unauthorized → User is not admin
- 400: Validation error → Check required fields

---

### Test Case 2: Get My Fines (Current User)

**Description**: User retrieves their own fines with optional filters

**Request 2a - No Filters**:
```
GET {{baseUrl}}/api/fines/my-fines
Authorization: Bearer {{token}}
```

**Request 2b - Filter by Status**:
```
GET {{baseUrl}}/api/fines/my-fines?status=PENDING
Authorization: Bearer {{token}}
```

**Request 2c - Filter by Type**:
```
GET {{baseUrl}}/api/fines/my-fines?type=OVERDUE
Authorization: Bearer {{token}}
```

**Request 2d - Filter by Both Status and Type**:
```
GET {{baseUrl}}/api/fines/my-fines?status=PENDING&type=OVERDUE
Authorization: Bearer {{token}}
```

**Expected Response**:
- Status: `200 OK`
- Body: Array of FineDto objects
- Each fine belongs to current user

**Validation Steps**:
1. ✅ Response status is 200
2. ✅ Response is an array
3. ✅ All items have userId matching current user
4. ✅ If filtered by status, all items match that status
5. ✅ If filtered by type, all items match that type
6. ✅ Can identify the fine created in Test Case 1

**Common Errors**:
- 401: Unauthorized → Token missing or invalid
- 200 with empty array → Fine might not exist for this user

---

### Test Case 3: Get All Fines (Admin Pagination)

**Description**: Admin retrieves all fines with pagination and filters

**Request 3a - Page 0, Size 10**:
```
GET {{baseUrl}}/api/fines/all?page=0&size=10
Authorization: Bearer {{token}}
```

**Request 3b - Filter by Specific User**:
```
GET {{baseUrl}}/api/fines/all?userId=5&page=0&size=10
Authorization: Bearer {{token}}
```

**Request 3c - Complex Filter**:
```
GET {{baseUrl}}/api/fines/all?status=PENDING&type=OVERDUE&userId=5&page=0&size=5
Authorization: Bearer {{token}}
```

**Expected Response**:
```json
{
  "content": [
    {
      "id": 1,
      "userId": 5,
      "fineStatus": "PENDING",
      ...
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 5,
  "totalPages": 1,
  "isLast": true,
  "isFirst": true,
  "isEmpty": false
}
```

**Validation Steps**:
1. ✅ Response status is 200
2. ✅ Contains pagination metadata
3. ✅ content array has items
4. ✅ pageNumber matches request (0)
5. ✅ pageSize matches request (10)
6. ✅ totalElements > 0
7. ✅ If filtered by userId, all items have matching userId
8. ✅ If status/type filters applied, all match

**Common Errors**:
- 403: Unauthorized → Only admin can access /all endpoint
- 400: Invalid page/size → Must be non-negative integers

---

### Test Case 4: Initiate Fine Payment

**Description**: User initiates payment for their fine

**Request**:
```
POST {{baseUrl}}/api/fines/{{fineId}}/pay
Authorization: Bearer {{token}}
```

**Optional with Transaction ID**:
```
POST {{baseUrl}}/api/fines/{{fineId}}/pay?transactionId=external_txn_12345
Authorization: Bearer {{token}}
```

**Expected Response**:
```json
{
  "paymentIntentId": "pi_1234567890abcdef",
  "clientSecret": "pi_1234567890abcdef_secret_xyz123",
  "status": "requires_payment_method",
  "amount": 50000,
  "currency": "pkr",
  "createdAt": "2026-04-07T14:00:00"
}
```

**Validation Steps**:
1. ✅ Response status is 200
2. ✅ paymentIntentId is present and valid format (starts with pi_)
3. ✅ clientSecret is present
4. ✅ amount matches fine amount (50000)
5. ✅ status is "requires_payment_method" or "succeeded"
6. ✅ Save paymentIntentId for webhook/confirmation

**Common Errors**:
- 404: Fine not found → Invalid fineId
- 400: Fine already paid → Can't pay PAID/WAIVED fines
- 400: Fine is waived → Can't pay waived fines
- 500: Payment gateway error → Stripe integration issue

**Next Step**: Complete payment in Stripe Dashboard or use client library

---

### Test Case 5: Mark Fine as Paid (Webhook/Confirmation)

**Description**: After successful Stripe payment, mark fine as paid

**Request**:
```
POST {{baseUrl}}/api/fines/{{fineId}}/mark-paid?transactionID=stripe_txn_1234567890
Authorization: Bearer {{token}}
Content-Type: application/json

50000
```

**Explanation**:
- Path: {{fineId}} - The fine ID to mark as paid
- Query Param: transactionID - Stripe transaction/payment intent ID
- Body: 50000 - The amount paid (in paisa/paise/cents)

**Expected Response**:
- Status: `200 OK`
- Body: Empty or success message

**Validation Steps**:
1. ✅ Response status is 200
2. ✅ No errors returned
3. ✅ Verify fine status is now "PAID" by calling Test Case 2 again
4. ✅ paidAt field should be populated
5. ✅ transactionId should match

**Verification (Follow-up)**:
```
GET {{baseUrl}}/api/fines/my-fines
Authorization: Bearer {{token}}
```

Check response: The fine should now have:
- fineStatus: "PAID"
- paidAt: "2026-04-07T14:30:00" (current timestamp)
- transactionId: "stripe_txn_1234567890"

**Common Errors**:
- 404: Fine not found → Invalid fineId
- 400: Validation error → Check amount format

---

### Test Case 6: Waive Fine (Admin Only)

**Description**: Admin waives a fine instead of requiring payment

**Prerequisites**:
- Fine status must be PENDING (not already PAID or WAIVED)
- Current user must be admin

**Request**:
```
POST {{baseUrl}}/api/fines/waive-fine
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "fineId": 1,
  "reason": "User is economically disadvantaged. Waived as per humanitarian policy."
}
```

**Expected Response**:
```json
{
  "id": 1,
  "userId": 5,
  "userName": "Ahmed Ali",
  "fineStatus": "WAIVED",
  "waivedByUserId": 2,
  "waivedByUserName": "Admin User",
  "waivedAt": "2026-04-07T15:00:00.123456",
  "waivedReason": "User is economically disadvantaged. Waived as per humanitarian policy.",
  "amount": 50000,
  ...
}
```

**Validation Steps**:
1. ✅ Response status is 200
2. ✅ fineStatus changed to "WAIVED"
3. ✅ waivedByUserId matches current admin user
4. ✅ waivedAt is populated (current timestamp)
5. ✅ waivedReason matches request
6. ✅ Response includes all fine details

**Follow-up Verification**:
```
GET {{baseUrl}}/api/fines/my-fines?status=WAIVED
Authorization: Bearer {{token}}
```

The waived fine should appear in the results.

**Common Errors**:
- 404: Fine not found → Invalid fineId
- 400: Fine already paid → Can't waive PAID fines
- 400: Fine already waived → Can't waive twice
- 403: Unauthorized → Only admin can waive fines
- 400: Validation error → Check required fields

**After Waiving**:
- User cannot pay this fine anymore
- Fine disappears from "unpaid" lists
- Remains in history for audit purposes

---

### Test Case 7: Error Scenarios

#### 7a - Invalid Fine ID
```
GET {{baseUrl}}/api/fines/all?userId=9999
Authorization: Bearer {{token}}

Expected: 200 OK with empty content array
```

#### 7b - Unauthorized Access
```
GET {{baseUrl}}/api/fines/all
(No Authorization header)

Expected: 401 Unauthorized
Response: {
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

#### 7c - Invalid Filters
```
GET {{baseUrl}}/api/fines/my-fines?status=INVALID_STATUS
Authorization: Bearer {{token}}

Expected: 400 Bad Request
Response includes validation error details
```

#### 7d - Double Payment Attempt
```
POST {{baseUrl}}/api/fines/{{fineId}}/pay
Authorization: Bearer {{token}}

(After already completing payment)

Expected: 400 Bad Request
Response: "Fine is already paid"
```

#### 7e - Waive Already Paid Fine
```
POST {{baseUrl}}/api/fines/waive-fine
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "fineId": 1,
  "reason": "Trying to waive paid fine"
}

Expected: 400 Bad Request
Response: "Fine is already paid and Cannot be waived"
```

---

## 📊 Response Examples

### Full FineDto Response Example

```json
{
  "id": 1,
  "bookLoanId": 15,
  "bookTitle": "The Great Gatsby",
  "bookIsbn": "978-0-7432-7356-5",
  "userId": 5,
  "userName": "Ahmed Ali",
  "userEmail": "ahmed.ali@library.com",
  "fineType": "OVERDUE",
  "amount": 50000,
  "amountPaid": 50000,
  "amountOutstanding": 0,
  "fineStatus": "PAID",
  "reason": "Book returned 5 days after due date",
  "notes": "User confirmed the fine, will be paid next week",
  "waivedByUserId": null,
  "waivedByUserName": null,
  "waivedAt": null,
  "waivedReason": null,
  "paidAt": "2026-04-07T14:30:00",
  "processedByUserId": 2,
  "processedByUserName": "Admin User",
  "transactionId": "stripe_pi_1234567890abcdef",
  "createdAt": "2026-03-15T10:30:00",
  "updatedAt": "2026-04-07T14:30:00"
}
```

### PageResponse Structure

```json
{
  "content": [
    { /* FineDto object 1 */ },
    { /* FineDto object 2 */ },
    { /* FineDto object 3 */ }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 23,
  "totalPages": 3,
  "isLast": false,
  "isFirst": true,
  "isEmpty": false
}
```

### Error Response Examples

**Validation Error**:
```json
{
  "timestamp": "2026-04-07T14:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    {
      "field": "amount",
      "message": "Fine amount must be zero or positive"
    }
  ]
}
```

**Not Found Error**:
```json
{
  "timestamp": "2026-04-07T14:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Fine not found with id: 999"
}
```

**Unauthorized Error**:
```json
{
  "timestamp": "2026-04-07T14:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource"
}
```

---

## ⚠️ Error Handling

### Exception Types

| Exception | HTTP Status | Scenario | Resolution |
|-----------|-------------|----------|-----------|
| `RuntimeException` | 500 | Fine not found | Verify fineId exists |
| `RuntimeException` | 400 | Fine already paid | Cannot pay twice |
| `RuntimeException` | 400 | Fine already waived | Cannot pay waived fine |
| `PaymentException` | 400 | Invalid payment amount | Amount must be > 0 |
| `ValidationException` | 400 | Invalid input data | Check all required fields |
| `UnauthorizedException` | 403 | No proper permissions | User must be admin |
| `AuthenticationException` | 401 | Missing/invalid token | Provide valid JWT token |

### Best Practices for Error Handling

1. **Always check HTTP status code** before processing response
2. **Validate all required fields** before making requests
3. **Handle idempotency** - payments should be idempotent
4. **Log all errors** for debugging
5. **Retry logic** for network failures (not for business logic errors)
6. **User-friendly messages** when displaying to end users

---

## 🧩 Integration Checklist

Before going to production, ensure:

- [ ] All 7 test cases pass successfully
- [ ] Error scenarios are handled gracefully
- [ ] Pagination works with large datasets
- [ ] Payment integration with Stripe is tested
- [ ] Admin-only endpoints are properly secured
- [ ] LAZY loading doesn't cause LazyInitializationException
- [ ] Transaction IDs are properly tracked
- [ ] Timestamps (createdAt, updatedAt) are accurate
- [ ] Audit trail for waived fines is complete
- [ ] User can only see their own fines (unless admin)
- [ ] Database transactions are properly managed
- [ ] Performance is acceptable with pagination

---

## 📝 Notes

### Important Points

1. **Amounts are in smallest currency units**
   - 50000 = 500 PKR or 500 INR or $5 USD
   - Always divide by 100 for display

2. **LAZY Loading**
   - `processedBy` field uses LAZY loading
   - Access it within transaction context only
   - Outside transaction → LazyInitializationException

3. **Fine Lifecycle**
   - Once PAID or WAIVED, status cannot be changed
   - No partial payment support (yet)
   - PENDING is the only state allowing actions

4. **Concurrent Requests**
   - Multiple payment attempts should be prevented
   - Use database constraints or optimistic locking

5. **Audit Trail**
   - All fine actions are logged with timestamps
   - Admin actions (waive) record admin user ID
   - Modification timestamps track all updates

---

## 📚 Related Modules

- **BookLoan Module**: Creates fines based on overdue loans
- **Payment Module**: Handles fine payment processing
- **User Module**: Manages user roles and permissions
- **Notification Module**: Could send fine alerts to users

---

**Last Updated**: April 7, 2026  
**Module Status**: Production Ready  
**API Version**: 1.0


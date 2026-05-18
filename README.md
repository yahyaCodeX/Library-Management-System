<div align="center">

# 📚 Library Management System

### A Full-Featured RESTful API for Modern Library Operations

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Stripe](https://img.shields.io/badge/Stripe-Payments-635bff?style=for-the-badge&logo=stripe&logoColor=white)](https://stripe.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

<br/>

*A production-ready backend system built with Spring Boot 4 that manages books, users, loans, reservations, fines, subscriptions, payments, and more — secured with JWT & OAuth2.*

---

</div>

## 🏗️ Architecture Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                        CLIENT (Frontend / Postman)               │
└──────────────────────────┬───────────────────────────────────────┘
                           │  HTTP / REST
┌──────────────────────────▼───────────────────────────────────────┐
│                     SPRING SECURITY LAYER                        │
│          JWT Filter  ·  OAuth2 (Google)  ·  CORS Config          │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                      CONTROLLER LAYER (REST APIs)                │
│  Auth · Book · BookLoan · Reservation · Fine · Payment           │
│  Subscription · Genre · Review · WishList · Admin · User         │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                       SERVICE LAYER (Business Logic)             │
│  Interface-based design with dedicated Impl classes              │
│  + StripeService gateway  ·  EmailService                        │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                     REPOSITORY LAYER (Spring Data JPA)           │
│  BookRepo · UserRepo · FineRepo · PaymentRepo · etc.            │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                          MySQL Database                          │
└──────────────────────────────────────────────────────────────────┘
```

---

## ✨ Features

### 📖 Book Management
- Full CRUD operations with soft-delete & hard-delete support
- Bulk book creation for efficient catalog population
- Advanced search with filters (genre, availability, pagination, sorting)
- Book statistics dashboard (total active & available counts)
- ISBN-based lookup

### 🔐 Authentication & Authorization
- **JWT-based** stateless authentication (signup, login, token generation)
- **Google OAuth2** social login with custom success handler
- Password reset via email with secure token-based flow
- Role-based access control (`USER` / `ADMIN`)
- Method-level security with `@PreAuthorize`

### 📋 Book Loans
- Checkout & check-in workflow
- Admin checkout on behalf of users
- Loan renewal system
- Personal loan history with status filtering
- Automated overdue detection & status updates

### 📌 Reservations
- Reserve books when unavailable
- Admin-managed reservation fulfillment
- Cancellation support
- Advanced search with status/active filters & pagination

### 💰 Fine Management
- Admin-created fines (overdue, damage, lost books, etc.)
- **Stripe-integrated** fine payment with checkout sessions
- Admin fine waiver with reason tracking
- Manual mark-as-paid for cash payments
- Filter by status (`PENDING`, `PAID`, `WAIVED`) and type

### 💳 Payments (Stripe Integration)
- Secure payment initiation with Stripe Checkout Sessions
- **Webhook-based** automatic payment verification (HMAC-signed)
- Manual payment verification fallback
- Admin payment dashboard with pagination & sorting

### 🎫 Subscriptions
- Subscription plan management (CRUD for plans)
- User subscription with Stripe payment flow
- Active subscription tracking per user
- Admin activation & deactivation controls
- Automatic expired subscription cleanup
- Cancellation with reason tracking

### ⭐ Book Reviews
- User book reviews with ratings
- Review management per book

### 💝 Wishlist
- Personal wishlist management
- Add/remove books with pagination

### 📧 Email Service
- Password reset email delivery via SMTP (Gmail)
- Configurable mail templates

### 📂 Genre Management
- Full CRUD for book genres/categories
- Genre-based book filtering

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 4.0.2 |
| **Language** | Java 21 |
| **Security** | Spring Security 6 + JWT (jjwt 0.12.5) + OAuth2 |
| **Database** | MySQL 8+ with Spring Data JPA / Hibernate |
| **Payments** | Stripe Java SDK 25.3.0 |
| **Email** | Spring Boot Mail (SMTP) |
| **Validation** | Jakarta Bean Validation |
| **Build Tool** | Maven |
| **Utilities** | Lombok |

---

## 📁 Project Structure

```
src/main/java/com/librarymanagment/librarymanagment/
├── LibraryManagmentApplication.java       # Application entry point
├── config/
│   ├── SpringSecurityConfig.java          # Security filter chain & rules
│   ├── OAuth2SuccessHandler.java          # Google OAuth2 callback handler
│   ├── StripeConfig.java                  # Stripe API key configuration
│   └── WebConfig.java                     # CORS configuration
├── controller/
│   ├── AuthController.java                # Signup, Login, Password Reset
│   ├── AdminController.java               # Admin-specific endpoints
│   ├── BookController.java                # Book CRUD & search
│   ├── BookLoanController.java            # Checkout, Check-in, Renew
│   ├── BookReviewController.java          # Book reviews
│   ├── FineController.java                # Fine creation, payment, waiver
│   ├── GenreController.java               # Genre CRUD
│   ├── PaymentController.java             # Payment initiation & webhooks
│   ├── ReservationController.java         # Book reservations
│   ├── SubscriptionController.java        # User subscriptions
│   ├── SubscriptionPlanController.java    # Subscription plan management
│   ├── UserController.java                # User profile
│   └── WishListController.java            # Wishlist management
├── dto/                                   # Data Transfer Objects
│   ├── request/                           # Incoming request DTOs
│   └── response/                          # Outgoing response DTOs
├── entity/                                # JPA Entities
│   ├── Book.java
│   ├── BookLoan.java
│   ├── BookReview.java
│   ├── Fine.java
│   ├── Genre.java
│   ├── PasswordResetToken.java
│   ├── Payment.java
│   ├── Reservation.java
│   ├── Subscription.java
│   ├── SubscriptionPlan.java
│   ├── User.java
│   └── WishList.java
├── constant/                              # Enums (BookLoanStatus, FineStatus, etc.)
├── exception/                             # Custom exceptions & global handler
├── filter/
│   └── JwtFilter.java                     # JWT authentication filter
├── mapper/                                # Entity ↔ DTO mappers
├── repository/                            # Spring Data JPA repositories
├── service/
│   ├── *Service.java                      # Service interfaces
│   ├── Implementations/                   # Service implementations
│   │   ├── AuthServiceImpl.java
│   │   ├── BookServiceImpl.java
│   │   ├── BookLoanServiceImpl.java
│   │   ├── FineServiceImpl.java
│   │   ├── PaymentServiceImpl.java
│   │   ├── ReservationServiceImpl.java
│   │   ├── SubscriptionServiceImpl.java
│   │   ├── EmailService.java
│   │   └── ...
│   └── Gateway/
│       └── StripeService.java             # Stripe payment gateway
└── utils/
    └── JwtUtil.java                       # JWT token generation & validation
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or later
- **Maven 3.8+**
- **MySQL 8.0+**
- **Stripe Account** (for payment features)
- **Gmail Account** (for email features, with App Password)

### 1. Clone the Repository

```bash
git clone https://github.com/yahyaCodeX/Library-Management-System.git
cd Library-Management-System
```

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE librarymanagmentsystem;
```

### 3. Set Environment Variables

The application uses environment variables for sensitive configuration. Set the following before running:

| Variable | Description |
|----------|-------------|
| `jwt-secret-key` | Secret key for JWT token signing |
| `your-email-password` | Gmail App Password for SMTP |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |
| `STRIPE_SECRET_KEY` | Stripe secret API key |
| `STRIPE_PUBLISHABLE_KEY` | Stripe publishable API key |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret |

> **Tip:** You can set these in your IDE run configuration, or create a `.env` file and use a tool like `spring-dotenv`.

### 4. Build & Run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## 📡 API Reference

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/auth/signup` | Register a new user | ❌ |
| `POST` | `/auth/login` | Login & get JWT token | ❌ |
| `POST` | `/auth/forgot-password` | Request password reset email | ❌ |
| `POST` | `/auth/reset-password` | Reset password with token | ❌ |

### Books

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/books/create/bulk` | Create books in bulk | 🔑 Admin |
| `GET` | `/api/books` | Search books (filters + pagination) | 🔑 |
| `GET` | `/api/books/{id}` | Get book by ID | 🔑 |
| `GET` | `/api/books/isbn/{isbn}` | Get book by ISBN | 🔑 |
| `POST` | `/api/books/search` | Advanced search | 🔑 |
| `PUT` | `/api/books/update/{id}` | Update a book | 🔑 Admin |
| `DELETE` | `/api/books/delete/{id}` | Soft-delete a book | 🔑 Admin |
| `DELETE` | `/api/books/hard-delete/{id}` | Permanently delete a book | 🔑 Admin |
| `GET` | `/api/books/stats` | Get book statistics | 🔑 |

### Book Loans

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/book-loans/checkout` | Checkout a book | 🔑 |
| `POST` | `/api/book-loans/checkout/user/{userId}` | Admin checkout for user | 🔑 Admin |
| `POST` | `/api/book-loans/checkin` | Return a book | 🔑 |
| `POST` | `/api/book-loans/renew` | Renew a loan | 🔑 |
| `GET` | `/api/book-loans/MyBook-Loans` | Get my loans | 🔑 |
| `GET` | `/api/book-loans/search` | Search all loans | 🔑 Admin |
| `POST` | `/api/book-loans/admin/update-overdue` | Update overdue statuses | 🔑 Admin |

### Reservations

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/reservations` | Create a reservation | 🔑 |
| `POST` | `/api/reservations/user/{userId}` | Reserve for a user | 🔑 Admin |
| `DELETE` | `/api/reservations/{id}` | Cancel a reservation | 🔑 |
| `POST` | `/api/reservations/{id}/fulfill` | Fulfill a reservation | 🔑 Admin |
| `GET` | `/api/reservations/my` | Get my reservations | 🔑 |
| `GET` | `/api/reservations/search` | Search reservations | 🔑 Admin |

### Fines

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/fines/create-fine` | Create a fine | 🔑 Admin |
| `POST` | `/api/fines/{fineId}/pay` | Pay a fine (Stripe) | 🔑 |
| `POST` | `/api/fines/{fineId}/mark-paid` | Mark fine as paid | 🔑 Admin |
| `POST` | `/api/fines/waive-fine` | Waive a fine | 🔑 Admin |
| `GET` | `/api/fines/my-fines` | Get my fines | 🔑 |
| `GET` | `/api/fines/all` | Get all fines | 🔑 Admin |

### Payments

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/payments/initiate` | Start a payment | 🔑 |
| `POST` | `/api/payments/verify` | Verify a payment | 🔑 |
| `POST` | `/api/payments/webhook/stripe` | Stripe webhook callback | ❌ |
| `GET` | `/api/payments/all` | Get all payments | 🔑 Admin |

### Subscriptions

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/subscriptions/subscribe` | Subscribe to a plan | 🔑 |
| `GET` | `/api/subscriptions/active/me` | Get my active subscription | 🔑 |
| `GET` | `/api/subscriptions/active/user/{userId}` | Get user's subscription | 🔑 Admin |
| `PATCH` | `/api/subscriptions/{id}/cancel` | Cancel subscription | 🔑 |
| `PATCH` | `/api/subscriptions/{id}/activate` | Activate subscription | 🔑 Admin |
| `PATCH` | `/api/subscriptions/deactivate-expired` | Cleanup expired | 🔑 Admin |
| `GET` | `/api/subscriptions/all` | Get all subscriptions | 🔑 Admin |

### Wishlist

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/wishlist/add/{bookId}` | Add to wishlist | 🔑 |
| `DELETE` | `/api/wishlist/remove/{bookId}` | Remove from wishlist | 🔑 |
| `GET` | `/api/wishlist/my-wishlist` | Get my wishlist | 🔑 |

---

## 🔒 Security

- **JWT Authentication**: Stateless token-based auth with configurable expiry
- **OAuth2 Social Login**: Google sign-in integration
- **Password Hashing**: BCrypt encryption for stored passwords
- **Role-Based Access**: `USER` and `ADMIN` roles with method-level security
- **Stripe Webhook Verification**: HMAC signature validation for payment webhooks
- **CORS Configuration**: Configurable cross-origin resource sharing
- **Input Validation**: Jakarta Bean Validation on all request DTOs

---

## 🗄️ Database Schema (Entities)

| Entity | Description |
|--------|-------------|
| `User` | User accounts with roles |
| `Book` | Book catalog with soft-delete |
| `Genre` | Book categories |
| `BookLoan` | Checkout/check-in records |
| `BookReview` | User book reviews & ratings |
| `Reservation` | Book reservation queue |
| `Fine` | Overdue/damage fines |
| `Payment` | Stripe payment records |
| `Subscription` | User subscription records |
| `SubscriptionPlan` | Available subscription tiers |
| `PasswordResetToken` | Secure password reset tokens |
| `WishList` | User wishlist items |

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**Yahya Siddiqui** — [@yahyaCodeX](https://github.com/yahyaCodeX)

---

<div align="center">

⭐ **Star this repo if you find it useful!** ⭐

</div>

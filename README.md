# CraveDash – Multi-Restaurant Food Ordering & Delivery Orchestration Engine (PS034)

CraveDash is a high-throughput microservices-based food ordering and fulfillment platform connecting Consumers, Restaurant Partners, Order Fulfillment, and Payment Processing.

## Architecture

```text
CraveDash/
├── eureka-server        (Port 8761) - Eureka Service Discovery Registry
├── api-gateway          (Port 8080) - Gateway Web MVC Routing, Load Balancing & JWT Security
├── auth-service         (Port 8081) - User Registration, Login, BCrypt & JWT Issuance
├── restaurant-service   (Port 8082) - Restaurant & Menu Management, Real-time Availability
├── order-service        (Port 8083) - Multi-item Order Orchestration & OpenFeign Integration
└── payment-service      (Port 8084) - Payment Transaction Processing & Status Lifecycle
```

## Tech Stack

- **Language**: Java 17+
- **Framework**: Spring Boot 3.2.5, Spring Cloud 2023.0.1
- **Service Discovery**: Netflix Eureka Server & Client
- **API Gateway**: Spring Cloud Gateway Server Web MVC with LoadBalancer
- **Security**: Spring Security, JWT (HMAC-SHA256), BCrypt
- **Inter-Service Communication**: OpenFeign
- **Database**: PostgreSQL (`cravedash_auth_db`, `cravedash_restaurant_db`, `cravedash_order_db`, `cravedash_payment_db`) / H2 fallback
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven

## Ports

| Service | Port |
| :--- | :---: |
| Eureka Server | `8761` |
| API Gateway | `8080` |
| Auth Service | `8081` |
| Restaurant Service | `8082` |
| Order Service | `8083` |
| Payment Service | `8084` |

## Build & Test

```bash
mvn clean test
```

## How to Run

1. Start `eureka-server` (Port 8761)
2. Start `auth-service` (Port 8081)
3. Start `restaurant-service` (Port 8082)
4. Start `payment-service` (Port 8084)
5. Start `order-service` (Port 8083)
6. Start `api-gateway` (Port 8080)

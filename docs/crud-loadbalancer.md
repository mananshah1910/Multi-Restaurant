# CRUD via API Gateway with Load Balancer

## Overview
This guide shows how to expose **Create, Read, Update, Delete (CRUD)** operations of any microservice through the **Spring Cloud Gateway** while leveraging **Spring Cloud LoadBalancer** (Eureka‑based) to distribute requests across multiple service instances.

---
### 1️⃣ Add/Update Route Configuration
Create or edit `GatewayConfig.java` (usually under `src/main/java/com/cravedash/gateway/config`).
```java
package com.cravedash.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // ------ Restaurant Service CRUD ------
            .route("restaurant_crud", r -> r.path("/restaurants/**")
                .and()
                .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.GET)
                .uri("lb://RESTAURANT-SERVICE"))
            // ------ Order Service CRUD (example) ------
            .route("order_crud", r -> r.path("/orders/**")
                .and()
                .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.GET)
                .uri("lb://ORDER-SERVICE"))
            // Add more services in the same pattern …
            .build();
    }
}
```
* `lb://SERVICE-NAME` tells the gateway to use **LoadBalancer**. The service name must match the **Eureka** registration name (e.g., `RESTAURANT-SERVICE`).
* The same route works for **all CRUD HTTP verbs**; you can further restrict methods per‑operation if desired.

---
### 2️⃣ Run Multiple Instances (Scaling)
Start additional instances of a microservice on different ports. Example for *restaurant‑service*:
```bash
# First instance (default 8081)
java -jar restaurant-service.jar --server.port=8081 &
# Second instance
java -jar restaurant-service.jar --server.port=8085 &
```
Both instances register with Eureka. The gateway will round‑robin between them.

---
### 3️⃣ Verify Load‑Balancing
Send a few requests and inspect the `X-Instance-Id` header (added by Spring Cloud LoadBalancer).
```bash
for i in {1..5}; do
  curl -s -i http://localhost:8080/restaurants/1 | grep X-Instance-Id
done
```
You should see different instance IDs, confirming distribution.

---
### 4️⃣ Test CRUD End‑Points
Assuming the services are running **Eureka → Auth → Restaurant → Order → Payment → API‑Gateway** (gateway on `8080`).
#### Create (POST)
```bash
curl -X POST http://localhost:8080/restaurants \
  -H "Content-Type: application/json" \
  -d '{"name":"Sushi Place","address":"123 Ocean Ave"}'
```
#### Read (GET)
```bash
curl http://localhost:8080/restaurants/1
```
#### Update (PUT)
```bash
curl -X PUT http://localhost:8080/restaurants/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Sushi Palace","address":"123 Ocean Ave"}'
```
#### Delete (DELETE)
```bash
curl -X DELETE http://localhost:8080/restaurants/1
```
All calls go **through the gateway** (`localhost:8080`) – you never need to know the underlying service ports.

---
### 5️⃣ Verify Persistence in PostgreSQL
Connect to the corresponding database and query the table.
```bash
psql -U postgres -d cravedash_restaurant_db -c "SELECT * FROM restaurants;"
```
You should see the rows created/updated via the gateway.

---
### 6️⃣ Diagram – Request Flow
```mermaid
flowchart TD
    Client[Client (curl / Postman)] -->|HTTP request| GW[API Gateway (8080)]
    GW -->|Load‑balanced via Eureka| RS1[Restaurant Service Instance 1]
    GW -->|Load‑balanced via Eureka| RS2[Restaurant Service Instance 2]
    RS1 -->|JPA / PostgreSQL| DB[(cravedash_restaurant_db)]
    RS2 -->|JPA / PostgreSQL| DB
    style GW fill:#f9f,stroke:#333,stroke-width:2px
    style RS1 fill:#bbf,stroke:#333,stroke-width:1px
    style RS2 fill:#bbf,stroke:#333,stroke-width:1px
```
The client hits a **single base URL** (`http://localhost:8080`). The gateway resolves the target service via Eureka, picks an instance using the load‑balancer, forwards the request, and the service persists data in PostgreSQL.

---
## Quick Checklist
- [ ] Add routes for each microservice (pattern shown above). 
- [ ] Ensure each service registers with **Eureka** using the same `eureka.instance.appname`. 
- [ ] Run multiple instances to see load‑balancing in action. 
- [ ] Test all CRUD operations via the gateway. 
- [ ] Verify DB state with `psql`.

**That's it!** Copy‑paste the snippets, adjust service names as needed, and you have a fully load‑balanced CRUD gateway.

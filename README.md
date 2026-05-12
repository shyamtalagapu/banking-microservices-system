# Banking Microservices System

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.1-6DB33F?style=flat-square&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939?style=flat-square&logo=jenkins&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS-EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white)

A production-grade banking backend built with **6 Spring Boot microservices**, featuring JWT authentication with role-based access control, Saga-pattern fund transfers with compensating transactions, circuit breakers, distributed tracing, and a fully automated CI/CD pipeline deploying to AWS EC2.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Key Design Decisions](#key-design-decisions)
- [Microservices Breakdown](#microservices-breakdown)
- [Inter-Service Communication](#inter-service-communication)
- [Security Model](#security-model)
- [Saga Pattern — Fund Transfer Flow](#saga-pattern--fund-transfer-flow)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [CI/CD Pipeline](#cicd-pipeline)
- [Database Design](#database-design)
- [API Reference](#api-reference)
- [How to Run](#how-to-run)

---

## Architecture Overview

```
                          ┌──────────────────────────────────────────────────────┐
                          │                  Docker Network                      │
                          │                                                      │
  Client ──► API Gateway (:8085)                                                │
                │  ├─ JWT validation (GlobalFilter)                              │
                │  ├─ Role-based route authorization                             │
                │  └─ Load-balanced routing via Eureka                           │
                │                                                                │
                ├──► Auth Service (:8083)        ──► MySQL (userdetails)         │
                ├──► Customer Service (:8086)    ──► MySQL (customerdb)          │
                ├──► Accounts Service (:8081)    ──► MySQL (accountsdb)          │
                └──► Transaction Service (:8082) ──► MySQL (transactiondb)      │
                                                                                 │
                     Discovery Server (:8761)  ← all services register here     │
                          │                                                      │
                          └──────────────────────────────────────────────────────┘
```

Every request enters through the **API Gateway** — the only publicly exposed port. The gateway validates the JWT token, extracts the user role from the token claims, enforces route-level authorization, and routes the request to the correct downstream service discovered dynamically via **Eureka**. Business services do not have hardcoded URLs; all inter-service calls use logical service names resolved through client-side load balancing.

---

## Key Design Decisions

| Decision | Implementation | Why It Matters |
|---|---|---|
| **Saga Pattern for fund transfers** | `AccountService.transferAmmountSagaImplementation()` orchestrates debit → credit → transaction-log across two services with compensating rollback | Ensures data consistency across services without distributed transactions (2PC) |
| **Pessimistic locking on account balance** | `@Lock(PESSIMISTIC_WRITE)` on `AccountRepository.findByIdOrUpdate()` | Prevents race conditions and double-spend during concurrent transfers |
| **Circuit Breaker on inter-service calls** | Resilience4j `@CircuitBreaker` on `TransactionClient.createPending()` with a defined fallback method | Prevents cascading failures when the Transaction Service is down |
| **Role-based access at the Gateway** | `JwtAuthenticationFilter` (GlobalFilter) checks `role` claim and enforces per-route authorization (CUSTOMER, EMPLOYEE, ADMIN) | Centralized authorization — downstream services don't need to re-validate roles |
| **Database-per-service** | Each service owns a separate MySQL schema (`userdetails`, `customerdb`, `accountsdb`, `transactiondb`) | True data isolation — no shared tables, no cross-service DB queries |
| **Profile-based configuration** | `application-local.properties` + `application-docker.properties` per service | Same codebase runs locally and in Docker without config changes |
| **Lightweight Docker images** | `eclipse-temurin:17-jre-alpine` base + JVM memory tuning (`-Xms32m -Xmx128m`) | Smaller images, faster startup, controlled memory footprint per container |
| **Auto-registration via Feign + Eureka** | Customer Service calls Auth Service via `@FeignClient`, Accounts calls Customer + Transaction via `@LoadBalanced RestTemplate` | No hardcoded IPs — services discover each other dynamically |

---

## Microservices Breakdown

### 1. API Gateway (`api-gateway/`)

**Role:** Single entry point — JWT validation, role-based routing, request filtering.

- Built on **Spring Cloud Gateway (WebFlux)** — non-blocking, reactive stack
- Implements `GlobalFilter` (`JwtAuthenticationFilter`) that intercepts every request
- Public endpoints whitelisted: `/auth/login`, `/auth/register`, `/customers/saveCustomer`
- Route-level authorization logic:
  - `/accounts/create`, `/accounts/myAccounts`, `/accounts/transferMoney` → `ROLE_CUSTOMER` only
  - `/accounts/approve`, `/customers/all` → `ROLE_EMPLOYEE` only
  - `/admin/**`, `/**/delete/**` → `ROLE_ADMIN` only
- Routes configured via `application.properties` with `lb://` prefix for load-balanced Eureka lookup
- Returns structured JSON error responses for `401 Unauthorized` and `403 Forbidden`

### 2. Auth Service (`auth-service/`)

**Role:** User registration, login, JWT token generation.

- **Spring Security** with `BCryptPasswordEncoder` for password hashing
- JWT generation using **JJWT 0.11.5** — tokens contain `sub` (username) + `role` claim, 1-hour expiry, HMAC-SHA256 signed
- Three endpoints: `/auth/register`, `/auth/login`, `/auth/internal/createUser`
- `GlobalExceptionHandler` with structured `ApiErrorResponse` for `UserNotFoundException`, `InvalidCredentialsException`, `MethodArgumentNotValidException`
- Duplicate user detection via email uniqueness check before registration

### 3. Customer Service (`customer-service/`)

**Role:** Customer onboarding with automatic auth user provisioning.

- On customer registration (`/customers/saveCustomer`):
  1. Validates input with Bean Validation (`@NotBlank`, `@Email`, `@Pattern` for phone)
  2. Checks for duplicate email
  3. Saves customer to `customerdb`
  4. Calls Auth Service via **OpenFeign** (`AuthFeignClient`) to auto-create login credentials
  5. Returns customer ID + creation timestamp
- `@Transactional` ensures customer save + auth call succeed together
- Lookup by email (`/customers/email/{email}`) used internally by Accounts Service via Feign

### 4. Accounts Service (`accounts-service/`)

**Role:** Account lifecycle management + fund transfers with Saga pattern.

- Account creation requires prior customer validation via `RestTemplate` call to Customer Service
- New accounts start with `INACTIVE` status and zero balance — require explicit approval (`/accounts/approve/{id}`)
- Credit/debit operations enforce active account status check
- `@LoadBalanced RestTemplate` for Eureka-resolved inter-service HTTP calls
- **OpenFeign** (`CustomerFeign`) for email-to-customerId resolution
- Debit checks balance before withdrawal; throws `InsufficientBalanceException` if insufficient
- **Pessimistic write lock** (`@Lock(PESSIMISTIC_WRITE)`) prevents concurrent balance updates

### 5. Transaction Service (`transactionDetails-service/`)

**Role:** Transaction ledger — records and tracks the status of every fund transfer.

- Stores every transfer as a `Transaction` entity with states: `PENDING → SUCCESS | FAILED | COMPENSATED`
- Endpoints: `/transactions/pending` (POST), `/{id}/success` (PUT), `/{id}/failed` (PUT), `/{id}/compensated` (PUT)
- Acts as the single source of truth for transfer audit trail
- Each transaction records: `fromAccountId`, `toAccountId`, `amount`, `referenceId` (UUID), `createdAt`, `status`

### 6. Discovery Server (`discovery-server/`)

**Role:** Netflix Eureka service registry.

- All microservices register on startup and heartbeat at configured intervals
- Gateway uses Eureka to resolve service names (`lb://ACCOUNTS-SERVICE`) to actual IPs
- Self-preservation enabled for production stability

---

## Inter-Service Communication

```
Customer Service ──Feign──► Auth Service          (user provisioning on registration)
Accounts Service ──Feign──► Customer Service      (email → customerId resolution)
Accounts Service ──RestTemplate──► Customer Service  (customer existence validation)
Accounts Service ──RestTemplate──► Transaction Service (Saga: create/update transaction records)
API Gateway ──Eureka──► All Services              (dynamic route resolution)
```

| Pattern | Used Where | Library |
|---|---|---|
| OpenFeign declarative client | Customer → Auth, Accounts → Customer | `spring-cloud-starter-openfeign` |
| `@LoadBalanced RestTemplate` | Accounts → Customer (validation), Accounts → Transaction (Saga) | `spring-cloud-starter-loadbalancer` |
| Eureka service discovery | All services | `spring-cloud-starter-netflix-eureka-client` |
| Circuit Breaker | Accounts → Transaction (`createPending`) | `spring-cloud-starter-circuitbreaker-resilience4j` |

---

## Security Model

```
                        ┌───────────────────────────────────────┐
                        │           JWT Token                    │
                        │  Header: { alg: HS256 }               │
                        │  Payload: {                            │
                        │    sub: "john@email.com",              │
                        │    role: "ROLE_CUSTOMER",              │
                        │    iat: 1715000000,                    │
                        │    exp: 1715003600   (1hr)             │
                        │  }                                     │
                        │  Signature: HMAC-SHA256(secret)        │
                        └───────────────────────────────────────┘
```

**Authentication flow:**
1. Client calls `POST /auth/login` with email + password
2. Auth Service validates credentials against BCrypt hash in DB
3. Returns signed JWT with username as `sub` and role in claims
4. Client sends JWT in `Authorization: Bearer <token>` header for all subsequent requests

**Authorization enforcement (at Gateway):**
- Gateway's `JwtAuthenticationFilter` validates token signature + expiry first
- Extracts `role` claim and matches against route-specific access rules
- Returns `401` for invalid/expired tokens, `403` for insufficient privileges

**Three roles:** `ROLE_CUSTOMER`, `ROLE_EMPLOYEE`, `ROLE_ADMIN` — each with distinct endpoint access.

---

## Saga Pattern — Fund Transfer Flow

The fund transfer implements an **orchestration-based Saga** in `AccountService.transferAmmountSagaImplementation()`:

```
Step 1: Create PENDING transaction record       → Transaction Service
Step 2: Debit sender account (pessimistic lock) → Local DB
Step 3: Credit receiver account                 → Local DB
Step 4: Mark transaction SUCCESS                → Transaction Service
        ──────────── Return success ────────────

        ─── If Step 2 or 3 fails: ───
Step 4a: Mark transaction FAILED                → Transaction Service
Step 4b: Compensate debit (refund sender)       → Local DB
Step 4c: Mark transaction COMPENSATED           → Transaction Service
         ──────────── Return failure ────────────
```

Each transfer gets a **UUID reference ID** for traceability. The transaction record in the ledger reflects the final state: `SUCCESS`, `FAILED`, or `COMPENSATED`.

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Cloud | Spring Cloud | 2023.0.1 |
| Service Discovery | Netflix Eureka | — |
| API Gateway | Spring Cloud Gateway (WebFlux) | — |
| Auth | Spring Security + JWT (JJWT) | 0.11.5 |
| Database | MySQL | 8.0 |
| ORM | Spring Data JPA + Hibernate | — |
| Inter-Service (declarative) | OpenFeign | — |
| Inter-Service (template) | `@LoadBalanced` RestTemplate | — |
| Resilience | Resilience4j Circuit Breaker | — |
| Tracing | Micrometer Tracing + Zipkin Reporter (Brave) | — |
| Validation | Jakarta Bean Validation | — |
| Build | Maven Multi-Module | — |
| Containerization | Docker + Docker Compose | — |
| CI/CD | Jenkins (Declarative Pipeline) | — |
| Image Registry | Docker Hub | — |
| Cloud Deployment | AWS EC2 | — |
| Docker Base Image | eclipse-temurin:17-jre-alpine | — |

---

## Project Structure

```
banking-microservices-system/
│
├── pom.xml                              ← Parent POM: Spring Boot 3.2.5 + Spring Cloud 2023.0.1
├── docker-compose.yml                   ← 7 containers: MySQL + 6 services
├── deployment-services/
│   └── Jenkinsfile                      ← 9-stage CI/CD pipeline
├── mysql-init/
│   └── init.sql                         ← Auto-creates 4 schemas + auth user on first boot
│
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml                          ← spring-cloud-starter-gateway + JJWT
│   └── src/main/java/.../
│       ├── ApiGatewayApplication.java
│       └── security/
│           ├── JwtAuthenticationFilter.java   ← GlobalFilter: JWT validation + RBAC
│           └── JwtUtility.java                ← Token parsing + role extraction
│
├── auth-service/
│   ├── Dockerfile
│   ├── pom.xml                          ← spring-boot-starter-security + JJWT
│   └── src/main/java/.../
│       ├── AuthServiceApplication.java
│       ├── config/SecurityConfig.java         ← BCrypt + endpoint security rules
│       ├── controller/AuthController.java     ← /register, /login, /internal/createUser
│       ├── jwtUtils/JwtUtility.java           ← Token generation (HS256, 1hr expiry)
│       ├── entity/UserMaster.java             ← userId, userName, email, password, role
│       ├── repository/UserRepository.java     ← findByEmail, findByUserName
│       ├── dto/                               ← LoginRequest, RegisterRequest
│       ├── responseHandlers/                  ← AuthResponse, ApiErrorResponse
│       └── exceptionHandler/                  ← GlobalExceptionHandler (4 handlers)
│
├── customer-service/
│   ├── Dockerfile
│   ├── pom.xml                          ← spring-cloud-starter-openfeign + Zipkin
│   └── src/main/java/.../
│       ├── CustomerServiceApplication.java    ← @EnableFeignClients
│       ├── controller/CustomerController.java ← CRUD + email lookup
│       ├── service/CustomerService.java       ← Save customer + auto-provision auth user
│       ├── entity/Customer.java               ← fullName, email, phone, kycVerified, role
│       ├── authRegister/AuthFeignClient.java  ← Feign → Auth Service
│       ├── auth/dto/AuthRegisterRequest.java
│       ├── repository/CustomerRepository.java
│       └── exception/                         ← CustomerNotFoundException, UserAlreadyExists
│
├── accounts-service/
│   ├── Dockerfile
│   ├── pom.xml                          ← Resilience4j + OpenFeign + JJWT + Zipkin
│   └── src/main/java/.../
│       ├── AccountServiceApllication.java     ← @EnableFeignClients, @EnableDiscoveryClient
│       ├── controller/AccountController.java  ← create, credit, debit, transfer, approve, delete
│       ├── service/AccountService.java        ← Saga transfer, compensating transactions
│       ├── service/client/TransactionClient.java ← RestTemplate + @CircuitBreaker + fallback
│       ├── config/CustomerFeign.java          ← Feign → Customer Service (email lookup)
│       ├── config/RestConfig.java             ← @LoadBalanced RestTemplate bean
│       ├── security/JwtUtil.java              ← Extract username from JWT
│       ├── security/SecurityConfig.java       ← Permit all (auth handled at Gateway)
│       ├── entity/Account.java                ← accountId, customerId, balance, status, IFSC, branch
│       ├── repository/AccountRepository.java  ← Pessimistic lock + custom queries
│       └── exception/                         ← InsufficientBalanceException, AccountDetailsNotFound
│
└── transactionDetails-service/
    ├── Dockerfile
    ├── pom.xml                          ← Eureka client + Zipkin
    └── src/main/java/.../
        ├── TransactionDetailsServiceApplication.java
        ├── controller/TransactionController.java  ← /pending, /{id}/success, /{id}/failed, /{id}/compensated
        ├── service/TransactionService.java        ← Create PENDING + status transitions
        ├── entity/Transaction.java                ← from, to, amount, status (enum), referenceId, timestamp
        ├── model/TransactionStatus.java           ← Enum: PENDING, SUCCESS, FAILED, COMPENSATED
        └── repository/TransactionDetailsRepository.java
```

Each service has `application.properties` (profile selector), `application-local.properties` (localhost DB), and `application-docker.properties` (container DB) for environment-specific configuration.

---

## CI/CD Pipeline

The `Jenkinsfile` in `deployment-services/` defines a **9-stage declarative pipeline**:

```
 git push → Jenkins
     │
     ├─ 1. Checkout Code         ── Pull latest from GitHub (main branch)
     ├─ 2. Verify Java           ── Confirm JDK 17 availability
     ├─ 3. Verify Maven          ── Confirm Maven 3 availability
     ├─ 4. Build Microservices   ── mvn clean install (compiles all 6 services)
     ├─ 5. Verify Docker         ── Confirm Docker availability
     ├─ 6. Docker Compose Build  ── Build Docker images for all services
     ├─ 7. Docker Compose Push   ── Push all images to Docker Hub
     ├─ 8. Clean Containers      ── docker compose down (graceful teardown)
     └─ 9. Deploy to AWS EC2     ── SSH into EC2, pull images, docker compose up -d
```

Images are pushed to Docker Hub under `shyamtalagapu/` namespace. The EC2 deployment uses SSH with key-based authentication.

---

## Database Design

MySQL 8 runs on port `3307` (host) → `3306` (container). On first startup, `mysql-init/init.sql` auto-creates all schemas and a dedicated `auth_user` DB user.

**Schema isolation — each service owns its data:**

| Schema | Service | Primary Table | Key Columns |
|---|---|---|---|
| `userdetails` | Auth Service | `usermaster` | userId, userName, email (unique), password (BCrypt), role |
| `customerdb` | Customer Service | `customerdetails` | id, fullName, email (unique), phone, kycVerified, role, createdAt |
| `accountsdb` | Accounts Service | `accountDetails` | id, accountId, customerId, balance (BigDecimal), accountStatus, accountName, IFSCCode, bankBranch, createdAt |
| `transactiondb` | Transaction Service | `transaction_records` | id, fromAccountId, toAccountId, amount, status (enum), referenceId (UUID), createdAt |

All tables are auto-created by Hibernate (`ddl-auto=update`). The `Account` table uses `BigDecimal` for monetary values to avoid floating-point precision errors.

---

## API Reference

**Base URL:** `http://localhost:8085`  
**Auth:** All endpoints except login/register require `Authorization: Bearer <token>`

### Auth Service

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a new user |
| POST | `/auth/login` | Public | Login and receive JWT |
| POST | `/auth/internal/createUser` | Public (internal) | Auto-create auth user during customer registration |

<details>
<summary>Request/Response Examples</summary>

**POST /auth/register**
```json
{
  "userName": "john_doe",
  "email": "john@example.com",
  "password": "securePass123",
  "role": "ROLE_CUSTOMER"
}
```

**POST /auth/login**
```json
// Request
{ "email": "john@example.com", "password": "securePass123" }

// Response
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```
</details>

### Customer Service

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/customers/saveCustomer` | Public | Register customer + auto-create auth credentials |
| GET | `/customers/customer/{id}` | Authenticated | Get customer by ID |
| GET | `/customers/all` | EMPLOYEE | List all customers |
| GET | `/customers/email/{email}` | Internal | Resolve email to customer ID |
| DELETE | `/customers/admin/customers/{id}` | ADMIN | Delete customer |

<details>
<summary>Request/Response Examples</summary>

**POST /customers/saveCustomer**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "password": "securePass123"
}
// Response: { "id": 1, "createdAt": "2025-05-12T10:30:00", "message": "Customer registration successful" }
```
</details>

### Accounts Service

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/accounts/create` | CUSTOMER | Create a new bank account (starts INACTIVE) |
| POST | `/accounts/approve/{accountId}` | EMPLOYEE | Activate an account |
| POST | `/accounts/{id}/credit?amount=X` | Authenticated | Credit amount to account |
| POST | `/accounts/{id}/debit?amount=X` | Authenticated | Debit amount from account |
| POST | `/accounts/transferMoney` | CUSTOMER | Transfer funds (Saga pattern) |
| GET | `/accounts/checkBalance/{id}` | Authenticated | Get account details + balance |
| GET | `/accounts/myAccounts` | CUSTOMER | Get all active accounts for logged-in user |
| DELETE | `/accounts/admin/accounts/{id}` | ADMIN | Delete an account |

<details>
<summary>Request/Response Examples</summary>

**POST /accounts/create**
```json
{
  "customerId": 1,
  "accountId": 100001,
  "accountName": "John Savings",
  "ifscCode": "BANK0001234",
  "bankBranch": "Hyderabad Main"
}
```

**POST /accounts/transferMoney**
```json
// Request
{ "fromAccountId": 100001, "toAccountId": 100002, "amount": 5000.00 }

// Response (success)
{ "id": 1, "status": "SUCCESS", "referenceId": "a1b2c3d4-...", "message": "Amount Transfer successful" }

// Response (failure — auto-compensated)
{ "id": 2, "status": "FAILED", "referenceId": "e5f6g7h8-...", "message": "Insufficient Balance" }
```
</details>

### Transaction Service (Internal)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/transactions/pending` | Create a PENDING transaction record |
| PUT | `/transactions/{id}/success` | Mark transaction as SUCCESS |
| PUT | `/transactions/{id}/failed` | Mark transaction as FAILED |
| PUT | `/transactions/{id}/compensated` | Mark transaction as COMPENSATED |

---

## How to Run

### Prerequisites

- Java 17
- Maven 3.8+
- Docker Desktop (running)

### Local Setup

```bash
# Clone
git clone https://github.com/shyamtalagapu/banking-microservices-system.git
cd banking-microservices-system

# Build all 6 services
mvn clean package -DskipTests

# Start all containers (MySQL + 6 services)
docker-compose up --build
```

### Verify

| Service | URL |
|---|---|
| API Gateway | http://localhost:8085 |
| Eureka Dashboard | http://localhost:8761 |
| MySQL | `localhost:3307` (user: `root` / password: `root`) |

### Stop

```bash
docker-compose down
```

---

## Author

**Shyam Talagapu** — Java Backend Developer

[![GitHub](https://img.shields.io/badge/GitHub-shyamtalagapu-181717?style=flat-square&logo=github)](https://github.com/shyamtalagapu)

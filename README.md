# 🏦 Banking Microservices System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Multi--Module-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**A production-inspired, cloud-native banking backend built with Spring Boot 3 microservices.**  
Covers customer management, account operations, transaction processing, JWT-based authentication,  
service discovery, API gateway routing — all containerized with Docker Compose.

[Architecture](#-architecture) • [Services](#-microservices) • [Quick Start](#-quick-start) • [API Reference](#-api-reference) • [Tech Stack](#-tech-stack)

</div>

---

## 📌 Table of Contents

- [Architecture](#-architecture)
- [Microservices](#-microservices)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Running with Docker](#-running-with-docker)
- [API Reference](#-api-reference)
- [Service Ports & Access Points](#-service-ports--access-points)
- [Database Schema](#-database-schema)
- [Key Design Decisions](#-key-design-decisions)
- [Author](#-author)

---

## 🏗️ Architecture

All client requests enter through a **single API Gateway** on port `8085`. The gateway validates JWT tokens and routes requests to downstream services discovered dynamically via **Netflix Eureka**. All services share a dedicated **Docker bridge network** (`banking-network`) and a single **MySQL 8** instance with isolated schemas per service.

```
                        ┌─────────────────────────────────┐
                        │        CLIENT / POSTMAN          │
                        └──────────────┬──────────────────┘
                                       │ HTTP (Port 8085)
                                       ▼
                        ┌─────────────────────────────────┐
                        │           API GATEWAY            │
                        │     Spring Cloud Gateway         │
                        │  • JWT Token Validation          │
                        │  • Route-based Forwarding        │
                        │  • Load Balancing (Eureka)       │
                        └──────────────┬──────────────────┘
                                       │
              ┌────────────────────────┼────────────────────────┐
              │                        │                        │
              ▼                        ▼                        ▼
  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐
  │   AUTH SERVICE     │  │  CUSTOMER SERVICE  │  │  ACCOUNTS SERVICE  │
  │  JWT Generation    │  │  Profile CRUD      │  │  Account Mgmt      │
  │  Login/Register    │  │  Customer Data     │  │  Balance Ops       │
  └────────────────────┘  └────────────────────┘  └────────────────────┘
                                                             │
                                       ┌─────────────────────┘
                                       │
                          ┌────────────────────┐
                          │ TRANSACTION SERVICE │
                          │  Txn History        │
                          │  Txn Details        │
                          └────────────────────┘
                                       │
              ┌────────────────────────┼────────────────────────┐
              │                        │                        │
              ▼                        ▼                        ▼
  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐
  │  DISCOVERY SERVER  │  │    MySQL 8 DB       │  │  banking-network   │
  │  Netflix Eureka    │  │  Port 3307:3306     │  │  Docker Bridge     │
  │  Port 8761         │  │  Schema per service │  │  Isolated Network  │
  └────────────────────┘  └────────────────────┘  └────────────────────┘
```

### Request Lifecycle

```
1.  Client sends HTTP request → API Gateway (8085)
2.  Gateway checks Authorization: Bearer <JWT>
3.  JWT valid → Gateway resolves service name via Eureka
4.  Gateway forwards request to target microservice
5.  Service processes request → reads/writes MySQL
6.  Response returns through Gateway back to client
```

---

## 🧩 Microservices

| # | Service | Responsibility | Port |
|---|---|---|---|
| 1 | **API Gateway** | Single entry point — JWT validation, route forwarding, load balancing | `8085` |
| 2 | **Discovery Server** | Netflix Eureka — service registry, dynamic discovery, health monitoring | `8761` |
| 3 | **Auth Service** | User registration, login, JWT token issuance and validation | Dynamic |
| 4 | **Customer Service** | Customer profile creation, retrieval, update, deletion | Dynamic |
| 5 | **Accounts Service** | Bank account creation, balance queries, account management | Dynamic |
| 6 | **Transaction Service** | Transaction recording, history queries, transaction details | Dynamic |
| 7 | **MySQL Init** | Auto-creates all database schemas and tables on first startup | — |

### Service Communication Flow

```
Auth Service       →  issues JWT tokens used by all other services
API Gateway        →  validates JWT before forwarding to any service
Customer Service   →  registers with Eureka, discovered by Gateway
Accounts Service   →  registers with Eureka, can call Customer Service
Transaction Svc    →  registers with Eureka, linked to Accounts Service
All Services       →  share banking-network Docker bridge
All Services       →  persist data to MySQL (isolated schemas)
```

---

## 🛠️ Tech Stack

| Category | Technology | Purpose |
|---|---|---|
| **Language** | Java 17 | LTS version with modern features (records, sealed classes, text blocks) |
| **Framework** | Spring Boot 3.2.5 | Auto-configuration, embedded server, production-ready defaults |
| **Cloud** | Spring Cloud 2023.0.1 | Microservices patterns — discovery, gateway, config |
| **Service Discovery** | Netflix Eureka | Dynamic service registration and discovery without hardcoded URLs |
| **API Gateway** | Spring Cloud Gateway | Reactive gateway — routing, JWT filter, load balancing |
| **Security** | Spring Security + JWT | Stateless auth — JWT issued by auth-service, validated at gateway |
| **Database** | MySQL 8.0 | Relational persistence, one schema per microservice |
| **ORM** | Spring Data JPA + Hibernate | Entity management, repositories, query generation |
| **Build** | Maven Multi-Module | Centralized dependency management across all 6 services |
| **Containerization** | Docker + Docker Compose | Each service has its own Dockerfile, orchestrated via Compose |
| **Networking** | Docker Bridge Network | Isolated `banking-network` — services communicate by container name |

---

## 📁 Project Structure

```
banking-microservices-system/
│
├── 📂 accounts-service/              # Bank account management
│   ├── src/main/java/                # Controllers, Services, Repositories, Entities
│   ├── src/main/resources/           # application.properties
│   ├── Dockerfile                    # Service container definition
│   └── pom.xml                       # Service-level dependencies
│
├── 📂 api-gateway/                   # Centralized routing + JWT filter
│   ├── src/main/java/                # Gateway filter, JWT validation logic
│   ├── src/main/resources/           # Route config (application.yml)
│   ├── Dockerfile
│   └── pom.xml
│
├── 📂 auth-service/                  # Authentication + JWT issuance
│   ├── src/main/java/                # Auth controller, UserDetails, JWT util
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📂 customer-service/              # Customer profile management
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📂 discovery-server/              # Eureka service registry
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📂 transactionDetails-service/    # Transaction history + details
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📂 mysql-init/                    # SQL scripts — auto-run on DB startup
│   └── init.sql                      # Creates all schemas and tables
│
├── 🐳 docker-compose.yml             # Full system orchestration (7 containers)
├── 📄 pom.xml                        # Parent POM — manages all module versions
└── 📄 .gitignore                     # Excludes target/, *.jar, *.class
```

---

## 🚀 Quick Start

### Prerequisites

| Tool | Minimum Version |
|---|---|
| Java | 17+ |
| Maven | 3.8+ |
| Docker Desktop | Latest |
| Git | Any |

### Clone and Run in 3 Steps

```bash
# Step 1 — Clone the repository
git clone https://github.com/shyamtalagapu/banking-microservices-system.git
cd banking-microservices-system

# Step 2 — Build all services
mvn clean package -DskipTests

# Step 3 — Launch everything with Docker Compose
docker-compose up --build
```

That's it. All 7 containers start automatically in the correct order.

---

## 🐳 Running with Docker

### Start All Services

```bash
docker-compose up --build
```

### Run in Background (Detached Mode)

```bash
docker-compose up --build -d
```

### View Logs for a Specific Service

```bash
docker-compose logs -f accounts-service
docker-compose logs -f api-gateway
docker-compose logs -f auth-service
```

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove Volumes (Full Reset)

```bash
docker-compose down -v
```

### Container Startup Order

Docker Compose starts services in dependency order:

```
[1] MySQL 8          →  Database ready, schemas auto-created
[2] Discovery Server →  Eureka starts, ready to accept registrations
[3] API Gateway      →  Depends on Discovery Server
[4] Auth Service     →  Depends on MySQL + Discovery Server
[5] Customer Service →  Depends on MySQL + Discovery Server
[6] Accounts Service →  Depends on MySQL + Discovery Server
[7] Transaction Svc  →  Depends on MySQL + Discovery Server
```

All services register themselves with Eureka on startup. The API Gateway discovers them by service name — no hardcoded IPs anywhere.

---

## 📡 API Reference

### Authentication

Before calling any secured endpoint, obtain a JWT token:

```http
POST http://localhost:8085/auth/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Use this token in all subsequent requests:
```
Authorization: Bearer <token>
```

---

### Customer Service Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/customers` | Create a new customer profile |
| `GET` | `/customers/{id}` | Get customer by ID |
| `GET` | `/customers` | Get all customers |
| `PUT` | `/customers/{id}` | Update customer details |
| `DELETE` | `/customers/{id}` | Delete a customer |

---

### Accounts Service Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/accounts` | Create a new bank account |
| `GET` | `/accounts/{id}` | Get account details by ID |
| `GET` | `/accounts/customer/{customerId}` | Get all accounts for a customer |
| `PUT` | `/accounts/{id}` | Update account information |
| `DELETE` | `/accounts/{id}` | Close/delete an account |

---

### Transaction Service Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/transactions` | Record a new transaction |
| `GET` | `/transactions/{id}` | Get transaction by ID |
| `GET` | `/transactions/account/{accountId}` | Get transaction history for an account |

---

### Sample Request Flow

```bash
# 1. Register / Login to get JWT
curl -X POST http://localhost:8085/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# 2. Create a customer (use token from step 1)
curl -X POST http://localhost:8085/customers \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phone":"9876543210"}'

# 3. Create a bank account for the customer
curl -X POST http://localhost:8085/accounts \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"accountType":"SAVINGS","balance":10000.00}'

# 4. Record a transaction
curl -X POST http://localhost:8085/transactions \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"accountId":1,"type":"CREDIT","amount":5000.00,"description":"Salary"}'
```

---

## 🌐 Service Ports & Access Points

| Service | Host Port | URL |
|---|---|---|
| **API Gateway** (main entry) | `8085` | `http://localhost:8085` |
| **Eureka Dashboard** | `8761` | `http://localhost:8761` |
| **MySQL Database** | `3307` | `localhost:3307` |
| Auth Service | Dynamic | Routed via Gateway |
| Customer Service | Dynamic | Routed via Gateway |
| Accounts Service | Dynamic | Routed via Gateway |
| Transaction Service | Dynamic | Routed via Gateway |

> All business API calls go through `http://localhost:8085` only.  
> Business services do not expose ports directly — they register with Eureka and are discovered dynamically.

---

## 🗄️ Database Schema

MySQL 8 runs on port `3307`. On first container startup, the `mysql-init/` scripts automatically create all required schemas and tables — zero manual setup needed.

Each microservice owns its own schema:

| Schema | Owned By | Tables |
|---|---|---|
| `customer_db` | Customer Service | `customers` |
| `accounts_db` | Accounts Service | `accounts` |
| `transaction_db` | Transaction Service | `transactions` |
| `auth_db` | Auth Service | `users` |

> Services never access each other's schemas. Inter-service data is fetched via REST API calls — enforcing loose coupling.

---

## 💡 Key Design Decisions

**Why a dedicated Auth Service?**  
Centralizes all authentication logic in one place. JWT tokens are issued here and validated at the gateway — downstream services remain stateless and free of auth concerns.

**Why Netflix Eureka for Service Discovery?**  
Services register by name, not IP. When a service restarts, its IP can change — Eureka handles this transparently. The API Gateway resolves `account-service` to whatever IP Eureka has registered, with no config changes.

**Why Docker Bridge Network (`banking-network`)?**  
All containers communicate using service names as hostnames (e.g., `mysql`, `discovery-server`). This eliminates IP dependencies entirely and mirrors real Kubernetes pod networking behavior.

**Why Maven Multi-Module with Parent POM?**  
Spring Boot `3.2.5` and Spring Cloud `2023.0.1` must be compatible with each other. Managing this in one parent POM guarantees all 6 services use matching versions — preventing the classic "dependency hell" in multi-service Java projects.

**Why Dockerfile per Service?**  
Each service builds its own image independently. This enables selective rebuilds — changing `accounts-service` code only rebuilds that image, not all 6. It also mirrors real CI/CD pipelines where each service has its own build and deploy lifecycle.

**Why MySQL Init Scripts?**  
Placing SQL scripts in `mysql-init/` means Docker mounts them as `docker-entrypoint-initdb.d` — MySQL auto-executes them on first run. The entire database setup is code, not manual steps.

---

## ⚙️ Configuration

### Environment Variables (docker-compose.yml)

| Variable | Service | Value |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | MySQL | `root` |
| `SPRING_DATASOURCE_URL` | Business Services | `jdbc:mysql://mysql:3306/<schema>` |
| `EUREKA_CLIENT_SERVICEURL` | All Services | `http://discovery-server:8761/eureka` |

> 🔒 **For production use**, move all credentials to a `.env` file and reference them as `${VARIABLE}` in `docker-compose.yml`. Never commit real credentials to source control.

---

## 🗺️ Roadmap

The following enhancements are planned to evolve this into a fully production-grade system:

- [ ] **Resilience4j Circuit Breakers** — Prevent cascade failures when a service is slow or down
- [ ] **Spring Cloud Config Server** — Centralized configuration management backed by Git
- [ ] **Micrometer + Zipkin** — Distributed tracing to follow requests across all services
- [ ] **Apache Kafka** — Event-driven transaction processing (replace synchronous REST for txns)
- [ ] **Springdoc OpenAPI** — Auto-generate Swagger UI for all services
- [ ] **Redis Caching** — Cache frequently accessed customer/account data
- [ ] **GitHub Actions CI/CD** — Automated build, test, and Docker image push pipeline
- [ ] **Kubernetes Deployment** — Helm charts to deploy on K8s with native service discovery
- [ ] **ELK Stack** — Centralized log aggregation (Elasticsearch + Logstash + Kibana)
- [ ] **AI Fraud Detection** — ML microservice for real-time transaction anomaly detection

---

## 👨‍💻 Author

**Shyam Talagapu**  
Java Backend Developer | Spring Boot | Microservices | Cloud-Native

[![GitHub](https://img.shields.io/badge/GitHub-shyamtalagapu-181717?style=flat-square&logo=github)](https://github.com/shyamtalagapu)

---

<div align="center">

⭐ If this project helped you understand Spring Boot microservices, give it a star!

</div>

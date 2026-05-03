# 🏦 Banking Microservices System

A production-ready, cloud-native **Banking Backend System** built with **Spring Boot 3**, **Spring Cloud**, and **Docker**. This system implements a complete microservices architecture covering customer management, account operations, transaction processing, authentication, and infrastructure services.

---

## 📌 Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Microservices](#-microservices)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Running with Docker](#-running-with-docker)
- [Service Ports](#-service-ports)
- [API Flow](#-api-flow)
- [Key Design Decisions](#-key-design-decisions)

---

## 🏗️ Architecture Overview

```
Client Request
      │
      ▼
 ┌─────────────┐
 │  API Gateway │  ← Single Entry Point (Port 8085)
 │  (Port 8085) │    Route-based load balancing
 └──────┬──────┘
        │
        ├──────────────────────────────────────────┐
        │                                          │
        ▼                                          ▼
 ┌──────────────┐                        ┌──────────────────┐
 │  Auth Service │                        │  Discovery Server │
 │  (JWT Token)  │                        │  Eureka (8761)    │
 └──────────────┘                        └──────────────────┘
        │                                          ▲
        │           All services register here ────┘
        ▼
 ┌──────────────────────────────────────────────────┐
 │              Business Microservices               │
 │  ┌──────────────┐  ┌──────────────┐  ┌────────┐  │
 │  │   Customer   │  │   Accounts   │  │  Txn   │  │
 │  │   Service    │  │   Service    │  │ Service│  │
 │  └──────────────┘  └──────────────┘  └────────┘  │
 └──────────────────────────────────────────────────┘
                          │
                          ▼
                   ┌─────────────┐
                   │   MySQL 8   │
                   │  (Port 3307)│
                   └─────────────┘
```

---

## 🧩 Microservices

| Service | Description | Port |
|---|---|---|
| **API Gateway** | Single entry point, JWT validation, route-based forwarding | `8085` |
| **Discovery Server** | Eureka-based service registry for dynamic service discovery | `8761` |
| **Auth Service** | User login, JWT token generation and validation | Dynamic |
| **Customer Service** | Customer profile creation, retrieval, and management | Dynamic |
| **Accounts Service** | Bank account creation, balance inquiries, account operations | Dynamic |
| **Transaction Service** | Transaction recording, history, and details | Dynamic |

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.5 |
| **Cloud** | Spring Cloud 2023.0.1 |
| **Service Discovery** | Netflix Eureka (Spring Cloud Netflix) |
| **API Gateway** | Spring Cloud Gateway |
| **Security** | Spring Security + JWT |
| **Database** | MySQL 8 |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven (Multi-Module) |
| **Containerization** | Docker + Docker Compose |
| **Communication** | REST (HTTP) via Feign Client / RestTemplate |

---

## 📁 Project Structure

```
banking-microservices-parent/
│
├── accounts-service/           # Bank account management
│   ├── src/
│   └── pom.xml
│
├── customer-service/           # Customer profile management
│   ├── src/
│   └── pom.xml
│
├── transactionDetails-service/ # Transaction history & details
│   ├── src/
│   └── pom.xml
│
├── auth-service/               # Authentication & JWT
│   ├── src/
│   └── pom.xml
│
├── api-gateway/                # API Gateway (routing + auth filter)
│   ├── src/
│   └── pom.xml
│
├── discovery-server/           # Eureka service registry
│   ├── src/
│   └── pom.xml
│
├── mysql-init/                 # DB initialization SQL scripts
│
├── docker-compose.yml          # Full system orchestration
└── pom.xml                     # Parent POM (multi-module)
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### Clone the Repository

```bash
git clone https://github.com/shyamtalagapu/banking-microservices-system.git
cd banking-microservices-system
```

### Build All Services

```bash
mvn clean package -DskipTests
```

---

## 🐳 Running with Docker

The entire system is orchestrated via Docker Compose. A single command brings up all services:

```bash
docker-compose up --build
```

To run in detached mode (background):

```bash
docker-compose up --build -d
```

To stop all services:

```bash
docker-compose down
```

To view logs of a specific service:

```bash
docker-compose logs -f accounts-service
```

### Docker Compose Service Startup Order

```
MySQL  →  Discovery Server  →  API Gateway
                            →  Auth Service
                            →  Customer Service
                            →  Accounts Service
                            →  Transaction Service
```

All business services depend on both **MySQL** and **Discovery Server** being healthy before starting.

---

## 🌐 Service Ports

| Service | Host Port | Container Port |
|---|---|---|
| MySQL | `3307` | `3306` |
| Discovery Server (Eureka) | `8761` | `8761` |
| API Gateway | `8085` | `8085` |
| Auth Service | Dynamic (Eureka) | — |
| Customer Service | Dynamic (Eureka) | — |
| Accounts Service | Dynamic (Eureka) | — |
| Transaction Service | Dynamic (Eureka) | — |

### Access Points

| URL | Description |
|---|---|
| `http://localhost:8085` | API Gateway (main entry point) |
| `http://localhost:8761` | Eureka Dashboard (service registry) |
| `localhost:3307` | MySQL Database |

---

## 🔄 API Flow

### Authentication Flow

```
1. POST /auth/login  →  Auth Service  →  Returns JWT Token
2. All subsequent requests must include:
   Authorization: Bearer <JWT_TOKEN>
```

### Sample Request Flow

```
Client
  │
  │  POST /api/customers  (with JWT)
  ▼
API Gateway (validates token, routes request)
  │
  ▼
Customer Service (processes request)
  │
  ▼
MySQL (persists data)
  │
  ▼
Response back to client
```

---

## 💡 Key Design Decisions

**Multi-Module Maven Project** — All services share a common parent POM for consistent dependency management and Spring Boot/Cloud version control.

**Service Discovery with Eureka** — Services register dynamically, enabling load balancing without hardcoded URLs. The API Gateway resolves routes via service names, not IPs.

**Centralized Entry via API Gateway** — The API Gateway is the only public-facing port (`8085`). All routing, JWT validation, and cross-cutting concerns are handled here before forwarding to downstream services.

**JWT-Based Stateless Security** — The Auth Service issues signed JWT tokens. The API Gateway validates tokens before routing, keeping downstream services stateless and free of authentication logic.

**Docker Compose Dependency Ordering** — `depends_on` ensures services start in the correct order (MySQL → Discovery Server → Business Services), preventing connection failures at startup.

**Shared MySQL with Schema Isolation** — All services use the same MySQL instance but operate on isolated schemas, initialized automatically via `mysql-init/` SQL scripts.

---

## 🗂️ Database

MySQL 8 is used as the primary relational database. On first startup, Docker automatically runs SQL scripts from the `mysql-init/` directory to create all required schemas and tables.

- **Host port:** `3307` (mapped from container's `3306`)
- **Root password:** `root`
- **Init scripts:** `./mysql-init/` folder

---

## 📦 Maven Multi-Module Structure

The parent `pom.xml` declares all services as modules and manages:

- Spring Boot version: `3.2.5`
- Spring Cloud version: `2023.0.1`
- Java version: `17`

This ensures all services stay in sync with compatible dependency versions.

---

## 👨‍💻 Author

**Shyam Talagapu**  
[GitHub](https://github.com/shyamtalagapu/banking-microservices-system)

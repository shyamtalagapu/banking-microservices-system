# 🏦 Banking Microservices System

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.1-6DB33F?style=flat-square&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939?style=flat-square&logo=jenkins&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS-EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white)

A cloud-native banking backend built with **Spring Boot 3 Microservices**, fully containerized with Docker, and **automatically deployed to AWS EC2 via a Jenkins CI/CD pipeline**. The system handles customer management, bank accounts, transactions, and JWT-based authentication — all behind a single API Gateway.

---

## 📌 Table of Contents

- [Architecture](#-architecture)
- [Microservices](#-microservices)
- [Tech Stack](#-tech-stack)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Project Structure](#-project-structure)
- [How to Run Locally](#-how-to-run-locally)
- [API Endpoints](#-api-endpoints)
- [Database](#-database)
- [Project Rating](#-project-rating)

---

## 🏗️ Architecture

Every request enters through the **API Gateway** — the single public-facing port. The gateway validates the JWT token and routes the request to the correct service, which it discovers dynamically from **Eureka** (the service registry). All services and the database sit inside a private **Docker network**.

```
Client
  │
  ▼
API Gateway (:8085)        ← validates JWT, routes requests
  │
  ├──► Auth Service         ← login, register, issue JWT
  ├──► Customer Service     ← manage customer profiles
  ├──► Accounts Service     ← manage bank accounts
  └──► Transaction Service  ← record and query transactions
            │
            ▼
     Discovery Server       ← Eureka: all services register here
            │
            ▼
         MySQL 8             ← each service has its own schema
```

---

## 🧩 Microservices

| Service | What It Does | Port |
|---|---|---|
| **API Gateway** | Entry point for all requests — JWT validation + routing | `8085` |
| **Discovery Server** | Eureka service registry — tracks all running services | `8761` |
| **Auth Service** | Register users, login, generate and validate JWT tokens | Dynamic |
| **Customer Service** | Create, read, update, delete customer profiles | Dynamic |
| **Accounts Service** | Create and manage bank accounts | Dynamic |
| **Transaction Service** | Record transactions and retrieve history | Dynamic |

> Business services don't have fixed ports. They register with Eureka on startup, and the Gateway finds them automatically by service name — no hardcoded URLs anywhere.

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Microservices | Spring Cloud 2023.0.1 |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Authentication | Spring Security + JWT |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA + Hibernate |
| Build Tool | Maven (Multi-Module) |
| Containerization | Docker + Docker Compose |
| CI/CD | Jenkins |
| Image Registry | Docker Hub |
| Cloud Deployment | AWS EC2 |

---

## 🚀 CI/CD Pipeline

Every `git push` to the `main` branch automatically triggers the Jenkins pipeline. No manual deployment steps — code goes from developer laptop to live AWS EC2 fully automated.

```
Developer pushes code to GitHub
          │
          ▼
    Jenkins detects push
          │
    ┌─────▼──────────────────────────────┐
    │ Stage 1: Checkout                  │
    │   Pull latest code from GitHub     │
    └─────┬──────────────────────────────┘
          │
    ┌─────▼──────────────────────────────┐
    │ Stage 2: Maven Build               │
    │   mvn clean package -DskipTests    │
    │   Compiles all 6 services → JARs  │
    └─────┬──────────────────────────────┘
          │
    ┌─────▼──────────────────────────────┐
    │ Stage 3: Docker Build              │
    │   Builds Docker image per service  │
    └─────┬──────────────────────────────┘
          │
    ┌─────▼──────────────────────────────┐
    │ Stage 4: Push to Docker Hub        │
    │   Pushes all 6 images to registry  │
    └─────┬──────────────────────────────┘
          │
    ┌─────▼──────────────────────────────┐
    │ Stage 5: Deploy to AWS EC2         │
    │   SSH into EC2                     │
    │   docker-compose pull              │
    │   docker-compose up -d             │
    └─────┬──────────────────────────────┘
          │
          ▼
   ✅ Application live on AWS EC2
```

The `Jenkinsfile` lives in the root of this repo — the pipeline definition is version-controlled alongside the application code.

---

## 📁 Project Structure

```
banking-microservices-system/
│
├── Jenkinsfile                      ← CI/CD pipeline (all 5 stages)
├── docker-compose.yml               ← Runs all 7 containers together
├── pom.xml                          ← Parent POM — shared versioning
│
├── api-gateway/                     ← JWT filter + route config
├── discovery-server/                ← Eureka server
├── auth-service/                    ← Login, register, JWT
├── customer-service/                ← Customer CRUD
├── accounts-service/                ← Account management
├── transactionDetails-service/      ← Transaction history
│
└── mysql-init/
    └── init.sql                     ← Auto-creates all DB schemas on startup
```

Each service folder contains its own `Dockerfile`, `pom.xml`, and Spring Boot source code.

---

## 💻 How to Run Locally

**Prerequisites:** Java 17, Maven 3.8+, Docker Desktop

```bash
# 1. Clone the repo
git clone https://github.com/shyamtalagapu/banking-microservices-system.git
cd banking-microservices-system

# 2. Build all services
mvn clean package -DskipTests

# 3. Start everything
docker-compose up --build
```

Once running:
- API Gateway → `http://localhost:8085`
- Eureka Dashboard → `http://localhost:8761`

To stop: `docker-compose down`

---

## 📡 API Endpoints

All requests go through `http://localhost:8085`.  
Every endpoint except login requires a JWT token in the header: `Authorization: Bearer <token>`

### Authentication

**Login**
```
POST /auth/login

Request Body:
{
  "username": "admin",
  "password": "password"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Register**
```
POST /auth/register

Request Body:
{
  "username": "john",
  "password": "password123"
}
```

---

### Customer Service

```
POST   /customers          → Create a new customer
GET    /customers          → Get all customers
GET    /customers/{id}     → Get customer by ID
PUT    /customers/{id}     → Update customer
DELETE /customers/{id}     → Delete customer

Sample Request Body (POST /customers):
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210"
}
```

---

### Accounts Service

```
POST   /accounts                        → Create a new bank account
GET    /accounts/{id}                   → Get account by ID
GET    /accounts/customer/{customerId}  → Get all accounts for a customer
PUT    /accounts/{id}                   → Update account
DELETE /accounts/{id}                   → Delete account

Sample Request Body (POST /accounts):
{
  "customerId": 1,
  "accountType": "SAVINGS",
  "balance": 10000.00
}
```

---

### Transaction Service

```
POST   /transactions                    → Record a transaction
GET    /transactions/{id}              → Get transaction by ID
GET    /transactions/account/{accountId} → Get transaction history

Sample Request Body (POST /transactions):
{
  "accountId": 1,
  "type": "CREDIT",
  "amount": 5000.00,
  "description": "Salary credit"
}
```

---

## 🗄️ Database

MySQL 8 runs on port `3307`. On first startup, Docker automatically runs `mysql-init/init.sql` which creates all required schemas — no manual DB setup needed.

Each service owns its own schema and never touches another service's data:

| Schema | Owned By |
|---|---|
| `auth_db` | Auth Service |
| `customer_db` | Customer Service |
| `accounts_db` | Accounts Service |
| `transaction_db` | Transaction Service |

---

## ⭐ Project Rating

> Evaluated as a **3-year experienced Java developer** — honest, detailed, and fair.

| Dimension | Score | Remarks |
|---|---|---|
| Architecture Design | 7.5 / 10 | Correct service split, Gateway + Eureka pattern is solid |
| Tech Stack | 7.0 / 10 | Latest Spring Boot 3.2.5 + Java 17 — up to date |
| Security | 5.0 / 10 | JWT auth works, but no rate limiting or HTTPS |
| CI/CD & DevOps | 7.5 / 10 | Jenkins + Docker Hub + AWS EC2 — real automated pipeline |
| Resilience | 3.0 / 10 | No circuit breakers — one service down can cascade |
| Observability | 2.0 / 10 | No distributed tracing (Zipkin) or centralized logging |
| Testing | 2.0 / 10 | No unit or integration tests visible in the repo |
| Code Quality | 6.0 / 10 | Clean structure, but no Swagger docs or config server |


---

## 👨‍💻 Author

**Shyam Talagapu** — Java Backend Developer

[![GitHub](https://img.shields.io/badge/GitHub-shyamtalagapu-181717?style=flat-square&logo=github)](https://github.com/shyamtalagapu)

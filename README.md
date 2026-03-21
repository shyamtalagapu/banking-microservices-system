

# Distributed Banking Microservices System

A cloud-ready backend system simulating real-world banking operations such as customer management, account handling, and money transfers using **Spring Boot Microservices Architecture**.

---

##  Overview

This project demonstrates how modern financial systems handle:

* Distributed transactions across services
* Data consistency using Saga pattern
* Service-to-service communication
* Fault tolerance and resilience
* Containerized cloud deployment using Docker

The system is fully deployed on a cloud VM using Docker and Docker Compose.

---

## Architecture


            Client  -->  API Gateway -----------------------------------------
                                       |               |                |
                                    Customer         Account        Transaction
                                     Service         Service          Service
                                         |              |              |
                                         --------------------------------
                                                       |
                                                     MySQL

##  Tech Stack

* **Language:** Java
* **Frameworks:** Spring Boot, Spring Cloud
* **Architecture:** Microservices
* **Service Discovery:** Eureka
* **API Gateway:** Spring Cloud Gateway
* **Resilience:** Resilience4j (Circuit Breaker)
* **Database:** MySQL
* **ORM:** Hibernate / JPA
* **Containerization:** Docker, Docker Compose
* **Deployment:** AWS EC2 (Cloud VM)

---

##  Microservices

| Service             | Responsibility                        |
| ------------------- | ------------------------------------- |
| Customer Service    | Manage customer data                  |
| Account Service     | Handle account operations & balance   |
| Transaction Service | Maintain transaction history (ledger) |
| API Gateway         | Entry point for all client requests   |
| Discovery Server    | Service registry (Eureka)             |

---

##  Key Features

###  Saga Pattern (Distributed Transactions)

Ensures consistency across multiple services during money transfer.

```
Transfer Flow:
1. Create PENDING transaction
2. Debit sender account
3. Credit receiver account
4. Mark transaction SUCCESS

Failure:
→ Compensation triggered → rollback debit


---

### Service Discovery (Eureka)

* Services dynamically register themselves
* No hardcoded service URLs
* Enables scalability and flexibility

---

###  API Gateway

* Centralized routing
* Single entry point for all APIs
* Decouples clients from internal services

---

###  Circuit Breaker (Resilience4j)

* Prevents cascading failures
* Provides fallback responses
* Improves system reliability

---

###  Concurrency Handling

* Implemented **Pessimistic Locking**
* Ensures safe balance updates during concurrent transactions

---

###  Docker-Based Deployment

* Each microservice runs in its own container
* Docker Compose orchestrates the entire system
* Services communicate via internal Docker network
* Images are pushed to Docker Hub and pulled on EC2

---

##  Money Transfer Flow

Client Request
     ↓
API Gateway
     ↓
Account Service
     ↓
Transaction Service (create PENDING)
     ↓
Debit Sender Account
     ↓
Credit Receiver Account
     ↓
Transaction marked SUCCESS
```

If failure occurs:
→ Compensation triggered
→ Transaction marked FAILED
→ Debit reversed


---

## Running the Project

### 1)Pull Docker Images

docker compose pull
---

### 2) Start All Services

docker compose up -d

---

### 3) Access Services

* Eureka Dashboard → `http://16.171.144.117:8761
* API Gateway → `http://16.171.144.117:8085


---

##  Highlights

* Designed a distributed banking system using microservices
* Implemented Saga pattern for data consistency
* Applied Circuit Breaker for fault tolerance
* Ensured data integrity using locking mechanisms
* Deployed complete system using Docker on AWS EC2

---

##  Docker Hub Images

* shyamtalagapu/customer-service
* shyamtalagapu/account-service
* shyamtalagapu/transactiondetails-service
* shyamtalagapu/api-gateway
* shyamtalagapu/discovery-server

---

##  Future Enhancements

* Kafka-based asynchronous communication
* Distributed logging & monitoring
* CI/CD pipeline integration
* Implement System Design concepts 


Author

Shyam Sundra Rao 


Java Backend Developer

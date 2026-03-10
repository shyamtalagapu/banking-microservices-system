**Distributed Banking Microservices System**
A cloud-deployed distributed banking backend built using Spring Boot microservices. 
The system supports customer management, account operations, and money transfers 
with Saga-based transaction orchestration, service discovery, API gateway routing, 
and resilience mechanisms.

The application is containerized using Docker and deployed on AWS EC2.
 
**Architecture Overview**
The system is built using a microservices architecture where each service is independently deployed and communicates via REST APIs.



**Tech Stack
Backend**

Java
Spring Boot
Spring Data JPA
Hibernate
REST APIs

**Microservices Infrastructure**
Spring Cloud
Netflix Eureka (Service Discovery)
API Gateway
Saga Pattern (Distributed Transactions)
Resilience4j (Circuit Breaker)


**Database**
MySQL

**DevOps & Deploymen**t
Docker
Docker Compose
AWS EC2

**Key Features**
-Microservices-based banking backend with independent services
-Distributed transaction management using Saga Pattern
-Service discovery using Netflix Eureka
-Centralized routing through API Gateway
-Fault tolerance using Resilience4j Circuit Breaker
-Concurrent transaction safety using pessimistic locking
-Accurate financial operations using BigDecimal
-Containerized deployment using Docker


**Running the Project**
Clone the repository
git clone https://github.com/shyamtalagapu/banking-microservices.git
cd banking-microservices

**Start all services**
docker compose up -d

**Verify Services**
Eureka Dashboard
http://localhost:8761

API Gateway

http://localhost:8085

**Example API**
**Money Transfer**

Endpoint

POST /accounts/transferMoney

Request Body

{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500
}
**Deployment**

The system is containerized using Docker and deployed on AWS EC2 using Docker Compose.
Each service runs in its own container, enabling independent scaling and fault isolation.

**Author**

Shyam Sundra Rao
Java Backend Developer

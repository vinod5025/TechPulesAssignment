
# Digital Wallet System

A secure REST API application developed using Java and Spring Boot that allows users to register, authenticate, manage wallet balances, add money, and transfer money between wallets.

## Technologies Used

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- REST API

## Features

- User Registration
- User Login
- Password Encryption using BCrypt
- JWT-based Authentication
- Automatic Wallet Creation
- Add Money to Wallet
- Check Wallet Balance
- Transfer Money Between Wallets
- Transaction Recording
- Idempotency Key Support
- Database Transaction Management
- Wallet Locking

## Project Architecture

The application follows a layered architecture:

- Controller Layer – Handles HTTP requests and responses.
- Service Layer – Contains business logic.
- Repository Layer – Handles database operations.
- Entity Layer – Represents database tables.
- DTO Layer – Handles request and response data.
- Security Layer – Handles JWT authentication and authorization.

## Database

The project uses the H2 in-memory database.

Database URL:

`jdbc:h2:mem:walletdb`

H2 Console:

`http://localhost:8080/h2-console`

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and generate JWT |

### Wallet

| Method | Endpoint | Description |
|---|---|---|
| GET | `/wallet` | Get wallet balance |
| POST | `/wallet/add` | Add money |
| POST | `/wallet/transfer` | Transfer money to another wallet |

## Authentication

Wallet endpoints require a JWT token.

Add the following header to authenticated requests:

`Authorization: Bearer <your-token>`

Money operations require an idempotency key:

`Idempotency-Key: unique-key`

## How to Run

1. Clone the repository.
2. Import the project into Spring Tool Suite as a Maven project.
3. Wait for Maven dependencies to download.
4. Run the Spring Boot application.
5. Test the APIs using Postman.

## Developer

Vinod Thombare

Java Developer

# Cred Manager - Backend

This is the backend for a self-hosted, zero-knowledge credentials manager built with **Spring Boot 3.4.3**, **Java 17+**, and **PostgreSQL**.

## 🔐 Features

- **JWT-based authentication**
- **Multi-user support**
- **Zero-knowledge encryption**
    - Each credential is AES-encrypted per user
    - AES key is derived from user password and stored encrypted using a master key
- **Password recovery without backdoors**
- **Encrypted credential storage**
- **RESTful API**
- **User registration, login, and deletion**
- **Credential add, view, edit, delete**
- **Audit tracking:** created & updated timestamps
- **Dark mode support (via frontend)**
- **Rate limiting** on login and registration to prevent brute-force attacks
    - Optional: can be toggled via a config flag

## 🚀 Technologies Used

- Java 17+
- Spring Boot 3.4.3
- Spring Security + JWT
- PostgreSQL (via Docker or local)
- Jasypt (for encrypting per-user AES keys with master key)
- Maven
- Docker (for PostgreSQL)
- BCrypt (for password hashing)

## 🚀 REST API Endpoints
- 🔐 Authentication
Method	Endpoint	Description
POST	/auth/register	Register a new user
POST	/auth/login	Login and get JWT
DELETE	/auth/delete	Delete current user

- 🧾 Credentials
All credential endpoints require Authorization: Bearer <JWT> header.

 - Method	Endpoint	         Description
   GET	    /credentials	    List all credentials
   POST	    /credentials	    Add a new credential
   GET	    /credentials/{id}	Get one credential (no pwd)
   GET	    /credentials/{id}/decrypted-password	Get decrypted password
   PUT	    /credentials/{id}	Update credential
   DELETE	/credentials/{id}	Delete credential

## ⛔ Rate Limiting
- Enabled by default on:
/auth/login
/auth/register

- Limits:
Max 5 requests per IP every 10 seconds
Toggle with Spring config flag:
rate.limiter.enabled: true

 ## Project Structure Highlights
- UserEntity, CredentialEntity – JPA entities
- CredentialService – business logic
- KeyEncryptionService – handles AES encryption/decryption
- JwtService, AuthService – handles login/registration and JWT
- InMemoryKeyCache – caches per-user AES keys during session
- RateLimitingFilter – servlet filter to block brute-force attempts

---

## ⚙️ Setup Instructions

### 1. Environment Variables

Set the following environment variable before starting the app:

```bash
export MASTER_KEY=your-secure-master-key

docker run --name cred-db -e POSTGRES_DB=credmanager -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres

./mvnw clean package
java -jar target/cred-manager-backend.jar

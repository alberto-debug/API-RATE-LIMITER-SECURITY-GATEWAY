# API Rate Limiter & Security Gateway

<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"></img>
<img src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white" alt="Spring"></img>
<img src="https://img.shields.io/badge/redis-%23DC382D.svg?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"></img>
<img src="https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"></img>
<img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens" alt="JWT"></img>

This project is a RESTful API built with Java and Spring Boot that implements a comprehensive **rate limiting and security gateway** for protecting APIs from abuse and unauthorized access. It features JWT-based authentication, Redis-backed rate limiting, role-based access control (RBAC), and user management.

## Table of Contents
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Architecture](#architecture)
- [Database](#database)
- [Security Features](#security-features)
- [Rate Limiting](#rate-limiting)
- [Contributing](#contributing)

## Installation
Clone the repository:

```bash
git clone https://github.com/alberto-debug/API-RATE-LIMITER-SECURITY-GATEWAY.git
cd API-RATE-LIMITER-SECURITY-GATEWAY
```

Build with Maven (wrapper included):

```bash
./mvnw clean package
# or run directly
./mvnw spring-boot:run
```

Notes:
- The project uses Java version 25 as configured in `pom.xml`. Ensure your `JAVA_HOME` points to a compatible JDK.
- Docker and Docker Compose are required for running PostgreSQL and Redis containers.

## Configuration

### Environment Variables
Create a `.env` file in the project root directory with the following configuration:

```env
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5433/ratelimiter_db
DB_NAME=ratelimiter_db
DB_USERNAME=postgres
DB_PASSWORD=postgres_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=redis_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_change_this_in_production_at_least_32_characters_long
JWT_EXPIRATION_MS=3600000
JWT_ISSUER=ratelimiter-api

# Admin Credentials
ADM_EMAIL=admin@example.com
ADM_PASSWORD=admin_password
```

### Application Properties
The repository includes `src/main/resources/application.properties` that reads from environment variables:

```properties
spring.application.name=rateLimiter

# Database Configuration
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Redis Configuration
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}

# JWT Configuration
KEY=${JWT_SECRET}
JWT_EXPIRATION_MS=${JWT_EXPIRATION_MS:3600000}
JWT_ISSUER=${JWT_ISSUER}

# Admin
ADM_EMAIL=${ADM_EMAIL}
ADM_PASSWORD=${ADM_PASSWORD}
```

## Usage

### Start PostgreSQL and Redis with Docker Compose

The project includes a `compose.yaml` file that defines PostgreSQL and Redis services:

```bash
docker compose -f compose.yaml up -d
```

This will start:
- **PostgreSQL** on port 5433
- **Redis** on port 6379

### Run the Application

Using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

By default, the API runs on **http://localhost:8080** (unless overridden in properties).

### Verify the Application is Running

```bash
curl http://localhost:8080/
```

Expected response:
```json
{
  "name": "API Rate Limiter & Security Gateway",
  "version": "1.0.0"
}
```

## API Endpoints

### Authentication Endpoints
These endpoints are publicly accessible and do not require authentication.

#### Register a New User
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

Response:
```json
{
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### User Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

Response:
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Admin Login
```http
POST /admin/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin_password"
}
```

Response:
```json
{
  "message": "Admin login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Protected Endpoints
These endpoints require a valid JWT token in the Authorization header:

```http
Authorization: Bearer <your_jwt_token>
```

#### Create User (Admin Only)
```http
POST /user/
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "Password123!"
}
```

#### Task Management (Authenticated Users)
```http
GET /tasks/
Authorization: Bearer <user_token>
```

```http
POST /tasks/
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "title": "Task Title",
  "description": "Task Description"
}
```

### Health Check
```http
GET /health
```

Response:
```json
{
  "status": "UP"
}
```

## Architecture

### System Components

```
┌─────────────────────────────────────────────────────────┐
│                   HTTP Request                          │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│              Security Filter                            │
│  - JWT Token Extraction & Validation                    │
│  - Client Identification                               │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│           Rate Limiter Service                          │
│  - Check Redis Counter                                 │
│  - Increment Request Count                             │
│  - Enforce Limits (100 req/min default)                │
└──────────────────────┬──────────────────────────────────┘
                       │
              ┌────────┴────────┐
              │                 │
    ┌─────────▼────────┐   ┌────▼──────────┐
    │   Rate Limit OK  │   │  Limit Hit    │
    │   Continue       │   │  Return 429   │
    └─────────┬────────┘   └───────────────┘
              │
┌─────────────▼──────────────────────────────────────────┐
│              Controller Layer                           │
│  - Route to appropriate endpoint                       │
│  - Process business logic                              │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│              Service Layer                              │
│  - User Management                                     │
│  - Authentication & Authorization                      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│          Repository Layer                               │
│  - Database Persistence (PostgreSQL)                   │
└──────────────────────┬──────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
  ┌─────▼──────────┐         ┌────────▼─────┐
  │  PostgreSQL    │         │    Redis     │
  │  (Users,       │         │  (Rate       │
  │   Roles)       │         │   Limits)    │
  └────────────────┘         └──────────────┘
```

### Key Classes

| Class | Purpose |
|-------|---------|
| `SecurityConfig` | Configures Spring Security, CORS, and authentication chain |
| `SecurityFilter` | Custom filter for JWT validation before request processing |
| `TokenService` | JWT generation and validation using Auth0 JWT library |
| `ClientIdentifier` | Extracts and validates client identity from Authorization header |
| `RateLimiterService` | Interface defining rate limiting contracts |
| `RateLimiterServiceImpl` | Redis-backed implementation of rate limiting |
| `UserService` | User management, authentication, and authorization |
| `AuthController` | Handles login and registration endpoints |

## Database

### PostgreSQL
The project uses PostgreSQL as the primary database for storing:
- **Users**: Email, hashed passwords, roles
- **Roles**: User roles (ROLE_USER, ROLE_ADMIN)
- **Associations**: User-to-Role mappings

### Redis
Redis is used for high-performance rate limiting:
- **Rate Limit Counters**: Key format `ratelimit:{clientId}`
- **Auto-Expiration**: Counters expire after the time window (default: 60 seconds)

### Database Initialization

On startup, the application automatically:
1. Creates tables via Hibernate (ddl-auto=update)
2. Seeds default roles (ROLE_USER, ROLE_ADMIN) via `RoleSeeder`
3. Seeds default admin user via `AdminSeeder`

## Security Features

### JWT Authentication
- **Algorithm**: HMAC256
- **Expiration**: Configurable (default: 1 hour / 3600000 ms)
- **Issuer**: Configurable via JWT_ISSUER
- **Subject**: User email address

### Password Encryption
- **Algorithm**: BCrypt
- **Strength**: Spring Security default (10 rounds)

### Role-Based Access Control (RBAC)
- **ROLE_USER**: Standard authenticated user
- **ROLE_ADMIN**: Administrative access to sensitive endpoints

### CORS Configuration
- Allows cross-origin requests from configured origins
- Supports preflight OPTIONS requests

### Rate Limiting
- **Strategy**: Sliding window counter stored in Redis
- **Default Limit**: 100 requests per 60 seconds per client
- **Customizable**: Per-endpoint or per-route limits
- **Fail-Open**: If Redis unavailable, requests are allowed (availability over security)

## Rate Limiting

### How It Works

The rate limiter uses a **token bucket with Redis** to track client requests:

1. **Client Identification**: Each client is identified by their JWT token subject (email)
2. **Counter Increment**: Each request increments a counter in Redis
3. **Window Tracking**: On first request, an expiration is set (sliding window)
4. **Limit Check**: Current count compared against configured limit
5. **Response**: Allowed (200) or Rate Limited (429)

### Example Flow

```
Request 1 → Counter: 1/100 ✓ Allowed
Request 2 → Counter: 2/100 ✓ Allowed
...
Request 100 → Counter: 100/100 ✓ Allowed
Request 101 → Counter: 101/100 ✗ Blocked (429 Too Many Requests)

After 60 seconds (window expires):
Request 102 → Counter: 1/100 ✓ Allowed (window reset)
```

### Custom Limits

Override default limits per endpoint:

```java
@GetMapping("/heavy-operation")
public ResponseEntity<?> heavyOperation(HttpServletRequest request) {
    String clientId = clientIdentifier.getUserIdentifier(request);
    
    // Allow only 10 requests per 5 minutes for this endpoint
    if (!rateLimiterService.isAllowed(clientId, 10, 300)) {
        return ResponseEntity.status(429)
            .body("Rate limit exceeded for heavy operations");
    }
    
    // Process request...
    return ResponseEntity.ok("Success");
}
```

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=RateLimiterApplicationTests
```

### Run with Coverage
```bash
./mvnw clean test jacoco:report
```

The project includes:
- **Unit Tests**: Service and utility classes
- **Integration Tests**: Full Spring Boot context with TestContainers
  - PostgreSQL testcontainer for database tests
  - Redis mock/testcontainer for rate limiting tests

### Environment for Tests
Tests use `@DynamicPropertyRegistry` to configure isolated test databases and services, ensuring no conflicts with local development.

## Contributing
Contributions are welcome! If you find issues or have suggestions:

1. Fork the repository.
2. Create a branch for your change: `git checkout -b feat/your-feature`.
3. Commit your changes with clear messages and follow the existing code style.
4. Run tests to ensure all pass: `./mvnw test`
5. Build the project: `./mvnw clean package`
6. Open a Pull Request describing the change and the rationale.

Please run tests and ensure the project builds before submitting a PR.

---

## Additional Resources

- **Spring Security Documentation**: https://spring.io/projects/spring-security
- **JWT Best Practices**: https://tools.ietf.org/html/rfc7519
- **Redis Rate Limiting**: https://redis.io/commands/incr/
- **Spring Data Redis**: https://spring.io/projects/spring-data-redis

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Support
For issues, questions, or suggestions, please open an issue on GitHub or contact the development team.

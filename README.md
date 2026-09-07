# JournalApp

A secure, role-based REST API backend for a personal journaling application built with **Spring Boot** and **MongoDB**. Users can create accounts, authenticate with credentials, and manage their journal entries in a multi-user environment with admin capabilities.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Testing](#testing)
- [Security Features](#security-features)
- [License](#license)

## Features

✨ **Core Functionality:**
- User registration and authentication with HTTP Basic Auth
- Secure password encryption using BCrypt
- Role-based access control (USER / ADMIN roles)
- Create, read, update, and delete (CRUD) journal entries
- Multi-user journal isolation with Spring Security
- MongoDB transactions for data consistency
- Admin panel for user management

🔐 **Security:**
- Spring Security with HTTP Basic authentication
- BCrypt password hashing
- CSRF protection disabled (stateless API)
- Role-based authorization on endpoints
- Transactional journal operations with MongoDB

## Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Programming language |
| **Spring Boot** | 3.5.9 | Web framework & autoconfiguration |
| **Spring Security** | Latest | Authentication & authorization |
| **Spring Data MongoDB** | Latest | NoSQL database ORM |
| **Lombok** | 1.18.40 | Boilerplate reduction (@Data, @NonNull) |
| **MongoDB** | 4.0+ | NoSQL document database |
| **Maven** | 3.8+ | Build & dependency management |

## Project Structure

```
journalApp/
├── src/
│   ├── main/
│   │   ├── java/com/ujuj/journalApp/
│   │   │   ├── JournalApplication.java          # Entry point with MongoDB transaction config
│   │   │   ├── config/
│   │   │   │   └── SpringSecurity.java          # Security configuration & endpoint authorization
│   │   │   ├── controller/
│   │   │   │   ├── PublicController.java        # Registration & health check endpoints
│   │   │   │   ├── UserController.java          # User account management endpoints
│   │   │   │   ├── journalEntryController.java  # Journal CRUD endpoints
│   │   │   │   └── AdminController.java         # Admin-only endpoints
│   │   │   ├── entity/
│   │   │   │   ├── User.java                    # MongoDB User document model
│   │   │   │   └── JournalEntry.java            # MongoDB JournalEntry document model
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java          # MongoDB user data access layer
│   │   │   │   └── JournalEntryRepository.java  # MongoDB journal entry data access layer
│   │   │   └── services/
│   │   │       ├── UserService.java             # User business logic & password encoding
│   │   │       ├── UserDetailsServiceImpl.java   # Spring Security user loading
│   │   │       └── JournalEntryService.java     # Journal entry business logic
│   │   └── resources/
│   │       └── application.properties           # MongoDB connection & app config
│   └── test/
│       └── java/com/ujuj/journalApp/
│           ├── JournalApplicationTests.java     # Smoke tests
│           └── service/UserServiceTests.java    # User service integration tests
├── pom.xml                                      # Maven configuration
├── mvnw & mvnw.cmd                              # Maven wrapper scripts
└── README.md                                    # This file

```

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
  - Verify: `java -version`
- **MongoDB** 4.0 or higher (running locally or accessible)
  - Verify: `mongosh` or `mongo` shell access
- **Maven** 3.8.0 or higher
  - Verify: `mvn -version`
- **Git** (to clone the repository)

### MongoDB Setup

1. **Install MongoDB locally:**
   ```bash
   # macOS (using Homebrew)
   brew tap mongodb/brew
   brew install mongodb-community
   brew services start mongodb-community

   # Ubuntu/Debian
   sudo apt-get install -y mongodb
   sudo systemctl start mongodb

   # Windows
   # Download from https://www.mongodb.com/try/download/community
   ```

2. **Verify MongoDB is running:**
   ```bash
   mongosh
   > show databases
   > exit
   ```

3. **Database will be created automatically** by Spring Boot on first run.

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ujwal2233/journalApp.git
   cd journalApp
   ```

2. **Build the project:**
   ```bash
   ./mvnw clean install
   ```

3. **Verify configuration (optional):**
   - Edit `src/main/resources/application.properties` if MongoDB is not on localhost:27017
   ```properties
   spring.data.mongodb.host=localhost
   spring.data.mongodb.port=27017
   spring.data.mongodb.database=journaldb
   ```

## Running the Application

### Option 1: Using Maven
```bash
./mvnw spring-boot:run
```

### Option 2: Run the JAR
```bash
./mvnw clean package
java -jar target/journalApp-0.0.1-SNAPSHOT.jar
```

**Expected output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.9)

2025-12-28 17:11:33.000  INFO 12345 --- [  main] c.u.j.JournalApplication : Started JournalApplication in 3.456 seconds
```

The application is now running on **http://localhost:8080**

## API Endpoints

### Authentication
All protected endpoints use **HTTP Basic Auth** with format: `-u username:password`

### Public Endpoints (No Authentication Required)

#### Health Check
```http
GET /public/health
```
**Response:** `200 OK` - `"ok"`

#### User Registration
```http
POST /public/create-user
Content-Type: application/json

{
  "userName": "john_doe",
  "password": "secure_password123"
}
```
**Response:** `201 Created` - User created with default USER role

---

### Protected Endpoints (Authentication Required)

#### Get All Personal Journal Entries
```http
GET /journal
Authorization: Basic base64(username:password)
```
**Response:** `200 OK` - Array of journal entries for authenticated user
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "title": "Day 1",
    "content": "Today was productive",
    "date": "2025-12-28T10:30:00"
  }
]
```

#### Create a Journal Entry
```http
POST /journal
Authorization: Basic base64(username:password)
Content-Type: application/json

{
  "title": "My First Entry",
  "content": "This is my first journal entry"
}
```
**Response:** `201 Created` - Entry object with generated ID and timestamp

#### Get Specific Journal Entry by ID
```http
GET /journal/id/{entryId}
Authorization: Basic base64(username:password)
```
**Response:** `200 OK` - Journal entry object
**Note:** User can only access their own entries

#### Update Journal Entry
```http
PUT /journal/id/{entryId}
Authorization: Basic base64(username:password)
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content"
}
```
**Response:** `200 OK` - Updated entry object

#### Delete Journal Entry
```http
DELETE /journal/id/{entryId}
Authorization: Basic base64(username:password)
```
**Response:** `204 No Content` - Entry deleted successfully

#### Get User Profile
```http
GET /user
Authorization: Basic base64(username:password)
```
**Response:** `200 OK` - User object with all users list

#### Update User Profile
```http
PUT /user
Authorization: Basic base64(username:password)
Content-Type: application/json

{
  "userName": "new_username",
  "password": "new_password"
}
```
**Response:** `204 No Content` - Profile updated

#### Delete User Account
```http
DELETE /user
Authorization: Basic base64(username:password)
Content-Type: application/json

{}
```
**Response:** `204 No Content` - Account deleted

---

### Admin Endpoints (ADMIN Role Required)

#### Get All Users
```http
GET /admin/all-users
Authorization: Basic base64(admin_user:admin_password)
```
**Response:** `200 OK` - Array of all user objects in the system

#### Create Admin User
```http
POST /admin/create-admin-user
Authorization: Basic base64(admin_user:admin_password)
Content-Type: application/json

{
  "userName": "admin_new",
  "password": "admin_password"
}
```
**Response:** `201 Created` - Admin user created with ADMIN & USER roles

---

## Example cURL Requests

### Register a New User
```bash
curl -X POST http://localhost:8080/public/create-user \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "alice",
    "password": "password123"
  }'
```

### Health Check
```bash
curl http://localhost:8080/public/health
```

### Create a Journal Entry
```bash
curl -X POST http://localhost:8080/journal \
  -H "Content-Type: application/json" \
  -u alice:password123 \
  -d '{
    "title": "My First Day",
    "content": "Today was amazing!"
  }'
```

### Get All Your Journal Entries
```bash
curl -X GET http://localhost:8080/journal \
  -u alice:password123
```

### Get a Specific Entry (replace with actual ID)
```bash
curl -X GET http://localhost:8080/journal/id/507f1f77bcf86cd799439011 \
  -u alice:password123
```

### Update an Entry
```bash
curl -X PUT http://localhost:8080/journal/id/507f1f77bcf86cd799439011 \
  -H "Content-Type: application/json" \
  -u alice:password123 \
  -d '{
    "title": "Updated Title",
    "content": "Updated content"
  }'
```

### Delete an Entry
```bash
curl -X DELETE http://localhost:8080/journal/id/507f1f77bcf86cd799439011 \
  -u alice:password123
```

### Update User Profile
```bash
curl -X PUT http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -u alice:password123 \
  -d '{
    "userName": "alice_new",
    "password": "newpassword456"
  }'
```

### Get Admin Users List (Admin Only)
```bash
curl -X GET http://localhost:8080/admin/all-users \
  -u admin:adminpass
```

## Configuration

### Application Properties

Edit `src/main/resources/application.properties` to customize:

```properties
# Application Name
spring.application.name=journalApp

# MongoDB Configuration
spring.data.mongodb.host=localhost          # MongoDB server host
spring.data.mongodb.port=27017              # MongoDB server port
spring.data.mongodb.database=journaldb      # Database name

# Server Configuration (optional)
server.port=8080                            # Application port
server.servlet.context-path=/               # Context path
```

### Custom Database Name
To use a different MongoDB database:
```properties
spring.data.mongodb.database=my_custom_db
```

### MongoDB Atlas (Cloud)
To connect to MongoDB Atlas:
```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/journaldb
```

## Testing

Run the test suite:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTests

# Generate test report
./mvnw test -Dtest=*Tests
```

### Test Coverage

- **JournalApplicationTests.java** — Smoke test ensuring application context loads
- **UserServiceTests.java** — Integration test for user repository lookups

Add more tests to increase coverage:
```bash
./mvnw clean test jacoco:report
```

## Security Features

### Authentication & Authorization

1. **HTTP Basic Authentication**
   - Username and password sent in Base64 in `Authorization` header
   - Suitable for development/testing; use OAuth2 for production

2. **Password Security**
   - Passwords hashed using BCrypt algorithm (spring-security-crypto)
   - Never stored in plaintext
   - Encoding handled by `UserService.saveNewUser()`

3. **Role-Based Access Control (RBAC)**
   - **USER** — Can manage own journal entries and account
   - **ADMIN** — Can view all users, create admin accounts
   - Default role: USER (assigned on registration)

4. **Endpoint Authorization (SpringSecurity.java)**
   - `/public/**` — Open to all (registration, health)
   - `/journal/**`, `/user/**` — Requires USER role (authenticated)
   - `/admin/**` — Requires ADMIN role
   - CSRF protection disabled (stateless API with Basic auth)

5. **Multi-User Isolation**
   - Users can only access their own journal entries
   - Ownership validated in controller via `SecurityContextHolder`
   - MongoDB `DBRef` maintains relationship between User and JournalEntry

### Best Practices Implemented

- ✅ Stateless authentication (no session state)
- ✅ Transactional consistency for entry operations
- ✅ Password hashing with BCrypt
- ✅ Role-based endpoint protection
- ✅ User-scoped data isolation

### Security Recommendations for Production

- 🔒 Use HTTPS/TLS for all endpoints
- 🔒 Implement OAuth2 or JWT instead of HTTP Basic
- 🔒 Add rate limiting to prevent brute force
- 🔒 Use environment variables for MongoDB credentials
- 🔒 Enable MongoDB authentication
- 🔒 Add input validation & sanitization
- 🔒 Implement audit logging
- 🔒 Use Spring Security's CORS configuration

## Troubleshooting

### MongoDB Connection Error
```
Error: connect ECONNREFUSED 127.0.0.1:27017
```
**Solution:** Ensure MongoDB is running
```bash
# Check if MongoDB is running
brew services list          # macOS
systemctl status mongodb    # Linux
```

### Port Already in Use
```
Address already in use: bind
```
**Solution:** Change port in `application.properties`
```properties
server.port=8081
```

### Authentication Failures
```
401 Unauthorized
```
**Solution:** Check username and password are correct
```bash
# Encode credentials in Base64
echo -n "username:password" | base64
# Use in curl: -u username:password (curl handles encoding)
```

### Entry Not Found
```
404 Not Found
```
**Possible causes:**
- Entry doesn't exist
- Entry belongs to different user
- ObjectId format incorrect
**Solution:** Verify entry ID and user ownership

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Future Enhancements

- [ ] OAuth2 / JWT authentication
- [ ] Email verification for registration
- [ ] Journal entry sharing between users
- [ ] Search and filter journal entries
- [ ] Tags and categories for entries
- [ ] Cloud storage integration
- [ ] Mobile app (React Native / Flutter)
- [ ] Web UI (React / Angular)
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Pagination for large datasets

## License

This project is open source and available under the MIT License. See `LICENSE` file for details.

## Author

**Ujwal** — [GitHub Profile](https://github.com/ujwal2233)

## Support

For issues, questions, or suggestions:
- 📝 [Open an Issue](https://github.com/ujwal2233/journalApp/issues)
- 💬 [Discussions](https://github.com/ujwal2233/journalApp/discussions)

---

**Happy Journaling! 📔✨**

*Last updated: December 28, 2025*

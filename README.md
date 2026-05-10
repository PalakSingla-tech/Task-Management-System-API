# Task-Management-System-API

A robust, secure, and scalable RESTful API built with Spring Boot for managing tasks. This project features a dual-module system (User and Admin) with JWT-based authentication, soft-deletion, and advanced filtering capabilities.

## 🚀 Features

- **JWT Authentication**: Secure stateless authentication using JSON Web Tokens.
- **Dual Role System**: Separate flows for `ROLE_USER` and `ROLE_ADMIN`.
- **Task Management**:
    - Full CRUD operations.
    - **Soft Delete**: Tasks are moved to "trash" instead of being permanently deleted.
    - **Task Completion**: Quick status updates.
- **Advanced Filtering & Search**:
    - Paginated results for high performance.
    - Dynamic search by title (case-insensitive).
    - Filter by Status (PENDING, IN_PROGRESS, COMPLETED).
    - Filter by Priority (LOW, MEDIUM, HIGH).
    - Sort by any field (ID, Title, Due Date, etc.).
- **Admin Dashboard Features**:
    - Restore soft-deleted tasks.
    - Permanently delete tasks from the database.
    - Global view of all users and tasks.
- **Security**: 
    - Password hashing using BCrypt.
    - Ownership verification (Users can only see/edit their own tasks).
- **Validation & Error Handling**: Comprehensive global exception handling with descriptive error messages.
- **Audit Trails**: Automatic tracking of `createdAt` and `updatedAt` timestamps.

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Security**: Spring Security 6.x (JWT)
- **Database**: H2 (In-memory for development/testing)
- **ORM**: Spring Data JPA
- **Testing**: JUnit 5, Mockito, MockMvc
- **Documentation**: Swagger/OpenAPI (v3)

## 📋 API Documentation

Once the application is running, you can access the interactive API documentation at:
`http://localhost:8080/swagger-ui.html`

### Core Endpoints

#### Authentication
- `POST /signup`: Register a new user.
- `POST /login`: Authenticate and receive a JWT.
- `POST /refresh`: Refresh an expired access token.

#### User Task Operations
- `GET /tasks`: List all active tasks (Paginated/Filtered).
- `POST /tasks`: Create a new task.
- `PUT /tasks/{id}`: Update an existing task.
- `DELETE /tasks/{id}`: Soft-delete a task.
- `PATCH /tasks/{id}/complete`: Mark task as done.

#### Admin Operations
- `GET /admin/users`: List all registered users.
- `GET /admin/tasks`: List all tasks across the platform (including deleted ones).
- `PATCH /admin/tasks/{id}/restore`: Restore a soft-deleted task.
- `DELETE /admin/tasks/{id}/permanent`: Hard-delete a task.

## ⚙️ Setup Instructions

1. **Prerequisites**: Ensure you have JDK 21 and Maven installed.
2. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd task-management-api
   ```
3. **Build the project**:
   ```bash
   mvn clean install
   ```
4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
5. **Access the Console**: The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskdb`).

## 🧪 Testing

Run the automated test suite (Unit + Integration tests) using:
```bash
mvn test
```

---

*This project was developed as part of a technical hiring assessment and demonstrates backend engineering best practices using Spring Boot, JWT authentication, role-based access control, and RESTful API design.*

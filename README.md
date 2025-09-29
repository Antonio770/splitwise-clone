# Shared Expenses Tracker

## Overview
A Spring Boot implementation of a shared expense tracker inspired by Splitwise.
Users can create groups, add expenses, and track balances with authentication
and group management features.

## Features
- JWT-based authentication
- Group management (create/delete groups, add/remove members)
- Expense tracking within groups

## Tech Stack
- Java, Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Maven
- Postman

## API Endpoints
- **Authentication**
    - `POST /auth/register` - Register a new user
    - `POST /auth/login` - Authenticate and get a JWT
- **User**
    - `GET /user` - Get information about the authenticated user
- **Groups**
    - `GET /groups` - Get all groups the authenticated user is part of
    - `GET /groups/{groupId}` - Get a group
    - `POST /groups` - Create a group
    - `POST /groups/{groupId}/users/{userId}` - Add user to a group
    - `DELETE /groups/{groupId}` - Delete a group
    - `DELETE /groups/{groupId}/users/{userId}` - Remove a user from a group
    - `DELETE /groups/{groupId}/leave` - Leave a group
- **Expenses**
    - `GET /groups/{groupId}/expenses` - Get all expenses of a group
    - `GET /groups/{groupId}/expenses/{expenseId}` - Get an expense
    - `POST /groups/{groupId}/expenses` - Create an expense
    - `GET /groups/{groupId}/balance` - Get a list of all money owed to and 
    owed by all members of a group, as well as the net balance
    - `POST /groups/{groupId}/settle-up/{userId}` - Settle up with another 
    user of a group

## Installation and Setup
### Prerequisites
- Java 17
- Maven
- PostgreSQL
- Postman / curl (for testing)

## Steps

### Clone the repository
```bash
git clone https://github.com/Antonio770/splitwise-clone.git
```

### Start docker
```bash
docker-compose up
```

### Build and run the project
```bash
./mvnw compile
./mvnw spring-boot:run
```

### Test the API
- The app runs on `http://localhost:8080`
- Use **Postman** or **curl** to test the endpoints

## Example usage

### Register a new user

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "secret"}'
```

### Login and get JWT

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "secret"}'
```

This returns a JWT token:

```json
{"token":"eyJhbGciOiJIUzI1NiIs..."}
```

### Get authenticated user info

```bash
curl -X GET http://localhost:8080/user \
  -H "Authorization: Bearer <token>"
```

## Future Improvements
- Custom expense splitting
- Currency management
- Export expenses as csv file
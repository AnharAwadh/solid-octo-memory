# Ride Sharing Platform

A ride-sharing platform built with Microservices Architecture.

## Running the Project

```bash
docker-compose up --build
```

Services:
- Gateway: http://localhost:8080
- Customer Service: http://localhost:8081
- Driver Service: http://localhost:8082

---

## How Security Works

### The Basic Idea

Instead of using JWT tokens, we use Sessions stored in Redis. When a user logs in, we create a Session, save it in Redis, and send back a Cookie with the Session ID.

### Login Steps

1. User sends username and password to Gateway
2. Gateway checks credentials against the database
3. If valid, creates a new Session
4. Saves the Session in Redis
5. Sends back a Cookie named SESSION

### Request Steps After Login

1. User sends a request with the Cookie
2. Gateway reads the Session ID from the Cookie
3. Fetches the Session from Redis
4. Now it knows who the user is and what permissions they have
5. Forwards the request to the target Service with headers containing user info

### Why Redis for Sessions?

If we have multiple Gateway instances, they all need to read the same Sessions. Redis makes Sessions shared between all instances.

### Permissions

- `/auth/*` - Open to everyone
- `/api/customer/*` - Requires role: CUSTOMER
- `/api/driver/*` - Requires role: DRIVER

---

## Sample Users

| Username | Password | Role |
|----------|----------|------|
| customer1 | password123 | CUSTOMER |
| driver1 | password123 | DRIVER |

---

## Endpoints

### Authentication

**Register new user**
```
POST /auth/register
{
  "username": "ahmed",
  "password": "123456",
  "email": "ahmed@example.com",
  "phone": "0501234567",
  "role": "CUSTOMER"
}
```

**Login**
```
POST /auth/login
{
  "username": "customer1",
  "password": "password123"
}
```

**Logout**
```
POST /auth/logout
```




---

### Customer Endpoints

Requires login with role: CUSTOMER

**Request a new ride**
```
POST /api/customer/rides
{
  "pickupLocation": "Airport",
  "dropOffLocation": "Downtown"
}
```

**Get ride history**
```
GET /api/customer/rides/history
```



---

### Driver Endpoints

Requires login with role: DRIVER

**Get profile**
```
GET /api/driver/profile
```

**Update status (online/offline)**
```
PUT /api/driver/status
{
  "status": "ONLINE"
}
```

**Get available rides**
```
GET /api/driver/rides/available
```

**Accept a ride**
```
POST /api/driver/rides/{id}/accept
```



**Get ride history**
```
GET /api/driver/rides/history
```

---

## Examples Using curl

```bash
# Login and save the Cookie
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"username":"customer1","password":"password123"}'

# Request a ride (Cookie sent automatically)
curl -X POST http://localhost:8080/api/customer/rides \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"pickupLocation":"Airport","dropOffLocation":"Hotel"}'

# Get ride history
curl -X GET http://localhost:8080/api/customer/rides/history \
  -b cookies.txt

# Logout
curl -X POST http://localhost:8080/auth/logout \
  -b cookies.txt
```

---

## Tech Stack

- Spring Boot 3.2.5
- Spring Security + Spring Session
- PostgreSQL 16
- Redis 7
- Liquibase
- Docker

---



## Troubleshooting

**If there are database issues:**
```bash
# Delete volumes and restart
docker-compose down -v
docker-compose up --build
```



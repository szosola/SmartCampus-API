# Smart Campus Management System - API Report

## Project Overview
This RESTful API is built using **JAX-RS (javax)** and deployed on **Tomcat 9**. It manages university resources (Rooms and Sensors) using in-memory data structures.

## Setup & Execution
1. **Build**: Use `mvn clean install`.
2. **Deployment**: Deploy the `SmartCampus.war` file to the Tomcat `webapps` folder.
3. **Base URL**: `http://localhost:8080/SmartCampus/api/v1`

---

## PART 1: Setup & Discovery

### 1.1 JAX-RS Resource Lifecycle & Data Integrity
**Question:** Explain the default lifecycle of a JAX-RS Resource class and the strategy for synchronizing in-memory data.

**Answer:** The default lifecycle of a JAX-RS resource is **Request-scoped**. Every time a client sends an HTTP request, the Jersey runtime creates a new instance of the resource class and destroys it after the response is sent. 

**Strategy for Synchronization:** Because instances are destroyed, we cannot store data in standard instance variables. In this project, I used a `static HashMap` within a `DataStore` class. Since the API can handle multiple simultaneous requests (multi-threading), I utilized `ConcurrentHashMap`. This ensures **thread-safety**, preventing "race conditions" where two requests might try to modify the same room data at the exact same millisecond, which would otherwise lead to data corruption.

### 1.2 HATEOAS & Discovery
**Question:** Justify the use of HATEOAS in API design.

**Answer:** HATEOAS (Hypermedia as the Engine of Application State) is a hallmark of advanced RESTful design because it makes the API **self-documenting**. By providing a "Discovery" endpoint that returns links to `/rooms` and `/sensors`, the client does not need to hardcode every URL. This decouples the client from the server, allowing the backend structure to evolve without breaking the frontend.

---

## PART 2: Room Management

### 2.1 HTTP Methods and Business Logic
**Question:** Explain the implementation of the DELETE operation and the logic for linked resources.

**Answer:** The Room Management module implements standard CRUD operations:
* **GET**: Retrieves all rooms or a specific room by ID.
* **POST**: Creates a new room POJO from a JSON payload.
* **DELETE**: Removes a room by ID.

**Logic for Linked Resources:** A critical requirement is preventing the deletion of a room if it contains sensors. In the `deleteRoom` method, the API checks the `sensorCountPerRoom` map. If the count is greater than zero, the API returns an **HTTP 409 Conflict** status. This maintains **referential integrity**, ensuring we don't leave "orphaned" sensors that belong to a room that no longer exists.

---

## Sample API Usage
| Action | Method | URL |
| :--- | :--- | :--- |
| Discover API | GET | `/api/v1/` |
| View All Rooms | GET | `/api/v1/rooms` |
| Add a Room | POST | `/api/v1/rooms` |
| Delete a Room | DELETE | `/api/v1/rooms/{id}` |

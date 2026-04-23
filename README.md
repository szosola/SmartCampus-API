# Smart Campus Management System - API Report

## Project Overview
This RESTful API is built using **JAX-RS (javax)** and deployed on **Tomcat 9**. It manages university resources (Rooms and Sensors) using in-memory data structures, adhering to strict REST principles.

## Setup & Execution

### Prerequisites
* Java JDK 11 or 17
* Apache Maven 3.6+
* Apache Tomcat 9.0

### Step-by-Step Build and Launch
1.  **Clone the Repository**: Download or clone the source code to your local machine.
2.  **Compile and Package**: Open a terminal in the project root and run:
    ```bash
    mvn clean package
    ```
3.  **Locate Artifact**: Navigate to the `target/` directory and find `SmartCampus.war`.
4.  **Deploy to Tomcat**: 
    * Copy `SmartCampus.war` to your Tomcat installation's `webapps` folder.
    * Start Tomcat using `bin/startup.sh` (Linux/Mac) or `bin/startup.bat` (Windows).
5.  **Verify Access**: Open your browser or API client and navigate to:
    `http://localhost:8080/SmartCampus/api/v1`
    
---

## PART 1: Service Architecture & Setup

### 1.1 JAX-RS Resource Lifecycle & Data Integrity
**Question:** Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this impacts in-memory data management.

**Answer:** By default, JAX-RS resources are **Request-scoped**. The runtime creates a new instance of the resource class for every incoming HTTP request and discards it after the response is sent. 
**Architectural Impact:** Because instances are short-lived, instance variables cannot be used to persist data. To prevent data loss, I implemented a `DataStore` class using **static** members. Furthermore, because the server is multi-threaded, I utilized `ConcurrentHashMap` to ensure **thread-safety**, preventing race conditions where simultaneous requests might corrupt the shared state of rooms or sensors.

### 1.2 The "Discovery" Endpoint & HATEOAS
**Question:** Why is the provision of "Hypermedia" considered a hallmark of advanced RESTful design (HATEOAS)? How does this benefit client developers compared to static documentation?

**Answer:** HATEOAS (Hypermedia as the Engine of Application State) allows a client to interact with the API entirely through responses provided dynamically by the server. 
**Benefits:** It makes the API **self-documenting**. A client developer only needs to know the entry point (`/api/v1`). From there, the discovery links guide the client to available resources. This is superior to static documentation because the server can change URI structures without breaking the client, as long as the "rel" (relationship) names in the JSON remain consistent.

---

## PART 2: Room Management

### 2.1 Payload Design: IDs vs. Full Objects
**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects?

**Answer:** * **Returning only IDs:** Minimizes **network bandwidth**, which is beneficial for mobile clients or low-latency environments. However, it forces the client to perform "N+1" requests (one request for the list, then N requests to get details for each ID), increasing server load.
* **Returning Full Objects:** Increases the initial payload size but improves performance by allowing the client to render the entire UI in a **single round-trip**. My implementation returns full objects to prioritize a smoother user experience.

### 2.2 Idempotency in DELETE Operations
**Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification.

**Answer:** Yes, my implementation is **idempotent**. In REST, an operation is idempotent if making the same request multiple times has the same effect on the server state as making it once. 
* **First Request:** The room is deleted, and the server returns `204 No Content`.
* **Subsequent Requests:** The room no longer exists, so the server returns `404 Not Found`.
While the *response code* changes, the **state of the server** remains the same (the room is still gone). Therefore, the operation is idempotent.

---

## PART 3: Sensor Operations & Linking

### 3.1 Technical Consequences of Media Type Mismatch
**Question:** Explain the technical consequences if a client attempts to send data in a different format (e.g., text/plain) despite the `@Consumes(MediaType.APPLICATION_JSON)` annotation.

**Answer:** If a client sends a request with a `Content-Type: text/plain` header to the `POST /sensors` endpoint, the JAX-RS runtime will automatically intercept the request before it reaches my method. The server will return an **HTTP 415 Unsupported Media Type** error. This built-in mechanism ensures that the internal JSON-to-POJO conversion logic (Moxy/Jackson) is never triggered with incompatible data, protecting the application from parsing errors.

### 3.2 Filtering: Query Parameters vs. Path Parameters
**Question:** Contrast the `@QueryParam` approach with making the type part of the URL path (e.g., `/sensors/type/CO2`).

**Answer:** Path parameters are intended to identify a **specific resource** (an identity). Query parameters are intended for **searching, sorting, or filtering** a collection. 
**Justification:** Using `/sensors?type=CO2` is superior because filtering is an optional modifier. If we used path parameters, the URL structure would become rigid and difficult to combine (e.g., filtering by type AND room). Query parameters allow for a clean, flexible URI where the base path remains constant regardless of the filter applied.

---

## PART 4: Deep Nesting with Sub-Resources

### 4.1 Architectural Benefits of Sub-Resource Locators
**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern compared to one massive controller class.

**Answer:** The Sub-Resource Locator pattern promotes the **Single Responsibility Principle**. By delegating `/sensors/{id}/readings` to a dedicated `SensorReadingResource` class, the `SensorResource` only needs to handle high-level sensor logic. This prevents "God Classes" and makes the codebase easier to maintain, test, and scale. It also allows the child resource to easily inherit context (like the `sensorId`) from the parent's path.

---

## PART 5: Advanced Error Handling, Exception Mapping & Logging

### 5.1 Semantics of HTTP 422 vs. 404
**Question:** Why is HTTP 422 (Unprocessable Entity) often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:** A **404 Not Found** indicates that the *URL path* itself does not exist. In the case of posting a sensor to a non-existent room, the endpoint (`/sensors`) exists and the JSON payload is syntactically correct, but it contains a **logical error** (a broken foreign key). **HTTP 422** is more accurate because it signals that the server understands the content type and the syntax of the request is correct, but it cannot process the contained instructions due to semantic errors (the referenced Room ID is missing).

### 5.2 Cybersecurity Risks of Stack Traces
**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:** Exposing stack traces is a significant **Information Leakage** vulnerability. An attacker can gather:
* **Internal Path Logic:** The exact package structure and class names (e.g., `com.smartcampus.repository.DataStore`).
* **Version Info:** Version numbers of the server (Tomcat 9.x) or libraries (Jersey 2.34), allowing them to search for known CVEs (vulnerabilities).
* **Database/Code Structure:** Hints about the underlying database schema or logic flaws that could be exploited for injection attacks.
The `GlobalExceptionMapper` mitigates this by "scrubbing" the error and returning a generic, safe `ErrorMessage` object.

### 5.3 Cross-Cutting Concerns: JAX-RS Filters vs. Manual Logging
**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

**Answer:** Using Filters implements the **DRY (Don't Repeat Yourself)** principle and handles **Cross-Cutting Concerns** centrally.
* **Maintainability:** If I need to change the logging format (e.g., adding timestamps or unique Request IDs), I only change one class (`LoggingFilter`) instead of updating dozens of resource methods.
* **Consistency:** A filter guarantees that *every* request and response is logged, even if a developer forgets to add a log statement to a new endpoint.
* **Decoupling:** It keeps the business logic in the Resource classes "clean" and focused purely on data processing rather than infrastructure concerns.

---

## Status Codes Implemented
| Code | Meaning | Scenario in this Project |
| :--- | :--- | :--- |
| **200** | OK | Successful GET requests. |
| **201** | Created | Successful POST (Room, Sensor, or Reading). |
| **204** | No Content | Successful DELETE of a Room. |
| **403** | Forbidden | POSTing a reading to a sensor in 'MAINTENANCE'. |
| **404** | Not Found | Requesting a Room/Sensor ID that doesn't exist. |
| **409** | Conflict | Deleting a Room that still has active Sensors. |
| **422** | Unprocessable | POSTing a Sensor to a Room ID that doesn't exist. |
| **500** | Server Error | Catch-all for unexpected code crashes. |

---

## Sample API Usage (cURL Commands)

To interact with the API, ensure Tomcat is running and use the following commands:

### 1. API Discovery (HATEOAS)
Retrieves the API metadata, contact information, and resource links.

```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/
```

### 2. Add a New Room
Creates a new room resource.

```Bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
     -H "Content-Type: application/json" \
     -d '{"id": 3, "name": "Server Room", "capacity": 5}'
```

### 3. Add a Sensor (Dependency Validation)
Links a new sensor to an existing room. (Note: If the roomId does not exist, this will trigger the 422 Unprocessable Entity error).

```Bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
     -H "Content-Type: application/json" \
     -d '{"id": 105, "type": "CO2", "currentValue": 400.0, "roomId": 1}'
```

### 4. POST a Reading (State Constraint Test)
Attempts to add a reading to Sensor 102. Since Sensor 102 is marked as 'MAINTENANCE' in the DataStore, this will trigger the custom 403 Forbidden response.

```Bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/102/readings \
     -H "Content-Type: application/json" \
     -d '{"id": 500, "value": 25.5}'
```

### 5. Filter Sensors by Type
Demonstrates the use of query parameters to filter the sensor collection.

```Bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=Temperature"
```

### 6. Delete a Room (Resource Conflict Test)
Attempts to delete Room 1. Because Room 1 has sensors assigned to it in the DataStore, this triggers the 409 Conflict error.

```Bash
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/1
```


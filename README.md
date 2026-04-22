# Smart Campus Management System - API Report

## Project Overview
This RESTful API is built using **JAX-RS (javax)** and deployed on **Tomcat 9**. It manages university resources (Rooms and Sensors) using in-memory data structures, adhering to strict REST principles.

## Setup & Execution
1. **Build**: Use `mvn clean install` (requires JDK 11 or 17).
2. **Deployment**: Deploy the generated `SmartCampus.war` to the Tomcat `webapps` folder.
3. **Base URL**: `http://localhost:8080/SmartCampus/api/v1`

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

## PART 5: Error Handling & Logging (Work in Progress)
*This section will be updated upon completion of the custom ExceptionMappers and Filters.*

---

## Sample API Usage
| Action | Method | URL |
| :--- | :--- | :--- |
| API Discovery | GET | `/api/v1/` |
| List All Rooms | GET | `/api/v1/rooms` |
| Get Specific Room | GET | `/api/v1/rooms/{id}` |
| List All Sensors | GET | `/api/v1/sensors` |
| Filter Sensors | GET | `/api/v1/sensors?type=CO2` |
| Sensor Readings | GET | `/api/v1/sensors/{id}/readings` |

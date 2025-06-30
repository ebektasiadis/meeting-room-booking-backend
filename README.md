# 🏢 Meeting Room Booking (Backend)

## 📌 Project Purpose

This is my first project using **Java Spring Boot** to build a REST API. The main goal was to dive deep into Java Spring
Boot and gain hands-on experience building a backend from scratch.

> [!NOTE]
> Just to make it clear, I didn't use any AI agents or chatbots to generate code for me. In fact, I disabled any kind of
> AI autocompletion feature in my IDE because I wanted to write everything by myself. However, I used Google Gemini to
> review my code, generate requirements, and provide useful information and resources (documentation, videos, etc.)
> whenever I needed them.

> [!NOTE]
> As I become more confident with Spring Boot and external libraries, the folder structure of this project may change
> significantly.
> If you're interested in following my learning journey, feel free to explore the commit history.
> I’ll also try to tag or highlight commits that represent key improvements or upgrades to the codebase.

## 🛠️ Technologies Used

* Java 21
* Maven
* Spring Boot (web, validation)
* Spring Data JPA (with Hibernate)
* PostgreSQL (with JDBC connector)
* Lombok
* Spring Boot Actuator / Devtools
* Docker Compose (for PostgreSQL container)

## ✅ Things I am proud of

* Maintained a **well-structured codebase**
    * Grouped by **technical layers** (not domains), which was intentional due to the project’s size
* Followed **RFC 9457** for structured error responses
* Practiced the **DRY principle** across controllers and services
* Used **Docker Compose** to spin up a PostgreSQL instance instead of using an in-memory DB

## 🚧 Additions / Future improvements

* Add unit and integration tests
* Document endpoints with **OpenAPI (Swagger)**
* Integrate **Keycloak** for user authentication and authorization
* Add **OpenTelemetry** for observability
* Replace manual mapping with a **mapping library** (e.g., MapStruct)
* Use **Flyway or Liquibase** for DB migrations
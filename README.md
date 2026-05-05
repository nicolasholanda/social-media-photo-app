# PhotoShare

A social media photo sharing app where users can publish photos, tag them, browse a timeline, and leave comments.

## Tech Stack

- Java 21
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA + Hibernate
- Thymeleaf + Bootstrap 5
- Flyway (migrations)
- H2 (file-based database)

## Features

- **Register & login** — secure account creation with password hashing
- **Upload photos** — publish images with an optional caption and comma-separated tags
- **Timeline** — paginated feed of all photos ordered by newest first
- **Tag filtering** — click any tag to filter the timeline
- **Photo detail** — full view with comments section
- **Comments** — authenticated users can post and delete their own comments
- **User profiles** — view all photos from a specific user
- **Delete photos** — authors can delete their own photos

## How to Run

You need Java 21 and Maven installed.

```bash
./mvnw spring-boot:run
```

The app starts at `http://localhost:8080`. The H2 database is created automatically at `./data/photosharing` on first run.

To run the tests:

```bash
./mvnw test
```

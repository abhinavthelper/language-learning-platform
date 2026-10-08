# LingoLearn: Online Language Learning Platform

A web application where **learners** take language lessons and quizzes, **instructors** create and manage lessons, and **admins** manage users and approve content.

Built for the GUVI x HCL Java Web Based Project (Review 1 and Round 2).

## Features

| Role | Features |
|---|---|
| Learner | View approved lessons, take quizzes, track progress, forum interactions, profile management (name, learning preference, password) |
| Instructor | Create, edit and delete own lessons (sent for approval), give feedback, view learner progress, lesson analytics |
| Admin | Approve or reject lessons, add, edit and delete users, edit system settings, activity monitoring |

Other highlights:
- Three-tab login (Learner / Instructor / Admin) with server-side role check
- Light and dark theme with a toggle (choice is remembered in the browser)
- `/status` page served by a plain `HttpServlet`

## Tech Stack

- Java, Spring Boot 4.1.1 (Spring MVC)
- Spring Data JPA (Hibernate) and plain JDBC
- Thymeleaf templates, HTML, CSS, JavaScript
- H2 file database (data persists across restarts)
- Maven (Maven Wrapper included)
- Git and GitHub (feature branches and pull requests)

## Project Structure

```
src/main/java/com/guvi/languageplatform
├── controller/   Spring MVC controllers (login, admin, instructor, learner)
├── model/        JPA entities (User, Lesson, QuizAttempt, Feedback, ForumPost, ActivityLog)
├── repository/   Spring Data JPA repositories
├── core/         OOP concepts: interface, abstract class, custom exception, reports, synchronized counter
├── jdbc/         Plain JDBC data access (JdbcStatsDao)
└── servlet/      HttpServlet (/status) and its registration
src/main/resources
├── templates/    Thymeleaf pages (fragments/layout.html is the shared head and navbar)
├── static/       css/style.css, js/theme.js
└── application.properties   Database connection settings
```

## Database Design and Connectivity

The connection is configured in `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:h2:file:./data/languageplatform;AUTO_SERVER=TRUE
spring.datasource.username=sa
spring.jpa.hibernate.ddl-auto=update
```

Data is stored in a local file under the `data/` folder (ignored by Git), so it survives server restarts. Tables are created automatically from the JPA entities, and `DataInitializer` adds sample users and lessons on the first startup.

| Table | Purpose |
|---|---|
| `users` | Accounts with name, email, password, role, learning preference |
| `lessons` | Lessons with content, quiz, status (PENDING / APPROVED / REJECTED), instructor |
| `quiz_attempts` | Learner quiz results |
| `feedback` | Instructor feedback for learners |
| `forum_posts` | Learner discussion messages |
| `activity_logs` | Records of user actions for admin monitoring |

Two ways of accessing the database are used:
- **Spring Data JPA** repositories for normal application features
- **Plain JDBC** (`JdbcStatsDao`) with `Connection`, `PreparedStatement` and `ResultSet`

## Core Java Concepts Used

- **Interface:** `Reportable`
- **Inheritance and abstract class:** `AbstractReport` with `UserReport` and `LessonReport`
- **Polymorphism:** `ReportService` calls every report through the `Reportable` interface
- **Custom exception:** `ResourceNotFoundException`
- **Collections and Streams:** reports group data using `List`, `Map` and `Collectors.groupingBy`
- **Synchronization:** `VisitCounter` uses `synchronized` methods
- **Plain JDBC:** `JdbcStatsDao`
- **Servlet:** `StatusServlet` extends `HttpServlet`

## How to Run

**Requirements:** JDK 17 or higher, and Git. Maven is not needed separately (the wrapper is included).

```
git clone https://github.com/abhinavthelper/language-learning-platform.git
cd language-learning-platform
./mvnw spring-boot:run
```

On Windows PowerShell or CMD, use `.\mvnw spring-boot:run` if `./mvnw` does not work.

Open http://localhost:8080 once the console shows `Started LanguageplatformApplication`.
Stop the server with `Ctrl+C`. If port 8080 is busy, stop the old server first.

Status page: http://localhost:8080/status

The `data/` folder is created automatically on the first run.

## Demo Accounts

| Role | Email | Password |
|---|---|---|
| Admin | admin@mail.com | admin123 |
| Instructor | inst@mail.com | inst123 |
| Learner | learner@mail.com | learn123 |

Pick the matching tab on the login page.

## Team

| Member | GitHub | Contribution |
|---|---|---|
| Abhinav Verma (Team Lead) | abhinavthelper | Project setup, database models and connectivity, login and role check, UI design and theme, core Java concepts, JDBC, servlet, pull request reviews and merging |
| Nishant Kumar | nishantsalar | Instructor module (lessons, edit and delete, feedback, learner progress, analytics) |
| Abhijay Pandey | abhijaypandeygu-coder | Admin module (content approval, user management, system settings) |
| Janhvi | janhvibhati019 | Learner module (lessons, quizzes, progress, forum, profile) |

Work was split into feature branches (for example `admin-module`, `instructor-module`, `learner-module`, `learner-profile`, `lesson-edit`, `admin-edit`) and merged into `main` through pull requests.

## Known Limitations and Future Work

- Passwords are stored as plain text (planned: BCrypt hashing with Spring Security)
- Some admin pages check only that the user is logged in, not the exact role (planned: Spring Security route protection)
- System settings are kept in memory and reset to defaults when the server restarts (planned: store them in a database table)
- Email is read-only in profile and user edit, because tables are linked by email
- Tables are linked by email, not by foreign keys (planned: proper `@ManyToOne` relations)
- H2 is a file database for development (planned: MySQL or PostgreSQL)
- Planned: Google sign-in for learners

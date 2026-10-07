# LingoLearn: Online Language Learning Platform

A web application where **learners** take language lessons and quizzes, **instructors** create lessons, and **admins** manage users and approve content.

Built for the GUVI x HCL Java Web Based Project (Review 1).

## Features

| Role | Working now | In progress |
|---|---|---|
| Learner | View approved lessons, take quizzes | Progress tracking, interactions, profile management |
| Instructor | Create lessons (sent for approval), view own lessons and status | Feedback, learner progress, lesson analytics |
| Admin | Approve or reject lessons, view users, view system settings | User create/edit/delete, activity monitoring |

Other highlights:
- Three-tab login (Learner / Instructor / Admin) with server-side role check
- Light and dark theme with a toggle (choice is remembered in the browser)
- `/status` page served by a plain `HttpServlet`

## Tech Stack

- Java, Spring Boot 4.1.1 (Spring MVC)
- Spring Data JPA (Hibernate) and plain JDBC
- Thymeleaf templates, HTML, CSS, JavaScript
- H2 database (development)
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
└── static/       css/style.css, js/theme.js
```

## Database Design

| Table | Purpose |
|---|---|
| `users` | Accounts with name, email, password, role |
| `lessons` | Lessons with content, quiz, status (PENDING / APPROVED / REJECTED), instructor |
| `quiz_attempts` | Learner quiz results |
| `feedback` | Instructor feedback for learners |
| `forum_posts` | Learner discussion messages |
| `activity_logs` | Records of user actions for admin monitoring |

The tables are created automatically from the JPA entities. `DataInitializer` adds sample users and lessons on startup.

## Core Java Concepts Used

- **Interface:** `Reportable`
- **Inheritance and abstract class:** `AbstractReport` with `UserReport` and `LessonReport`
- **Polymorphism:** `ReportService` calls every report through the `Reportable` interface
- **Custom exception:** `ResourceNotFoundException`
- **Collections and Streams:** reports group data using `List`, `Map` and `Collectors.groupingBy`
- **Synchronization:** `VisitCounter` uses `synchronized` methods
- **Plain JDBC:** `JdbcStatsDao` uses `Connection`, `PreparedStatement`, `ResultSet`
- **Servlet:** `StatusServlet` extends `HttpServlet`

## How to Run

**Requirements:** JDK 17 or higher, Git. Maven is not needed separately (the wrapper is included).

```
git clone https://github.com/abhinavthelper/language-learning-platform.git
cd language-learning-platform
./mvnw spring-boot:run
```

On Windows PowerShell or CMD, use `mvnw spring-boot:run` if `./mvnw` does not work.

Open http://localhost:8080 once the console shows `Started LanguageplatformApplication`.
Stop the server with `Ctrl+C`. If port 8080 is busy, stop the old server first.

Status page: http://localhost:8080/status

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
| Abhinav Verma (Team Lead) | abhinavthelper | Project setup, database models, login and role check, UI design and theme, core Java concepts, JDBC, servlet |
| Nishant Kumar | nishantsalar | Instructor module |
| Abhijay Pandey | abhijaypandeygu-coder | Admin module |
| Janhvi | janhvibhati019 | Learner module |

Work was split into feature branches (`admin-module`, `instructor-module`, `learner-module`, `ui-redesign`, `phase1-base`) and merged into `main` through pull requests.

## Known Limitations and Future Work

- Passwords are stored as plain text (planned: BCrypt hashing with Spring Security)
- No route protection yet (planned: Spring Security)
- H2 data resets on restart (planned: MySQL or PostgreSQL)
- Remaining dashboard features: progress tracking, feedback, forum, profile, analytics, activity monitoring
- Planned: Google sign-in for learners
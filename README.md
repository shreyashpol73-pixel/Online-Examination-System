# Online Examination System

A robust, full-featured desktop-based examination management and assessment application developed in Java Swing and backed by MySQL. The system provides secure role-based access for administrators and students, supports diverse question formats, enforces automated timed exam sessions, and delivers real-time grading and performance analytics.

---

## 📋 Table of Contents
- [Features](#features)
  - [Admin Module](#admin-module)
  - [Student Module](#student-module)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
  - [1. Clone Repository](#1-clone-repository)
  - [2. Database Setup](#2-database-setup)
  - [3. Download JDBC Driver](#3-download-jdbc-driver)
  - [4. Build & Compilation](#4-build--compilation)
  - [5. Execution](#5-execution)
- [Default Credentials](#default-credentials)
- [Screenshots](#screenshots)
- [License](#license)

---

## ✨ Features

### Admin Module
- **User Management**: Add, update, view, and manage student accounts and faculty profiles.
- **Exam Management**: Create, edit, and schedule exams with custom duration, passing criteria, and instructions.
- **Question Bank Management**:
  - Add, edit, and organize questions categorized by subject and difficulty.
  - Support for multiple question formats:
    - **Multiple Choice Questions (MCQ)** with single/multiple options
    - **True / False** statements
    - **Fill-in-the-Blank** questions
- **Results & Analytics**:
  - Comprehensive view of student attempts, scores, and completion timestamps.
  - Analytical breakdown of pass/fail percentages, class averages, and score distributions.
  - Exportable score sheets and summary reports.

### Student Module
- **Authentication**: Secure registration and login mechanisms.
- **Student Dashboard**: Overview of available, scheduled, and completed assessments.
- **Interactive Examination Engine**:
  - Live countdown timer with auto-submission when time elapses.
  - Real-time question navigation palette (Answered, Marked for Review, Unvisited).
  - Clear answer selection with option to update or review before final submission.
- **Instant Result & Performance Review**:
  - Immediate score calculation upon exam submission.
  - Detailed score breakdown, accuracy rates, and answer analysis.
  - Historical exam attempt tracking.

---

## 💻 Tech Stack

- **Programming Language**: Java 8+
- **GUI Framework**: Java Swing & AWT (Modern look-and-feel)
- **Database**: MySQL 5.7+ / 8.x
- **Database Connectivity**: JDBC (Java Database Connectivity) with MySQL Connector/J

---

## ⚙️ Prerequisites

Before getting started, ensure the following are installed and configured on your system:
- **Java Development Kit (JDK)**: JDK 8 or higher (Ensure `JAVA_HOME` and `PATH` are set).
- **MySQL Server**: Version 5.7 or higher / MySQL 8.x.
- **MySQL Connector/J**: `mysql-connector-j-8.x.jar` driver for JDBC connectivity.

---

## 📁 Project Structure

```
Online-Examination-System/
├── .gitignore                          # Git ignore rules for Java/IDE files
├── build.bat                           # Windows compilation script
├── run.bat                             # Windows launch script
├── README.md                           # Project documentation
├── lib/                                # External libraries
│   ├── .gitkeep                        # Ensures directory tracking in git
│   └── mysql-connector-j-8.x.jar       # MySQL Connector/J driver (place here)
├── sql/
│   └── schema.sql                      # Database schema and seed data
└── src/
    └── com/
        └── examportal/
            ├── Main.java               # Application entry point
            ├── db/                     # Database connection & pooling
            │   ├── DatabaseConnection.java
            │   └── DBConfig.java
            ├── model/                  # Data models / POJOs
            │   ├── User.java
            │   ├── Exam.java
            │   ├── Question.java
            │   ├── QuestionType.java
            │   ├── Option.java
            │   ├── ExamAttempt.java
            │   └── StudentAnswer.java
            ├── dao/                    # Data Access Objects (CRUD)
            │   ├── UserDAO.java
            │   ├── ExamDAO.java
            │   ├── QuestionDAO.java
            │   └── ResultDAO.java
            └── ui/                     # Swing GUI views & components
                ├── LoginFrame.java
                ├── RegisterFrame.java
                ├── AdminDashboard.java
                ├── StudentDashboard.java
                ├── ExamFrame.java
                ├── ResultDialog.java
                └── components/
                    ├── TimerPanel.java
                    └── QuestionNavPanel.java
```

---

## 🚀 Setup & Installation

### 1. Clone Repository
```bash
git clone https://github.com/your-username/Online-Examination-System.git
cd Online-Examination-System
```

### 2. Database Setup
1. Start your MySQL service.
2. Open MySQL CLI or MySQL Workbench and create the database:
   ```sql
   CREATE DATABASE online_exam_db;
   ```
3. Execute the schema file to generate required tables and default records:
   ```bash
   mysql -u root -p online_exam_db < sql/schema.sql
   ```
4. Verify database credentials in `src/com/examportal/db/DBConfig.java` (or equivalent configuration) match your MySQL username and password.

### 3. Download JDBC Driver
Download the MySQL Connector/J JAR (`mysql-connector-j-8.x.jar`) from Maven Central or the official MySQL website:
- **Maven Central**: [Download mysql-connector-j](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/)
- Place the downloaded `.jar` file inside the `lib/` directory:
  ```
  lib/mysql-connector-j-8.x.jar
  ```

### 4. Build & Compilation

#### Using the provided Windows script:
Double-click or run:
```cmd
build.bat
```

#### Manual Compilation:
```bash
# Windows
javac -cp ".;lib/mysql-connector-j-8.x.jar" -d out src/com/examportal/**/*.java src/com/examportal/*.java

# Linux / macOS
javac -cp ".:lib/mysql-connector-j-8.x.jar" -d out $(find src -name "*.java")
```

### 5. Execution

#### Using the provided Windows script:
Double-click or run:
```cmd
run.bat
```

#### Manual Execution:
```bash
# Windows
java -cp ".;out;lib/mysql-connector-j-8.x.jar" com.examportal.Main

# Linux / macOS
java -cp ".:out:lib/mysql-connector-j-8.x.jar" com.examportal.Main
```

---

## 🔑 Default Credentials

After running `sql/schema.sql`, default administrative credentials are initialized:

| Role | Username | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` |

> *Note: For security reasons, please update the default administrative credentials upon first login in production environments.*

---

## 🖼️ Screenshots

*(Screenshots will be added here upon application run)*

| Login Screen | Student Dashboard |
| :---: | :---: |
| *(Placeholder: Login Interface)* | *(Placeholder: Student Dashboard)* |

| Examination Interface with Timer | Performance Analytics |
| :---: | :---: |
| *(Placeholder: Exam & Palette)* | *(Placeholder: Admin Analytics)* |

---

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

-- =====================================================================
-- Online Examination System Database Schema
-- Database: online_exam_db
-- =====================================================================

CREATE DATABASE IF NOT EXISTS online_exam_db;
USE online_exam_db;

-- Drop tables in reverse order of foreign key dependencies if needed
DROP TABLE IF EXISTS student_answers;
DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- Table: users
-- Stores administrative and student user accounts
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table: exams
-- Stores test definitions created by administrators
-- ---------------------------------------------------------------------
CREATE TABLE exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 30,
    total_marks INT NOT NULL,
    passing_marks INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table: questions
-- Supports MCQ (multiple choice), TRUE_FALSE, and FILL_BLANK
-- ---------------------------------------------------------------------
CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MCQ', 'TRUE_FALSE', 'FILL_BLANK') NOT NULL,
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer VARCHAR(255) NOT NULL,
    marks INT NOT NULL DEFAULT 1,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table: results
-- Records student exam attempt summaries
-- ---------------------------------------------------------------------
CREATE TABLE results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    exam_id INT NOT NULL,
    score INT NOT NULL,
    total_marks INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    status ENUM('PASS', 'FAIL') NOT NULL,
    time_taken_seconds INT,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table: student_answers
-- Records specific question responses per exam attempt
-- ---------------------------------------------------------------------
CREATE TABLE student_answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_answer VARCHAR(255),
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (result_id) REFERENCES results(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- Seed Data
-- =====================================================================

-- Insert Default Admin
INSERT INTO users (username, password, full_name, email, role) 
VALUES ('admin', 'admin123', 'System Administrator', 'admin@exam.com', 'ADMIN');

-- Insert Sample Students
INSERT INTO users (username, password, full_name, email, role) 
VALUES ('student1', 'pass123', 'John Doe', 'john.doe@example.com', 'STUDENT'),
       ('student2', 'pass123', 'Jane Smith', 'jane.smith@example.com', 'STUDENT');

-- Insert Sample Exam
INSERT INTO exams (exam_id, title, description, duration_minutes, total_marks, passing_marks, is_active, created_by)
VALUES (1, 'Java Programming Fundamentals', 'Test covering core Java concepts including OOP, types, collections, and concurrency.', 30, 20, 10, TRUE, 1);

-- Insert Sample Questions for Exam 1 (10 Questions: 4 MCQ, 3 TRUE_FALSE, 3 FILL_BLANK)
INSERT INTO questions (exam_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, marks) VALUES
-- MCQ Questions
(1, 'Which of the following is not a Java primitive data type?', 'MCQ', 'int', 'String', 'boolean', 'double', 'String', 2),
(1, 'What is the default bit size of an int variable in Java?', 'MCQ', '8 bits', '16 bits', '32 bits', '64 bits', '32 bits', 2),
(1, 'Which collection class guarantees unique elements and does not maintain insertion order?', 'MCQ', 'ArrayList', 'HashSet', 'LinkedList', 'Vector', 'HashSet', 2),
(1, 'Which keyword is used to prevent a method from being overridden in a subclass?', 'MCQ', 'static', 'final', 'abstract', 'synchronized', 'final', 2),

-- TRUE_FALSE Questions
(1, 'Java supports multiple inheritance of state through classes.', 'TRUE_FALSE', 'True', 'False', NULL, NULL, 'False', 2),
(1, 'The default value of an uninitialized boolean instance variable in Java is false.', 'TRUE_FALSE', 'True', 'False', NULL, NULL, 'True', 2),
(1, 'String objects in Java are immutable once created in memory.', 'TRUE_FALSE', 'True', 'False', NULL, NULL, 'True', 2),

-- FILL_BLANK Questions
(1, 'The keyword used by a class to inherit another class in Java is ______.', 'FILL_BLANK', NULL, NULL, NULL, NULL, 'extends', 2),
(1, 'The method called on a Thread object to begin concurrent execution is ______.', 'FILL_BLANK', NULL, NULL, NULL, NULL, 'start', 2),
(1, 'The root superclass of every class hierarchy in Java is java.lang.______.', 'FILL_BLANK', NULL, NULL, NULL, NULL, 'Object', 2);

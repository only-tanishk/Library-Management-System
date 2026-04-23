-- ============================================================
-- Library Management System v2 - FRESH Database Setup
-- Run this ENTIRE script in MySQL Workbench before starting app
-- ============================================================

-- Drop and recreate database
DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db;
USE library_db;

-- ─── 1. Users table (admin + students) ───────────────────────
CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(150) NOT NULL,
    username VARCHAR(50)  UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role     ENUM('admin', 'student') NOT NULL DEFAULT 'student'
);

-- ─── 2. Books table ───────────────────────────────────────────
CREATE TABLE books (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    book_code VARCHAR(20)  UNIQUE NOT NULL,
    title     VARCHAR(200) NOT NULL,
    author    VARCHAR(150) NOT NULL,
    quantity  INT          NOT NULL DEFAULT 0
);

-- ─── 3. Transactions table ────────────────────────────────────
CREATE TABLE transactions (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT            NOT NULL,
    user_id     INT            NOT NULL,
    issue_date  DATE           NOT NULL,
    return_date DATE           DEFAULT NULL,      -- NULL = not yet returned
    fine_paid   DECIMAL(10,2)  NOT NULL DEFAULT 0, -- amount collected on return
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ─── 4. Seed data ─────────────────────────────────────────────
-- Default admin account (username: admin, password: admin123)
INSERT INTO users (name, username, password, role) VALUES
    ('Administrator', 'admin', 'admin123', 'admin');

-- Sample books
INSERT INTO books (book_code, title, author, quantity) VALUES
    ('BK001', 'Clean Code',               'Robert C. Martin', 3),
    ('BK002', 'Head First Java',           'Kathy Sierra',     5),
    ('BK003', 'Effective Java',            'Joshua Bloch',     4),
    ('BK004', 'The Pragmatic Programmer',  'David Thomas',     2),
    ('BK005', 'Design Patterns',           'Gang of Four',     3);

-- ============================================================
-- Done! Admin login: username=admin / password=admin123
-- Students can sign up from the Login screen.
-- ============================================================

# 📚 Library Management System

A simple, clean **Library Management System** built with **Java Swing GUI** and **MySQL database (JDBC)**.
No frameworks — just plain Java, perfect for academic projects.

---

## 🗂️ Project Structure

```
LMS BASIC/
├── src/
│   ├── model/              ← Data classes (Book, User, Transaction)
│   │   ├── Book.java
│   │   ├── User.java
│   │   └── Transaction.java
│   ├── dao/                ← Database logic (JDBC)
│   │   ├── DBConnection.java
│   │   ├── BookDAO.java
│   │   ├── UserDAO.java
│   │   └── TransactionDAO.java
│   ├── ui/                 ← Swing GUI screens
│   │   ├── Dashboard.java
│   │   ├── BookScreen.java
│   │   ├── IssueBookScreen.java
│   │   ├── ReturnBookScreen.java
│   │   └── UserScreen.java
│   └── main/
│       └── Main.java       ← Entry point
├── database/
│   └── library_db.sql      ← SQL setup script
├── lib/                    ← Put mysql-connector-java.jar here
├── out/                    ← Compiled .class files (auto-created)
└── run.bat                 ← One-click compile & run script
```

---

## ✅ Prerequisites

| Tool | Version |
|------|---------|
| JDK  | 8 or higher |
| MySQL| 5.7 or higher |
| MySQL JDBC Driver | `mysql-connector-java-8.x.x.jar` |

---

## ⚙️ Setup Instructions

### Step 1 — Set up the database

1. Open **MySQL Workbench** or **MySQL CLI**
2. Run the SQL script:
   ```sql
   SOURCE /path/to/database/library_db.sql;
   ```
   Or open the file in MySQL Workbench and click **Execute**.

### Step 2 — Configure DB credentials

Open `src/dao/DBConnection.java` and update:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/library_db";
private static final String USER     = "root";       // your MySQL username
private static final String PASSWORD = "root";       // your MySQL password
```

### Step 3 — Add JDBC Driver

1. Download **`mysql-connector-java.jar`** from:  
   https://dev.mysql.com/downloads/connector/j/
2. Place the `.jar` file in the `lib/` folder (create it if it doesn't exist):
   ```
   LMS BASIC/lib/mysql-connector-java-8.x.x.jar
   ```
3. Rename it to `mysql-connector-java.jar` (or update `run.bat` with the actual filename)

### Step 4 — Compile & Run

**Option A: Double-click `run.bat`** *(easiest)*

**Option B: Manual compile from terminal**
```batch
cd "c:\Users\tanis\OneDrive\Documents\LMS BASIC"

REM Compile
javac -cp "lib\mysql-connector-java.jar" -d out src\model\*.java src\dao\*.java src\ui\*.java src\main\Main.java

REM Run
java -cp "out;lib\mysql-connector-java.jar" main.Main
```

---

## 🖥️ Features

| Feature | Description |
|---------|-------------|
| 📖 Add Book | Add new books with title, author, quantity |
| 🔍 Search Books | Search by title or author keyword |
| 📤 Issue Book | Issue a book to a registered user |
| 📥 Return Book | Return an issued book, auto-updates quantity |
| 👤 Manage Users | Add and view library members |

---

## 🧠 Concepts Used

- **OOP** — Encapsulation with model classes
- **JDBC** — `PreparedStatement` for safe DB queries
- **Java Swing** — `JFrame`, `JPanel`, `JTable`, `JComboBox`, `JButton`
- **MVC-like separation** — UI ↔ DAO ↔ DB clearly separated
- **Exception handling** — Try-catch in all DB operations

---

## 🗄️ Database Schema

```sql
books        → id, title, author, quantity
users        → id, name
transactions → id, book_id, user_id, issue_date, return_date
```

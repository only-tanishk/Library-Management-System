@echo off
REM ============================================================
REM  Library Management System v2 — Compile & Run (Windows)
REM  Requires: JDK 8+  |  lib\mysql-connector-java.jar
REM ============================================================

echo Compiling Library Management System v2...

if not exist out mkdir out

javac -cp "lib\mysql-connector-java.jar" -d out ^
    src\model\Book.java ^
    src\model\User.java ^
    src\model\Transaction.java ^
    src\dao\DBConnection.java ^
    src\dao\BookDAO.java ^
    src\dao\UserDAO.java ^
    src\dao\TransactionDAO.java ^
    src\ui\AppFrame.java ^
    src\ui\UITheme.java ^
    src\ui\LoginScreen.java ^
    src\ui\SignupScreen.java ^
    src\ui\AdminDashboard.java ^
    src\ui\StudentDashboard.java ^
    src\ui\ManageBooksScreen.java ^
    src\ui\ActiveStudentsScreen.java ^
    src\ui\TransactionHistoryScreen.java ^
    src\ui\IssueBookAdminScreen.java ^
    src\ui\AvailableBooksScreen.java ^
    src\ui\SelfIssueScreen.java ^
    src\ui\MyTransactionsScreen.java ^
    src\main\Main.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Check errors above.
    pause
    exit /b 1
)

echo.
echo Compilation successful! Launching application...
echo.

java -cp "out;lib\mysql-connector-java.jar" main.Main

pause

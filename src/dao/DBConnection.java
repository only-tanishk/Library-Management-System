package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles MySQL database connection using JDBC.
 * Single-point of connection management for the entire application.
 */
public class DBConnection {

    // ---------------------------------------------------------------
    // ⚙️  CONFIGURE YOUR DATABASE CREDENTIALS HERE
    // ---------------------------------------------------------------
    private static final String URL      = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Tanishk@123";
    // ---------------------------------------------------------------

    private static Connection connection = null;

    /**
     * Returns a singleton Connection instance.
     * Creates a new connection if none exists or the current one is closed.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found! Add mysql-connector-java.jar to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed! Check credentials in DBConnection.java");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the database connection gracefully.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

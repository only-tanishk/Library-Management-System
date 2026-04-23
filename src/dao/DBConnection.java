package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    
    private static final String URL      = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Tanishk@123";
   
    private static Connection connection = null;

    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
               
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println(" Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println(" MySQL JDBC Driver not found! Add mysql-connector-java.jar to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(" Database connection failed! Check credentials in DBConnection.java");
            e.printStackTrace();
        }
        return connection;
    }

     
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

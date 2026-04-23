package dao;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    // --Login --
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Login error: " + e.getMessage());
        }
        return null; // login failed
    }

    // ─── Signup: register a new student account ────────────────────────────────
    public boolean signup(User user) {
        String sql = "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, 'student')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName().trim());
            ps.setString(2, user.getUsername().trim());
            ps.setString(3, user.getPassword().trim());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Signup error: " + e.getMessage());
            return false;
        }
    }

    // ─── Check if a username already exists (for signup validation) ───────────
    public boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if found

        } catch (SQLException e) {
            System.err.println("❌ Username check error: " + e.getMessage());
            return false;
        }
    }

    // ─── Get all students (for admin active students view) ────────────────────
    public List<User> getAllStudents() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'student' ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching students: " + e.getMessage());
        }
        return list;
    }

    // ─── Get all users (for admin issue dropdown) ────────────────────────────
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'student' ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
        }
        return list;
    }
}

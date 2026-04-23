package dao;

import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Book CRUD operations including edit, delete, and search by code.
 */
public class BookDAO {

    // ─── Add a new book ───────────────────────────────────────────────────────
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (book_code, title, author, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getBookCode().trim().toUpperCase());
            ps.setString(2, book.getTitle().trim());
            ps.setString(3, book.getAuthor().trim());
            ps.setInt(4, book.getQuantity());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding book: " + e.getMessage());
            return false;
        }
    }

    // ─── Update an existing book ──────────────────────────────────────────────
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET book_code=?, title=?, author=?, quantity=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getBookCode().trim().toUpperCase());
            ps.setString(2, book.getTitle().trim());
            ps.setString(3, book.getAuthor().trim());
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating book: " + e.getMessage());
            return false;
        }
    }

    // ─── Delete a book ────────────────────────────────────────────────────────
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting book: " + e.getMessage());
            return false;
        }
    }

    // ─── Get all books ────────────────────────────────────────────────────────
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching books: " + e.getMessage());
        }
        return books;
    }

    // ─── Get available books (qty > 0) ────────────────────────────────────────
    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE quantity > 0 ORDER BY book_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching available books: " + e.getMessage());
        }
        return books;
    }

    // ─── Search books ─────────────────────────────────────────────────────────
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR book_code LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ Error searching books: " + e.getMessage());
        }
        return books;
    }

    // ─── Find a book by its unique code ──────────────────────────────────────
    public Book getBookByCode(String code) {
        String sql = "SELECT * FROM books WHERE book_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim().toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ Error finding book by code: " + e.getMessage());
        }
        return null;
    }

    // ─── Get book by ID ───────────────────────────────────────────────────────
    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ Error finding book by id: " + e.getMessage());
        }
        return null;
    }

    // ─── Update quantity only ─────────────────────────────────────────────────
    public boolean updateQuantity(int bookId, int qty) {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qty); ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating quantity: " + e.getMessage());
            return false;
        }
    }

    // ─── Helper: map ResultSet row to Book ───────────────────────────────────
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("book_code"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getInt("quantity")
        );
    }
}

package dao;

import model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for transaction operations: issue, return (with fine), and queries.
 */
public class TransactionDAO {

    private BookDAO bookDAO = new BookDAO();

    private static final int MAX_BOOKS_PER_STUDENT = 2;

    // ─── Issue a book ─────────────────────────────────────────────────────────
    public String issueBook(int bookId, int userId) {
        // Check book exists and has stock
        model.Book book = bookDAO.getBookById(bookId);
        if (book == null)          return "Book not found.";
        if (book.getQuantity() <= 0) return "Book is out of stock.";

        // Check student hasn't exceeded 2-book limit
        if (getActiveBookCount(userId) >= MAX_BOOKS_PER_STUDENT)
            return "Student already has " + MAX_BOOKS_PER_STUDENT + " books issued. Cannot issue more.";

        String sql = "INSERT INTO transactions (book_id, user_id, issue_date) VALUES (?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.setInt(2, userId);
            if (ps.executeUpdate() > 0) {
                bookDAO.updateQuantity(bookId, book.getQuantity() - 1);
                return null; // null = success
            }
        } catch (SQLException e) {
            System.err.println("❌ Error issuing book: " + e.getMessage());
        }
        return "Database error while issuing book.";
    }

    // ─── Return a book with fine collected ────────────────────────────────────
    public boolean returnBook(int transactionId, double fineCollected) {
        String fetchSql = "SELECT * FROM transactions WHERE id = ? AND return_date IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement fetchPs = conn.prepareStatement(fetchSql)) {

            fetchPs.setInt(1, transactionId);
            ResultSet rs = fetchPs.executeQuery();
            if (!rs.next()) return false;

            int bookId = rs.getInt("book_id");

            // Mark as returned and record fine paid
            String updateSql = "UPDATE transactions SET return_date = CURDATE(), fine_paid = ? WHERE id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setDouble(1, fineCollected);
                updatePs.setInt(2, transactionId);
                updatePs.executeUpdate();
            }

            // Restore book stock
            model.Book book = bookDAO.getBookById(bookId);
            if (book != null) {
                bookDAO.updateQuantity(bookId, book.getQuantity() + 1);
            }
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error returning book: " + e.getMessage());
            return false;
        }
    }

    // ─── Count active (unreturned) books for a student ────────────────────────
    public int getActiveBookCount(int userId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND return_date IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("❌ Error counting active books: " + e.getMessage());
        }
        return 0;
    }

    // ─── Sum of fine_paid for a student (total paid) ──────────────────────────
    public double getTotalFinePaid(int userId) {
        String sql = "SELECT COALESCE(SUM(fine_paid), 0) FROM transactions WHERE user_id = ? AND return_date IS NOT NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            System.err.println("❌ Error fetching paid fines: " + e.getMessage());
        }
        return 0;
    }

    // ─── All transactions with book and user info (admin history view) ────────
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.book_id, t.user_id, t.issue_date, t.return_date, t.fine_paid, " +
                     "b.title AS book_title, b.book_code, u.name AS user_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN users u ON t.user_id = u.id " +
                     "ORDER BY t.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ Error fetching all transactions: " + e.getMessage());
        }
        return list;
    }

    // ─── Active (unreturned) transactions for all users ───────────────────────
    public List<Transaction> getActiveTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.book_id, t.user_id, t.issue_date, t.return_date, t.fine_paid, " +
                     "b.title AS book_title, b.book_code, u.name AS user_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN users u ON t.user_id = u.id " +
                     "WHERE t.return_date IS NULL ORDER BY t.issue_date";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ Error fetching active transactions: " + e.getMessage());
        }
        return list;
    }

    // ─── Transactions for a specific student ──────────────────────────────────
    public List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.book_id, t.user_id, t.issue_date, t.return_date, t.fine_paid, " +
                     "b.title AS book_title, b.book_code, u.name AS user_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN users u ON t.user_id = u.id " +
                     "WHERE t.user_id = ? ORDER BY t.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ Error fetching user transactions: " + e.getMessage());
        }
        return list;
    }

    // ─── Helper: map ResultSet to Transaction ─────────────────────────────────
    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setBookId(rs.getInt("book_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setIssueDate(rs.getDate("issue_date"));
        t.setReturnDate(rs.getDate("return_date"));
        t.setFinePaid(rs.getDouble("fine_paid"));
        t.setBookTitle(rs.getString("book_title"));
        t.setBookCode(rs.getString("book_code"));
        t.setUserName(rs.getString("user_name"));
        return t;
    }
}

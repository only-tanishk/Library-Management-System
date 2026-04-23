package ui;

import dao.BookDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import model.Book;
import model.Transaction;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Screen to issue a book to a user.
 * Shows dropdowns for book and user selection.
 */
public class IssueBookScreen extends JFrame {

    private BookDAO bookDAO             = new BookDAO();
    private UserDAO userDAO             = new UserDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    private JComboBox<Book> bookCombo;
    private JComboBox<User> userCombo;
    private DefaultTableModel tableModel;

    public IssueBookScreen() {
        setTitle("📤 Issue Book");
        setSize(780, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadData();
        setVisible(true);
    }

    // ─── Top panel: Issue form ────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));
        panel.setBackground(new Color(255, 250, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel heading = new JLabel("📤 Issue Book to User");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(heading, gbc);
        gbc.gridwidth = 1;

        // Book dropdown
        gbc.gridy = 1;
        gbc.gridx = 0; panel.add(new JLabel("Select Book:"), gbc);
        bookCombo = new JComboBox<>();
        bookCombo.setPreferredSize(new Dimension(250, 28));
        gbc.gridx = 1; panel.add(bookCombo, gbc);

        // User dropdown
        gbc.gridx = 2; panel.add(new JLabel("Select User:"), gbc);
        userCombo = new JComboBox<>();
        userCombo.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 3; panel.add(userCombo, gbc);

        // Issue button
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 4;
        JButton issueBtn = new JButton("  Issue Book  ");
        styleButton(issueBtn, new Color(218, 112, 0));
        issueBtn.addActionListener(e -> handleIssue());
        panel.add(issueBtn, gbc);

        return panel;
    }

    // ─── Center panel: Active transactions table ──────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JLabel label = new JLabel("  📋 Currently Issued Books (not yet returned)");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, BorderLayout.NORTH);

        String[] columns = {"Trans. ID", "Book Title", "Issued To", "Issue Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ─── Load books and users into dropdowns & table ──────────────────────────
    private void loadData() {
        // Populate book dropdown
        bookCombo.removeAllItems();
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            bookCombo.addItem(b); // Book.toString() is not used here; we set renderer below
        }
        // Renderer to show title + quantity
        bookCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value == null ? "" : value.getTitle() + " (qty: " + value.getQuantity() + ")");
            if (isSelected) { lbl.setBackground(list.getSelectionBackground()); lbl.setOpaque(true); }
            return lbl;
        });

        // Populate user dropdown
        userCombo.removeAllItems();
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            userCombo.addItem(u); // User.toString() returns name
        }

        // Load active transactions into table
        tableModel.setRowCount(0);
        List<Transaction> active = transactionDAO.getActiveTransactions();
        for (Transaction t : active) {
            tableModel.addRow(new Object[]{
                t.getId(), t.getBookTitle(), t.getUserName(), t.getIssueDate()
            });
        }
    }

    // ─── Handle issue action ──────────────────────────────────────────────────
    private void handleIssue() {
        Book selectedBook = (Book) bookCombo.getSelectedItem();
        User selectedUser = (User) userCombo.getSelectedItem();

        if (selectedBook == null || selectedUser == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a book and a user.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedBook.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "❌ Sorry, this book is out of stock!", "Out of Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Issue \"" + selectedBook.getTitle() + "\" to " + selectedUser.getName() + "?",
            "Confirm Issue", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = transactionDAO.issueBook(selectedBook.getId(), selectedUser.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Refresh dropdown and table
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to issue book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
    }
}

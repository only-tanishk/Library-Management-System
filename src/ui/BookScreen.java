package ui;

import dao.BookDAO;
import model.Book;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Screen to add a new book and view/search all books.
 */
public class BookScreen extends JFrame {

    private BookDAO bookDAO = new BookDAO();

    // ─── Form fields for adding a book ────────────────────────────────────────
    private JTextField titleField    = new JTextField(20);
    private JTextField authorField   = new JTextField(20);
    private JTextField quantityField = new JTextField(5);
    private JTextField searchField   = new JTextField(15);

    // ─── Table to display books ───────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable bookTable;

    public BookScreen() {
        setTitle("📚 Books Management");
        setSize(750, 550);
        setLocationRelativeTo(null);         // center on screen
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Build and add panels
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadBooks();  // Load all books on open
        setVisible(true);
    }

    // ─── Top panel: Add Book form ─────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));
        panel.setBackground(new Color(245, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title for the form
        JLabel heading = new JLabel("➕ Add New Book");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(heading, gbc);
        gbc.gridwidth = 1;

        // Row 1: Title | Author | Quantity | Add button
        gbc.gridy = 1;
        gbc.gridx = 0; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; panel.add(titleField, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 3; panel.add(authorField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; panel.add(quantityField, gbc);

        JButton addBtn = new JButton("Add Book");
        styleButton(addBtn, new Color(34, 139, 34));
        addBtn.addActionListener(this::handleAddBook);
        gbc.gridx = 2; gbc.gridwidth = 2; panel.add(addBtn, gbc);

        // Row 3: Search bar
        gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.gridx = 0; panel.add(new JLabel("🔍 Search:"), gbc);
        gbc.gridx = 1; panel.add(searchField, gbc);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, new Color(70, 130, 180));
        searchBtn.addActionListener(e -> handleSearch());
        gbc.gridx = 2; panel.add(searchBtn, gbc);

        JButton refreshBtn = new JButton("Show All");
        styleButton(refreshBtn, new Color(120, 120, 120));
        refreshBtn.addActionListener(e -> loadBooks());
        gbc.gridx = 3; panel.add(refreshBtn, gbc);

        return panel;
    }

    // ─── Center panel: Books table ────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));

        String[] columns = {"ID", "Title", "Author", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(24);
        bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Fix column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        return panel;
    }

    // ─── Action: Add book ─────────────────────────────────────────────────────
    private void handleAddBook(ActionEvent e) {
        String title  = titleField.getText().trim();
        String author = authorField.getText().trim();
        String qtyStr = quantityField.getText().trim();

        // Basic validation
        if (title.isEmpty() || author.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Quantity must be a positive number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Book book = new Book(title, author, quantity);
        boolean success = bookDAO.addBook(book);

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add book. Check DB connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── Action: Search books ─────────────────────────────────────────────────
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        List<Book> results = bookDAO.searchBooks(keyword);
        populateTable(results);
    }

    // ─── Load all books into table ────────────────────────────────────────────
    private void loadBooks() {
        List<Book> books = bookDAO.getAllBooks();
        populateTable(books);
    }

    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0); // clear existing rows
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getQuantity()});
        }
    }

    // ─── Clear the input form ─────────────────────────────────────────────────
    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        quantityField.setText("");
    }

    // ─── Utility: Style a button ──────────────────────────────────────────────
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }
}

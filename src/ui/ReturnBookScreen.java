package ui;

import dao.TransactionDAO;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Screen to return an issued book.
 * Shows all active (un-returned) transactions in a table.
 * User selects a row and clicks Return.
 */
public class ReturnBookScreen extends JFrame {

    private TransactionDAO transactionDAO = new TransactionDAO();

    private DefaultTableModel tableModel;
    private JTable transactionTable;

    public ReturnBookScreen() {
        setTitle("📥 Return Book");
        setSize(720, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadActiveTransactions();
        setVisible(true);
    }

    // ─── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(240, 255, 240));
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel label = new JLabel("📥 Select a transaction to return the book");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label);
        return panel;
    }

    // ─── Table of active transactions ─────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));

        String[] columns = {"Trans. ID", "Book Title", "Issued To", "Issue Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(24);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        return panel;
    }

    // ─── Bottom: Return button ────────────────────────────────────────────────
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JButton returnBtn = new JButton("  ✅ Return Selected Book  ");
        returnBtn.setBackground(new Color(70, 130, 50));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setFocusPainted(false);
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        returnBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        returnBtn.addActionListener(e -> handleReturn());
        panel.add(returnBtn);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> loadActiveTransactions());
        panel.add(refreshBtn);

        return panel;
    }

    // ─── Load active transactions into table ──────────────────────────────────
    private void loadActiveTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> list = transactionDAO.getActiveTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), t.getBookTitle(), t.getUserName(), t.getIssueDate()
            });
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "ℹ️ No books are currently issued out.",
                "Nothing to Return", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Handle return ────────────────────────────────────────────────────────
    private void handleReturn() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Please select a transaction from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookTitle  = (String) tableModel.getValueAt(selectedRow, 1);
        String userName   = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Return \"" + bookTitle + "\" from " + userName + "?",
            "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = transactionDAO.returnBook(transactionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadActiveTransactions(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to return book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Screen to manage library users (members).
 * Allows adding new users and viewing all users.
 */
public class UserScreen extends JFrame {

    private UserDAO userDAO = new UserDAO();

    private JTextField nameField = new JTextField(20);
    private DefaultTableModel tableModel;

    public UserScreen() {
        setTitle("👤 Users Management");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadUsers();
        setVisible(true);
    }

    // ─── Add user form ────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(248, 245, 255));
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel heading = new JLabel("➕ Add New User: ");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(heading);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        JButton addBtn = new JButton("Add User");
        addBtn.setBackground(new Color(100, 60, 180));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        addBtn.addActionListener(e -> handleAddUser());
        panel.add(addBtn);

        return panel;
    }

    // ─── Users table ──────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));

        String[] columns = {"ID", "Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ─── Load all users ───────────────────────────────────────────────────────
    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getName()});
        }
    }

    // ─── Handle add user ──────────────────────────────────────────────────────
    private void handleAddUser() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(name);
        boolean success = userDAO.addUser(user);

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

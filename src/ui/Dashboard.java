package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Dashboard window of the Library Management System.
 * Provides navigation buttons to all major screens.
 */
public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("📚 Library Management System");
        setSize(500, 420);
        setLocationRelativeTo(null);                  // center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ── Main container ──────────────────────────────────────────────────
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(28, 40, 65));
        setContentPane(mainPanel);

        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(20, 30, 55));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📚 Library Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage Books · Users · Transactions", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(160, 180, 220));

        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ── Button grid ──────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(new Color(28, 40, 65));
        buttonPanel.setBorder(new EmptyBorder(25, 40, 15, 40));

        JButton addViewBooksBtn = createDashButton("📖 Books",
            "Add & View Books", new Color(34, 120, 200));
        JButton issueBooksBtn   = createDashButton("📤 Issue Book",
            "Issue to a user",  new Color(200, 100, 30));
        JButton returnBooksBtn  = createDashButton("📥 Return Book",
            "Return a book",    new Color(50, 150, 80));
        JButton usersBtn        = createDashButton("👤 Users",
            "Manage Members",   new Color(130, 60, 190));

        addViewBooksBtn.addActionListener(e -> new BookScreen());
        issueBooksBtn.addActionListener(e -> new IssueBookScreen());
        returnBooksBtn.addActionListener(e -> new ReturnBookScreen());
        usersBtn.addActionListener(e -> new UserScreen());

        buttonPanel.add(addViewBooksBtn);
        buttonPanel.add(issueBooksBtn);
        buttonPanel.add(returnBooksBtn);
        buttonPanel.add(usersBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────────────────────
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(20, 30, 55));
        footerPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel footer = new JLabel("Library Management System  |  Java Swing + MySQL");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(120, 140, 180));
        footerPanel.add(footer);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Creates a styled dashboard button with icon text, subtitle, and color.
     */
    private JButton createDashButton(String icon, String sub, Color bg) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        iconLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(sub, SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(220, 230, 255));

        btn.add(iconLabel, BorderLayout.CENTER);
        btn.add(subLabel, BorderLayout.SOUTH);

        // Hover effect
        Color hoverColor = bg.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hoverColor); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });

        return btn;
    }
}

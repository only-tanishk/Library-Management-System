package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginScreen {

    private JTextField     uField = new JTextField();
    private JPasswordField pField = new JPasswordField();

    public LoginScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildLeft(),  BorderLayout.WEST);
        root.add(buildRight(), BorderLayout.CENTER);
        AppFrame.get().setTitle("Library Management System — Login");
        AppFrame.get().navigateRoot(root);
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, new Color(10, 16, 38), 0, getHeight(), new Color(30, 20, 70)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setPreferredSize(new Dimension(360, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.insets = new Insets(10, 24, 10, 24); g.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel("📚", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64)); p.add(icon, g);

        JLabel title = new JLabel("Library MS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30)); title.setForeground(Color.WHITE); p.add(title, g);

        JLabel sub = new JLabel("<html><center>Smart Book Management<br>for Modern Libraries</center></html>", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); sub.setForeground(new Color(170, 185, 220)); p.add(sub, g);

        for (String s : new String[]{"📖  Browse & Issue Books","🔔  Fine Tracking System","👥  Admin + Student Roles"})
            p.add(badge(s), g);
        return p;
    }

    private JPanel badge(String text) {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        b.setBackground(new Color(255,255,255,18));
        b.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,40)));
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.PLAIN, 13)); l.setForeground(new Color(200,215,255));
        b.add(l); return b;
    }

    private JPanel buildRight() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_SURFACE);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_CLR, 1), new EmptyBorder(44, 50, 44, 50)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel head = new JLabel("Welcome Back 👋"); head.setFont(UITheme.H2()); head.setForeground(UITheme.TEXT_PRI);
        gc.gridy = 0; gc.insets = new Insets(0,0,4,0); card.add(head, gc);

        JLabel sub = new JLabel("Sign in to your account"); sub.setFont(UITheme.BODY()); sub.setForeground(UITheme.TEXT_SEC);
        gc.gridy = 1; gc.insets = new Insets(0,0,30,0); card.add(sub, gc);

        gc.gridy = 2; gc.insets = new Insets(0,0,6,0); card.add(UITheme.label("USERNAME"), gc);
        gc.gridy = 3; gc.insets = new Insets(0,0,18,0);
        uField.setPreferredSize(new Dimension(320, 44)); UITheme.styleField(uField); card.add(uField, gc);

        gc.gridy = 4; gc.insets = new Insets(0,0,6,0); card.add(UITheme.label("PASSWORD"), gc);
        gc.gridy = 5; gc.insets = new Insets(0,0,28,0);
        pField.setPreferredSize(new Dimension(320, 44)); UITheme.styleField(pField); card.add(pField, gc);

        UITheme.StyledButton loginBtn = UITheme.primaryBtn("Login  →");
        loginBtn.setPreferredSize(new Dimension(320, 46));
        loginBtn.addActionListener(e -> handleLogin());
        gc.gridy = 6; gc.insets = new Insets(0,0,18,0); card.add(loginBtn, gc);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        row.setBackground(UITheme.BG_CARD);
        JLabel q = new JLabel("New student?"); q.setFont(UITheme.BODY()); q.setForeground(UITheme.TEXT_SEC);
        JButton link = new JButton("Create account");
        link.setFont(new Font("Segoe UI", Font.BOLD, 13)); link.setForeground(UITheme.CYAN);
        link.setBorderPainted(false); link.setContentAreaFilled(false); link.setFocusPainted(false);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addActionListener(e -> new SignupScreen());
        row.add(q); row.add(link);
        gc.gridy = 7; gc.insets = new Insets(0,0,0,0); card.add(row, gc);

        outer.add(card);
        return outer;
    }

    private void handleLogin() {
        String u = uField.getText().trim(), p = new String(pField.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(AppFrame.get(), "Please enter username and password.", "Missing Fields", JOptionPane.WARNING_MESSAGE); return;
        }
        User user = new UserDAO().login(u, p);
        if (user == null) {
            JOptionPane.showMessageDialog(AppFrame.get(), "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            pField.setText(""); return;
        }
        if (user.isAdmin()) new AdminDashboard(user); else new StudentDashboard(user);
    }
}

package ui;

import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard {

    private final User admin;

    public AdminDashboard(User admin) {
        this.admin = admin;
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildGrid(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        AppFrame.get().setTitle("Admin Dashboard — Library MS");
        AppFrame.get().navigateRoot(root); // clear history — can't go back past dashboard
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false); p.setBorder(new EmptyBorder(28, 36, 10, 36));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(UITheme.H1()); title.setForeground(UITheme.TEXT_PRI);
        JLabel sub = new JLabel("Welcome back, " + admin.getName() + "  •  Administrator");
        sub.setFont(UITheme.BODY()); sub.setForeground(UITheme.TEXT_SEC);

        JPanel left = new JPanel(new GridLayout(2,1,0,4));
        left.setOpaque(false); left.add(title); left.add(sub);
        p.add(left, BorderLayout.WEST);

        JPanel bar = UITheme.accentBar(UITheme.INDIGO); bar.setPreferredSize(new Dimension(0,3));
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setOpaque(false);
        wrap.add(p, BorderLayout.CENTER); wrap.add(bar, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildGrid() {
        JPanel p = new JPanel(new GridLayout(2, 2, 16, 16));
        p.setOpaque(false); p.setBorder(new EmptyBorder(20, 36, 20, 36));
        p.add(tile("📖", "Manage Books",       "Add · Edit · Delete books",     UITheme.INDIGO,  () -> new ManageBooksScreen()));
        p.add(tile("📤", "Issue Book",          "Issue a book to a student",      UITheme.ORANGE,  () -> new IssueBookAdminScreen()));
        p.add(tile("📋", "Transaction History", "Fines · Returns · Records",      UITheme.VIOLET,  () -> new TransactionHistoryScreen()));
        p.add(tile("👥", "Active Students",     "View registered members",        UITheme.TEAL,    () -> new ActiveStudentsScreen()));
        return p;
    }

    private JPanel tile(String icon, String title, String sub, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, UITheme.BG_CARD, getWidth(), getHeight(), new Color(16, 24, 52)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent); g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
            }
        };
        card.setOpaque(false); card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),70), 1),
            new EmptyBorder(22, 24, 22, 24)));

        JLabel iconLbl = new JLabel(icon + "  " + title);
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 18)); iconLbl.setForeground(Color.WHITE);
        JLabel subLbl  = new JLabel(sub);
        subLbl.setFont(UITheme.BODY()); subLbl.setForeground(UITheme.TEXT_SEC);
        JLabel arrow   = new JLabel("→"); arrow.setFont(UITheme.H3()); arrow.setForeground(accent); arrow.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(iconLbl, BorderLayout.NORTH); card.add(subLbl, BorderLayout.CENTER); card.add(arrow, BorderLayout.SOUTH);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
            public void mouseEntered(java.awt.event.MouseEvent e) { card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(accent,2),new EmptyBorder(22,24,22,24))); card.repaint(); }
            public void mouseExited(java.awt.event.MouseEvent e)  { card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),70),1),new EmptyBorder(22,24,22,24))); card.repaint(); }
        });
        return card;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        p.setOpaque(false); p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_CLR));
        JLabel info = new JLabel("Library Management System  •  Admin Console");
        info.setFont(UITheme.SMALL()); info.setForeground(UITheme.TEXT_MUTED);
        JButton logout = new JButton("Logout");
        logout.setFont(new Font("Segoe UI",Font.BOLD,13)); logout.setForeground(UITheme.ROSE);
        logout.setBorderPainted(false); logout.setContentAreaFilled(false); logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> new LoginScreen());
        p.add(info); p.add(logout); return p;
    }
}

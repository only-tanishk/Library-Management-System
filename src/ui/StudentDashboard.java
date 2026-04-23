package ui;

import dao.TransactionDAO;
import model.Transaction;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentDashboard {

    private final User student;
    private final TransactionDAO dao = new TransactionDAO();

    private JLabel issuedVal  = new JLabel("—");
    private JLabel pendingVal = new JLabel("—");
    private JLabel paidVal    = new JLabel("—");

    public StudentDashboard(User student) {
        this.student = student;
        JPanel root = buildPanel();
        AppFrame.get().setTitle("Student Portal — Library MS");
        AppFrame.get().navigateRoot(root);
        refreshStats();
    }

    private JPanel buildPanel() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false); p.setBorder(new EmptyBorder(26, 36, 10, 36));

        JLabel title = new JLabel("Student Portal");
        title.setFont(UITheme.H1()); title.setForeground(UITheme.TEXT_PRI);
        JLabel sub = new JLabel("Hi, " + student.getName() + "  •  " + student.getUsername());
        sub.setFont(UITheme.BODY()); sub.setForeground(UITheme.TEXT_SEC);

        JPanel left = new JPanel(new GridLayout(2,1,0,4));
        left.setOpaque(false); left.add(title); left.add(sub);
        p.add(left, BorderLayout.WEST);

        JPanel bar = UITheme.accentBar(UITheme.EMERALD); bar.setPreferredSize(new Dimension(0,3));
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setOpaque(false);
        wrap.add(p, BorderLayout.CENTER); wrap.add(bar, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false); p.setBorder(new EmptyBorder(16, 36, 10, 36));

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.add(UITheme.statCard("📖  Books Issued",  issuedVal,  UITheme.INDIGO));
        stats.add(UITheme.statCard("⚠️  Pending Fine",  pendingVal, UITheme.ROSE));
        stats.add(UITheme.statCard("✅  Total Paid",     paidVal,    UITheme.EMERALD));
        p.add(stats, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        grid.add(navTile("📚","Browse Books",    "See all available books",    UITheme.INDIGO,  () -> new AvailableBooksScreen()));
        grid.add(navTile("📤","Issue a Book",    "Enter book code to issue",   UITheme.ORANGE,  () -> new SelfIssueScreen(student)));
        grid.add(navTile("📋","My Transactions", "History, fines & returns",   UITheme.VIOLET,  () -> new MyTransactionsScreen(student)));
        grid.add(navTile("🔄","Refresh Stats",   "Update your summary",        UITheme.TEAL,    () -> refreshStats()));
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel navTile(String ico, String label, String sub, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0,6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,UITheme.BG_CARD,getWidth(),getHeight(),new Color(14,22,44)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(accent); g2.fillRoundRect(0,0,getWidth(),4,4,4);
            }
        };
        card.setOpaque(false); card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),70),1),
            new EmptyBorder(18,22,18,22)));
        JLabel lbl = new JLabel(ico + "  " + label);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,16)); lbl.setForeground(Color.WHITE);
        JLabel sl  = new JLabel(sub); sl.setFont(UITheme.SMALL()); sl.setForeground(UITheme.TEXT_SEC);
        card.add(lbl, BorderLayout.CENTER); card.add(sl, BorderLayout.SOUTH);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
            public void mouseEntered(java.awt.event.MouseEvent e) { card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(accent,2),new EmptyBorder(18,22,18,22))); card.repaint(); }
            public void mouseExited(java.awt.event.MouseEvent e)  { card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),70),1),new EmptyBorder(18,22,18,22))); card.repaint(); }
        });
        return card;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        p.setOpaque(false); p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_CLR));
        JLabel info = new JLabel("Issue limit: 2 books  •  Fine: ₹50/day after 15 days");
        info.setFont(UITheme.SMALL()); info.setForeground(UITheme.TEXT_MUTED);
        JButton out = new JButton("Logout"); out.setFont(new Font("Segoe UI",Font.BOLD,13)); out.setForeground(UITheme.ROSE);
        out.setBorderPainted(false); out.setContentAreaFilled(false); out.setFocusPainted(false);
        out.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        out.addActionListener(e -> new LoginScreen());
        p.add(info); p.add(out); return p;
    }

    public void refreshStats() {
        int    active = dao.getActiveBookCount(student.getId());
        double paid   = dao.getTotalFinePaid(student.getId());
        double pend   = dao.getTransactionsByUser(student.getId()).stream()
            .filter(t -> t.getReturnDate() == null)
            .mapToDouble(t -> Transaction.calculatePendingFine(t.getIssueDate())).sum();
        issuedVal.setText(active + " / 2");
        pendingVal.setText("₹" + String.format("%.0f", pend));
        paidVal.setText("₹"   + String.format("%.0f", paid));
        pendingVal.setForeground(pend > 0 ? UITheme.ROSE : UITheme.EMERALD);
        issuedVal.setForeground(active >= 2 ? UITheme.AMBER : UITheme.TEXT_PRI);
    }
}

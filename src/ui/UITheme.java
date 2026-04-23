package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Central design system for the LMS.
 * All colors, fonts, and reusable components live here.
 */
public class UITheme {

    // ── Color Palette ──────────────────────────────────────────────────────────
    public static final Color BG_DARK    = new Color(10, 16, 32);
    public static final Color BG_SURFACE = new Color(18, 27, 50);
    public static final Color BG_CARD    = new Color(28, 40, 68);
    public static final Color BG_INPUT   = new Color(20, 32, 58);
    public static final Color BORDER_CLR = new Color(45, 62, 100);

    public static final Color INDIGO  = new Color(99, 102, 241);
    public static final Color CYAN    = new Color(34, 211, 238);
    public static final Color EMERALD = new Color(16, 185, 129);
    public static final Color AMBER   = new Color(245, 158, 11);
    public static final Color ROSE    = new Color(244, 63, 94);
    public static final Color VIOLET  = new Color(167, 139, 250);
    public static final Color ORANGE  = new Color(251, 146, 60);
    public static final Color TEAL    = new Color(20, 184, 166);

    public static final Color TEXT_PRI   = new Color(241, 245, 249);
    public static final Color TEXT_SEC   = new Color(148, 163, 184);
    public static final Color TEXT_MUTED = new Color(90, 110, 150);

    public static final Color ROW_EVEN = new Color(18, 27, 50);
    public static final Color ROW_ODD  = new Color(24, 36, 62);
    public static final Color ROW_SEL  = new Color(55, 68, 120);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public static Font H1()    { return new Font("Segoe UI", Font.BOLD,  26); }
    public static Font H2()    { return new Font("Segoe UI", Font.BOLD,  19); }
    public static Font H3()    { return new Font("Segoe UI", Font.BOLD,  15); }
    public static Font BODY()  { return new Font("Segoe UI", Font.PLAIN, 13); }
    public static Font BOLD()  { return new Font("Segoe UI", Font.BOLD,  13); }
    public static Font SMALL() { return new Font("Segoe UI", Font.PLAIN, 11); }
    public static Font LABEL() { return new Font("Segoe UI", Font.BOLD,  12); }

    // ── Background gradient panel ──────────────────────────────────────────────
    public static JPanel gradientPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(16, 24, 52));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    // ── Panel factories ────────────────────────────────────────────────────────
    public static JPanel surfacePanel(LayoutManager lm) {
        JPanel p = new JPanel(lm); p.setBackground(BG_SURFACE); return p;
    }
    public static JPanel cardPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        return p;
    }

    // ── Text field styling ─────────────────────────────────────────────────────
    public static void styleField(JTextField f) {
        f.setBackground(BG_INPUT); f.setForeground(TEXT_PRI); f.setCaretColor(CYAN);
        f.setFont(BODY());
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)));
    }
    public static void styleField(JPasswordField f) {
        f.setBackground(BG_INPUT); f.setForeground(TEXT_PRI); f.setCaretColor(CYAN);
        f.setFont(BODY());
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)));
    }
    public static void styleCombo(JComboBox<?> c) {
        c.setBackground(BG_INPUT); c.setForeground(TEXT_PRI); c.setFont(BODY());
        c.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
    }

    // ── Labels ─────────────────────────────────────────────────────────────────
    public static JLabel h1(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER); l.setFont(H1()); l.setForeground(TEXT_PRI); return l;
    }
    public static JLabel h2(String t) {
        JLabel l = new JLabel(t); l.setFont(H2()); l.setForeground(TEXT_PRI); return l;
    }
    public static JLabel secondary(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER); l.setFont(BODY()); l.setForeground(TEXT_SEC); return l;
    }
    public static JLabel label(String t) {
        JLabel l = new JLabel(t); l.setFont(LABEL()); l.setForeground(TEXT_SEC); return l;
    }
    public static JLabel muted(String t) {
        JLabel l = new JLabel(t); l.setFont(SMALL()); l.setForeground(TEXT_MUTED); return l;
    }

    // ── Buttons ────────────────────────────────────────────────────────────────
    public static StyledButton primaryBtn(String t)  { return new StyledButton(t, INDIGO);  }
    public static StyledButton successBtn(String t)  { return new StyledButton(t, EMERALD); }
    public static StyledButton dangerBtn(String t)   { return new StyledButton(t, ROSE);    }
    public static StyledButton warningBtn(String t)  { return new StyledButton(t, AMBER);   }
    public static StyledButton neutralBtn(String t)  { return new StyledButton(t, new Color(55, 75, 115)); }
    public static StyledButton orangeBtn(String t)   { return new StyledButton(t, ORANGE);  }
    public static StyledButton tealBtn(String t)     { return new StyledButton(t, TEAL);    }
    public static StyledButton violetBtn(String t)   { return new StyledButton(t, VIOLET);  }

    // ── Table styling ──────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(ROW_EVEN); table.setForeground(TEXT_PRI);
        table.setGridColor(new Color(30, 44, 76));
        table.setRowHeight(30); table.setFont(BODY());
        table.setSelectionBackground(ROW_SEL); table.setSelectionForeground(TEXT_PRI);
        table.setShowHorizontalLines(true); table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader h = table.getTableHeader();
        h.setBackground(new Color(14, 22, 44)); h.setForeground(CYAN);
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setPreferredSize(new Dimension(h.getWidth(), 38));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INDIGO));
        h.setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new AltRowRenderer());
    }

    public static JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(ROW_EVEN);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        sp.setBackground(BG_SURFACE);
        return sp;
    }

    // ── Stat card ──────────────────────────────────────────────────────────────
    public static JPanel statCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(accent);
                g.fillRect(0, 0, 4, getHeight());
            }
        };
        card.setLayout(new GridLayout(2, 1, 0, 4));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 14)));
        JLabel t = new JLabel(title); t.setFont(LABEL()); t.setForeground(TEXT_SEC);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); valueLabel.setForeground(Color.WHITE);
        card.add(t); card.add(valueLabel); return card;
    }

    // ── Separator ─────────────────────────────────────────────────────────────
    public static JSeparator darkSep() {
        JSeparator s = new JSeparator(); s.setForeground(BORDER_CLR); s.setBackground(BG_DARK); return s;
    }

    // ── Accent bar (thin colored horizontal line) ──────────────────────────────
    public static JPanel accentBar(Color c) {
        JPanel p = new JPanel(); p.setBackground(c);
        p.setPreferredSize(new Dimension(Integer.MAX_VALUE, 3)); return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner: Rounded button
    // ─────────────────────────────────────────────────────────────────────────
    public static class StyledButton extends JButton {
        private final Color base;
        private Color cur;

        public StyledButton(String text, Color base) {
            super(text); this.base = base; this.cur = base;
            setContentAreaFilled(false); setFocusPainted(false);
            setForeground(Color.WHITE); setFont(BOLD());
            setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); setOpaque(false);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { cur = base.brighter(); repaint(); }
                public void mouseExited(MouseEvent e)  { cur = base; repaint(); }
                public void mousePressed(MouseEvent e) { cur = base.darker();   repaint(); }
                public void mouseReleased(MouseEvent e){ cur = base.brighter(); repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cur); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose(); super.paintComponent(g);
        }
        @Override protected void paintBorder(Graphics g) { /* none */ }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner: Alternating row renderer
    // ─────────────────────────────────────────────────────────────────────────
    public static class AltRowRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable table, Object value, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(table, value, sel, foc, row, col);
            if (!sel) {
                setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                setForeground(TEXT_PRI);
            } else {
                setBackground(ROW_SEL); setForeground(TEXT_PRI);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner: Status badge renderer (for Status columns in tables)
    // ─────────────────────────────────────────────────────────────────────────
    public static class StatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable table, Object value, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(table, value, sel, foc, row, col);
            String s = value == null ? "" : value.toString();
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(BOLD());
            if (!sel) {
                if ("Overdue".equals(s))       { setBackground(new Color(60, 20, 20)); setForeground(ROSE); }
                else if ("Returned".equals(s)) { setBackground(new Color(10, 45, 30)); setForeground(EMERALD); }
                else if ("Active".equals(s))   { setBackground(new Color(20, 40, 80)); setForeground(CYAN); }
                else                           { setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD); setForeground(TEXT_PRI); }
            }
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    // ── Toolbar panel ─────────────────────────────────────────────────────────
    public static JPanel toolbarPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setBackground(BG_SURFACE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        return p;
    }
}

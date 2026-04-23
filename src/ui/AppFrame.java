package ui;

import javax.swing.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Single application window — all screens share this JFrame.
 * Uses a navigation stack: navigate() pushes, goBack() pops.
 */
public class AppFrame extends JFrame {

    private static AppFrame instance;
    private final Deque<JPanel> history = new ArrayDeque<>();

    private AppFrame() {
        setTitle("Library Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // full screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(900, 600));
    }

    /** Get (or create) the singleton window. */
    public static AppFrame get() {
        if (instance == null) instance = new AppFrame();
        return instance;
    }

    /** Push a new panel onto the history stack and display it. */
    public void navigate(JPanel panel) {
        history.push(panel);
        setContentPane(panel);
        revalidate(); repaint();
        if (!isVisible()) setVisible(true);
    }

    /**
     * Navigate to a root panel (clears history first).
     * Used for Login and Dashboard screens so the user cannot
     * press Back into a previous session.
     */
    public void navigateRoot(JPanel panel) {
        history.clear();
        navigate(panel);
    }

    /** Pop the current panel and show the previous one. */
    public void goBack() {
        if (history.size() > 1) {
            history.pop();
            setContentPane(history.peek());
            revalidate(); repaint();
        }
    }

    /** True if there is a previous panel to go back to. */
    public boolean canGoBack() {
        return history.size() > 1;
    }
}

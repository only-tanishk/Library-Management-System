package main;

import ui.AppFrame;
import ui.LoginScreen;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { /* use default */ }

        SwingUtilities.invokeLater(() -> {
            AppFrame.get(); // create the window
            new LoginScreen();
        });
    }
}

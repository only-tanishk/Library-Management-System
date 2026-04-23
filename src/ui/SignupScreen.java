package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignupScreen {

    private JTextField     nameF = new JTextField();
    private JTextField     userF = new JTextField();
    private JPasswordField passF = new JPasswordField();
    private JPasswordField confF = new JPasswordField();

    public SignupScreen() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_CLR, 1), new EmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel icon = new JLabel("📝", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        gc.gridy = 0; gc.insets = new Insets(0,0,6,0); card.add(icon, gc);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(UITheme.H2()); title.setForeground(UITheme.TEXT_PRI);
        gc.gridy = 1; gc.insets = new Insets(0,0,4,0); card.add(title, gc);

        JLabel sub = new JLabel("Student library registration", SwingConstants.CENTER);
        sub.setFont(UITheme.BODY()); sub.setForeground(UITheme.TEXT_SEC);
        gc.gridy = 2; gc.insets = new Insets(0,0,26,0); card.add(sub, gc);

        Dimension fd = new Dimension(320, 44);
        addField(card, gc, 3,  "FULL NAME",         nameF, fd);
        addField(card, gc, 5,  "USERNAME",           userF, fd);
        addField(card, gc, 7,  "PASSWORD",           passF, fd);
        addField(card, gc, 9,  "CONFIRM PASSWORD",   confF, fd);

        UITheme.StyledButton createBtn = UITheme.successBtn("Create Account");
        createBtn.setPreferredSize(new Dimension(320, 46));
        createBtn.addActionListener(e -> handleSignup());
        gc.gridy = 11; gc.insets = new Insets(6,0,16,0); card.add(createBtn, gc);

        JButton back = new JButton("← Back to Login");
        back.setFont(new Font("Segoe UI", Font.BOLD, 13)); back.setForeground(UITheme.INDIGO);
        back.setBorderPainted(false); back.setContentAreaFilled(false); back.setFocusPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> AppFrame.get().goBack());
        gc.gridy = 12; gc.insets = new Insets(0,0,0,0); card.add(back, gc);

        root.add(card);
        AppFrame.get().setTitle("Create Account — Library MS");
        AppFrame.get().navigate(root);
    }

    private void addField(JPanel p, GridBagConstraints gc, int row, String label, JComponent field, Dimension d) {
        gc.gridy = row; gc.insets = new Insets(0,0,6,0); p.add(UITheme.label(label), gc);
        gc.gridy = row + 1; gc.insets = new Insets(0,0,16,0); field.setPreferredSize(d);
        if (field instanceof JTextField)     UITheme.styleField((JTextField) field);
        if (field instanceof JPasswordField) UITheme.styleField((JPasswordField) field);
        p.add(field, gc);
    }

    private void handleSignup() {
        String name = nameF.getText().trim(), user = userF.getText().trim();
        String pass = new String(passF.getPassword()).trim(), conf = new String(confF.getPassword()).trim();
        if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(AppFrame.get(), "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(AppFrame.get(), "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        if (user.length() < 3) {
            JOptionPane.showMessageDialog(AppFrame.get(), "Username must be at least 3 characters.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        UserDAO dao = new UserDAO();
        if (dao.usernameExists(user)) {
            JOptionPane.showMessageDialog(AppFrame.get(), "Username '" + user + "' is taken.", "Taken", JOptionPane.ERROR_MESSAGE); return;
        }
        if (dao.signup(new User(name, user, pass))) {
            JOptionPane.showMessageDialog(AppFrame.get(), "✅ Account created! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            AppFrame.get().goBack(); // go back to login
        } else {
            JOptionPane.showMessageDialog(AppFrame.get(), "Signup failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

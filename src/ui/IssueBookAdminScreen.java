package ui;

import dao.BookDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import model.Book;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class IssueBookAdminScreen {

    private final BookDAO        bookDAO = new BookDAO();
    private final UserDAO        userDAO = new UserDAO();
    private final TransactionDAO dao     = new TransactionDAO();
    private JComboBox<Book> bookCombo;
    private JComboBox<User> userCombo;

    public IssueBookAdminScreen() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout());
        root.add(buildToolbar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(buildCard());
        root.add(center, BorderLayout.CENTER);

        loadDropdowns();
        AppFrame.get().setTitle("Issue Book — Admin");
        AppFrame.get().navigate(root);
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack()); p.add(back);
        p.add(Box.createHorizontalStrut(12));
        JLabel h = UITheme.h2("📤  Issue Book to Student"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        return p;
    }

    private JPanel buildCard() {
        JPanel card = UITheme.cardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_CLR,1), new EmptyBorder(36,46,36,46)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL;

        gc.gridy=0; gc.insets=new Insets(0,0,6,0); card.add(UITheme.label("SELECT BOOK"), gc);
        gc.gridy=1; gc.insets=new Insets(0,0,18,0);
        bookCombo=new JComboBox<>(); bookCombo.setPreferredSize(new Dimension(380,38));
        UITheme.styleCombo(bookCombo); card.add(bookCombo, gc);

        gc.gridy=2; gc.insets=new Insets(0,0,6,0); card.add(UITheme.label("SELECT STUDENT"), gc);
        gc.gridy=3; gc.insets=new Insets(0,0,28,0);
        userCombo=new JComboBox<>(); userCombo.setPreferredSize(new Dimension(380,38));
        UITheme.styleCombo(userCombo); card.add(userCombo, gc);

        UITheme.StyledButton btn = UITheme.orangeBtn("📤  Issue Book");
        btn.setPreferredSize(new Dimension(380,46));
        btn.addActionListener(e -> handleIssue());
        gc.gridy=4; gc.insets=new Insets(0,0,0,0); card.add(btn, gc);

        return card;
    }

    private void loadDropdowns() {
        bookCombo.removeAllItems(); bookDAO.getAllBooks().forEach(bookCombo::addItem);
        userCombo.removeAllItems(); userDAO.getAllUsers().forEach(userCombo::addItem);
    }

    private void handleIssue() {
        Book b=(Book)bookCombo.getSelectedItem(); User u=(User)userCombo.getSelectedItem();
        if(b==null||u==null){JOptionPane.showMessageDialog(AppFrame.get(),"Select a book and student."); return;}
        if(JOptionPane.showConfirmDialog(AppFrame.get(),"Issue \""+b.getTitle()+"\" to "+u.getName()+"?",
            "Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            String err=dao.issueBook(b.getId(),u.getId());
            if(err==null){JOptionPane.showMessageDialog(AppFrame.get(),"✅ Book issued!"); loadDropdowns();}
            else JOptionPane.showMessageDialog(AppFrame.get(),"❌ "+err,"Cannot Issue",JOptionPane.ERROR_MESSAGE);
        }
    }
}

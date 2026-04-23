package ui;

import dao.BookDAO;
import dao.TransactionDAO;
import model.Book;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SelfIssueScreen {

    private final BookDAO        bookDAO = new BookDAO();
    private final TransactionDAO dao     = new TransactionDAO();
    private final User student;

    private JTextField codeField   = new JTextField();
    private JLabel     resultLabel = new JLabel("Enter a book code and click Find.");
    private Book       found       = null;

    public SelfIssueScreen(User student) {
        this.student = student;
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout());
        root.add(buildToolbar(), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridBagLayout()); center.setOpaque(false);
        center.add(buildCard());
        root.add(center, BorderLayout.CENTER);
        AppFrame.get().setTitle("Issue a Book — Library MS");
        AppFrame.get().navigate(root);
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack()); p.add(back);
        p.add(Box.createHorizontalStrut(12));
        JLabel h = UITheme.h2("📤  Self Issue by Book Code"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        return p;
    }

    private JPanel buildCard() {
        JPanel card = UITheme.cardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_CLR,1), new EmptyBorder(36,50,36,50)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL;

        // Limit display
        int active=dao.getActiveBookCount(student.getId()), left=2-active;
        JLabel limitLbl = new JLabel("Your limit: "+active+" / 2 books issued  •  "+left+" slot(s) free");
        limitLbl.setFont(UITheme.BODY()); limitLbl.setForeground(left==0?UITheme.ROSE:UITheme.EMERALD);
        gc.gridy=0; gc.insets=new Insets(0,0,22,0); card.add(limitLbl, gc);

        gc.gridy=1; gc.insets=new Insets(0,0,6,0); card.add(UITheme.label("BOOK CODE"), gc);

        JPanel row = new JPanel(new BorderLayout(10,0)); row.setOpaque(false);
        codeField.setPreferredSize(new Dimension(240,42)); UITheme.styleField(codeField);
        UITheme.StyledButton findBtn = UITheme.primaryBtn("Find →");
        findBtn.addActionListener(e -> handleSearch());
        row.add(codeField, BorderLayout.CENTER); row.add(findBtn, BorderLayout.EAST);
        gc.gridy=2; gc.insets=new Insets(0,0,16,0); card.add(row, gc);

        // Result display
        JPanel resultCard = UITheme.cardPanel(new BorderLayout());
        resultCard.setBorder(new EmptyBorder(14,16,14,16));
        resultCard.setPreferredSize(new Dimension(380, 54));
        resultLabel.setFont(UITheme.BODY()); resultLabel.setForeground(UITheme.TEXT_SEC);
        resultCard.add(resultLabel, BorderLayout.CENTER);
        gc.gridy=3; gc.insets=new Insets(0,0,24,0); card.add(resultCard, gc);

        UITheme.StyledButton issueBtn = UITheme.successBtn("✅  Confirm Issue");
        issueBtn.setPreferredSize(new Dimension(380,46));
        issueBtn.addActionListener(e -> handleIssue());
        gc.gridy=4; gc.insets=new Insets(0,0,0,0); card.add(issueBtn, gc);

        return card;
    }

    private void handleSearch() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) { resultLabel.setText("Enter a book code above."); resultLabel.setForeground(UITheme.TEXT_SEC); found=null; return; }
        found = bookDAO.getBookByCode(code);
        if (found==null) { resultLabel.setText("❌  Not found: "+code.toUpperCase()); resultLabel.setForeground(UITheme.ROSE); }
        else if (found.getQuantity()<=0) { resultLabel.setText("⚠️  Out of stock: "+found.getTitle()); resultLabel.setForeground(UITheme.AMBER); found=null; }
        else { resultLabel.setText("✅  "+found.getTitle()+"  |  "+found.getAuthor()+"  |  "+found.getQuantity()+" copies"); resultLabel.setForeground(UITheme.EMERALD); }
    }

    private void handleIssue() {
        if (found==null) { JOptionPane.showMessageDialog(AppFrame.get(),"Search for a valid book code first."); return; }
        if (JOptionPane.showConfirmDialog(AppFrame.get(),"Issue \""+found.getTitle()+"\" to yourself?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            String err = dao.issueBook(found.getId(), student.getId());
            if (err==null) { JOptionPane.showMessageDialog(AppFrame.get(),"✅ Book issued!\nReturn within 15 days to avoid ₹50/day fine.","Success",JOptionPane.INFORMATION_MESSAGE); AppFrame.get().goBack(); }
            else JOptionPane.showMessageDialog(AppFrame.get(),"❌ "+err,"Cannot Issue",JOptionPane.ERROR_MESSAGE);
        }
    }
}

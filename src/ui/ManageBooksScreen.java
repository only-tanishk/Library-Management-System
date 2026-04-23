package ui;

import dao.BookDAO;
import model.Book;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageBooksScreen {

    private final BookDAO bookDAO = new BookDAO();
    private DefaultTableModel tableModel;
    private JTable bookTable;

    public ManageBooksScreen() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(buildTable(),   BorderLayout.CENTER);
        AppFrame.get().setTitle("Manage Books — Library MS");
        AppFrame.get().navigate(root);
        loadBooks();
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack());
        p.add(back); p.add(Box.createHorizontalStrut(12));

        JLabel h = UITheme.h2("📖  Book Inventory"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        p.add(Box.createHorizontalStrut(16));

        UITheme.StyledButton addBtn  = UITheme.successBtn("➕ Add Book");
        UITheme.StyledButton editBtn = UITheme.primaryBtn("✏️ Edit");
        UITheme.StyledButton delBtn  = UITheme.dangerBtn("🗑 Delete");
        addBtn.addActionListener(e  -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        delBtn.addActionListener(e  -> handleDelete());
        p.add(addBtn); p.add(editBtn); p.add(delBtn);
        return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"ID", "Code", "Title", "Author", "Quantity"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        bookTable = new JTable(tableModel);
        UITheme.styleTable(bookTable);
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        return UITheme.styledScroll(bookTable);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        for (Book b : bookDAO.getAllBooks())
            tableModel.addRow(new Object[]{b.getId(), b.getBookCode(), b.getTitle(), b.getAuthor(), b.getQuantity()});
    }

    private void openAddDialog() {
        JTextField code = df(), title = df(), author = df(), qty = new JTextField("1", 8);
        JPanel p = formPnl(new String[]{"Book Code:", "Title:", "Author:", "Quantity:"}, new JComponent[]{code, title, author, qty});
        if (JOptionPane.showConfirmDialog(AppFrame.get(), p, "Add New Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        if (code.getText().isBlank() || title.getText().isBlank()) { JOptionPane.showMessageDialog(AppFrame.get(), "All fields required."); return; }
        try {
            if (bookDAO.addBook(new Book(code.getText().trim(), title.getText().trim(), author.getText().trim(), Integer.parseInt(qty.getText().trim()))))
                { JOptionPane.showMessageDialog(AppFrame.get(), "✅ Book added!"); loadBooks(); }
            else JOptionPane.showMessageDialog(AppFrame.get(), "❌ Failed — code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(AppFrame.get(), "Quantity must be a number."); }
    }

    private void openEditDialog() {
        int row = bookTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(AppFrame.get(), "Select a book to edit."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        JTextField code = df((String)tableModel.getValueAt(row,1)), title = df((String)tableModel.getValueAt(row,2)),
                   author = df((String)tableModel.getValueAt(row,3)), qty = df(String.valueOf(tableModel.getValueAt(row,4)));
        JPanel p = formPnl(new String[]{"Code:","Title:","Author:","Qty:"}, new JComponent[]{code,title,author,qty});
        if (JOptionPane.showConfirmDialog(AppFrame.get(), p, "Edit Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            if (bookDAO.updateBook(new Book(id, code.getText().trim(), title.getText().trim(), author.getText().trim(), Integer.parseInt(qty.getText().trim()))))
                { JOptionPane.showMessageDialog(AppFrame.get(), "✅ Updated!"); loadBooks(); }
            else JOptionPane.showMessageDialog(AppFrame.get(), "❌ Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(AppFrame.get(), "Qty must be a number."); }
    }

    private void handleDelete() {
        int row = bookTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(AppFrame.get(), "Select a book to delete."); return; }
        if (JOptionPane.showConfirmDialog(AppFrame.get(), "Delete \"" + tableModel.getValueAt(row,2) + "\"?",
            "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook((int)tableModel.getValueAt(row,0))) { JOptionPane.showMessageDialog(AppFrame.get(), "✅ Deleted."); loadBooks(); }
            else JOptionPane.showMessageDialog(AppFrame.get(), "❌ Cannot delete — active transactions exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField df()        { JTextField f = new JTextField(20); styleF(f); return f; }
    private JTextField df(String v){ JTextField f = new JTextField(v, 20); styleF(f); return f; }
    private void styleF(JTextField f) { f.setBackground(UITheme.BG_INPUT); f.setForeground(UITheme.TEXT_PRI); f.setCaretColor(UITheme.CYAN); }
    private JPanel formPnl(String[] ls, JComponent[] fs) {
        JPanel p = new JPanel(new GridLayout(ls.length, 2, 10, 10));
        p.setBackground(UITheme.BG_CARD); p.setBorder(new EmptyBorder(16, 16, 16, 16));
        for (int i = 0; i < ls.length; i++) { JLabel l = new JLabel(ls[i]); l.setForeground(UITheme.TEXT_SEC); l.setFont(UITheme.LABEL()); p.add(l); p.add(fs[i]); }
        return p;
    }
}

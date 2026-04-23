package ui;

import dao.BookDAO;
import model.Book;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AvailableBooksScreen {

    private final BookDAO bookDAO = new BookDAO();
    private DefaultTableModel tableModel;
    private JTextField searchField = new JTextField();

    public AvailableBooksScreen() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout(0,0));
        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(buildTable(),   BorderLayout.CENTER);
        AppFrame.get().setTitle("Available Books — Library MS");
        AppFrame.get().navigate(root);
        load(null);
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack()); p.add(back);
        p.add(Box.createHorizontalStrut(12));
        JLabel h = UITheme.h2("📚  Available Books"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        p.add(Box.createHorizontalStrut(16));
        UITheme.styleField(searchField); searchField.setPreferredSize(new Dimension(220, 34));
        p.add(searchField);
        UITheme.StyledButton s = UITheme.primaryBtn("🔍 Search");
        UITheme.StyledButton all = UITheme.neutralBtn("Show All");
        s.addActionListener(e -> load(searchField.getText().trim()));
        all.addActionListener(e -> { searchField.setText(""); load(null); });
        p.add(s); p.add(all); return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"Code","Title","Author","Available"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(340);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setCellRenderer(new UITheme.AltRowRenderer(){
            @Override public java.awt.Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel) setForeground(UITheme.EMERALD); setHorizontalAlignment(SwingConstants.CENTER); return this;
            }
        });
        return UITheme.styledScroll(table);
    }

    private void load(String kw) {
        tableModel.setRowCount(0);
        List<Book> books = (kw==null||kw.isEmpty()) ? bookDAO.getAvailableBooks() : bookDAO.searchBooks(kw);
        for (Book b : books)
            if (b.getQuantity()>0)
                tableModel.addRow(new Object[]{b.getBookCode(),b.getTitle(),b.getAuthor(),b.getQuantity()});
        if (tableModel.getRowCount()==0) tableModel.addRow(new Object[]{"—","No books found.","—","—"});
    }
}

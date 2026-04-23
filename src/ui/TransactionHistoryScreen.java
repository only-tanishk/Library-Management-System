package ui;

import dao.TransactionDAO;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionHistoryScreen {

    private final TransactionDAO dao = new TransactionDAO();
    private DefaultTableModel tableModel;
    private JTable table;

    public TransactionHistoryScreen() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(buildTable(),   BorderLayout.CENTER);
        root.add(buildLegend(),  BorderLayout.SOUTH);
        AppFrame.get().setTitle("Transaction History — Library MS");
        AppFrame.get().navigate(root);
        load();
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack()); p.add(back);
        p.add(Box.createHorizontalStrut(12));
        JLabel h = UITheme.h2("📋  Transaction History"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        p.add(Box.createHorizontalStrut(16));
        UITheme.StyledButton ret = UITheme.successBtn("📥 Return Book");
        UITheme.StyledButton ref = UITheme.neutralBtn("🔄 Refresh");
        ret.addActionListener(e -> handleReturn()); ref.addActionListener(e -> load());
        p.add(ret); p.add(ref); return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"ID","Code","Book Title","Student","Issue Date","Return Date","Days","Fine (₹)","Paid (₹)","Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(9).setCellRenderer(new UITheme.StatusRenderer());
        int[] w = {45,70,240,160,90,90,50,80,70,90};
        for (int i=0;i<w.length;i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        return UITheme.styledScroll(table);
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        p.setBackground(UITheme.BG_SURFACE);
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_CLR));
        p.add(dot(UITheme.ROSE));    p.add(lbl("Overdue"));
        p.add(dot(UITheme.EMERALD)); p.add(lbl("Returned"));
        p.add(dot(UITheme.CYAN));    p.add(lbl("Active"));
        p.add(Box.createHorizontalStrut(20));
        JLabel rule = new JLabel("Fine: ₹50/day after 15 days");
        rule.setFont(UITheme.SMALL()); rule.setForeground(UITheme.AMBER); p.add(rule); return p;
    }

    private JLabel dot(Color c) { JLabel l=new JLabel("●"); l.setForeground(c); l.setFont(new Font("Segoe UI",Font.BOLD,16)); return l; }
    private JLabel lbl(String t) { JLabel l=new JLabel(t); l.setFont(UITheme.SMALL()); l.setForeground(UITheme.TEXT_SEC); return l; }

    private void load() {
        tableModel.setRowCount(0);
        for (Transaction t : dao.getAllTransactions()) {
            long days = Transaction.daysElapsed(t.getIssueDate());
            double fine = t.getReturnDate()==null ? Transaction.calculatePendingFine(t.getIssueDate()) : 0;
            String st = t.getReturnDate()!=null ? "Returned" : fine>0 ? "Overdue" : "Active";
            tableModel.addRow(new Object[]{t.getId(),t.getBookCode(),t.getBookTitle(),t.getUserName(),
                t.getIssueDate(), t.getReturnDate()!=null?t.getReturnDate():"—",
                days, fine>0?String.format("%.0f",fine):"—",
                t.getFinePaid()>0?String.format("%.0f",t.getFinePaid()):"—", st});
        }
    }

    private void handleReturn() {
        int row = table.getSelectedRow();
        if (row<0) { JOptionPane.showMessageDialog(AppFrame.get(),"Select an active transaction.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        if ("Returned".equals(tableModel.getValueAt(row,9))) { JOptionPane.showMessageDialog(AppFrame.get(),"Already returned."); return; }

        int    id       = (int)    tableModel.getValueAt(row,0);
        String bookTitle= (String) tableModel.getValueAt(row,2);
        String student  = (String) tableModel.getValueAt(row,3);
        long   days     = (long)   tableModel.getValueAt(row,6);
        String fineStr  = (String) tableModel.getValueAt(row,7);
        double pendFine = "—".equals(fineStr) ? 0 : Double.parseDouble(fineStr);

        JPanel popup = new JPanel(new GridLayout(0,1,4,8));
        popup.setBackground(UITheme.BG_CARD); popup.setBorder(new EmptyBorder(16,18,16,18));
        addRow(popup,"Book",    bookTitle);
        addRow(popup,"Student", student);
        addRow(popup,"Days Held", days+" days");
        popup.add(UITheme.darkSep());

        JLabel fineLbl = new JLabel(pendFine>0
            ? "⚠️  Pending Fine:  ₹"+String.format("%.0f",pendFine)+"  ("+(days-15)+" days × ₹50)"
            : "✅  No fine — returned within 15 days");
        fineLbl.setFont(UITheme.BOLD()); fineLbl.setForeground(pendFine>0?UITheme.ROSE:UITheme.EMERALD);
        popup.add(fineLbl);

        JTextField fineInput = null;
        if (pendFine>0) {
            popup.add(UITheme.label("FINE COLLECTED (₹)"));
            fineInput = new JTextField(String.format("%.0f",pendFine),10);
            UITheme.styleField(fineInput); popup.add(fineInput);
        }

        if (JOptionPane.showConfirmDialog(AppFrame.get(),popup,"Return Book",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION) return;

        double collected = 0;
        if (pendFine>0 && fineInput!=null) {
            try { collected=Double.parseDouble(fineInput.getText().trim()); }
            catch(NumberFormatException ex){JOptionPane.showMessageDialog(AppFrame.get(),"Invalid amount."); return;}
        }

        if (dao.returnBook(id,collected)) {
            JOptionPane.showMessageDialog(AppFrame.get(),
                "✅ Book returned!"+(pendFine>0?"\n   Fine collected: ₹"+String.format("%.0f",collected):""),
                "Success",JOptionPane.INFORMATION_MESSAGE);
            load();
        } else JOptionPane.showMessageDialog(AppFrame.get(),"❌ Return failed.","Error",JOptionPane.ERROR_MESSAGE);
    }

    private void addRow(JPanel p, String key, String val) {
        JPanel row = new JPanel(new BorderLayout(10,0)); row.setBackground(UITheme.BG_CARD);
        JLabel k=new JLabel(key+":"); k.setFont(UITheme.LABEL()); k.setForeground(UITheme.TEXT_SEC); k.setPreferredSize(new Dimension(80,20));
        JLabel v=new JLabel(val); v.setFont(UITheme.BOLD()); v.setForeground(UITheme.TEXT_PRI);
        row.add(k,BorderLayout.WEST); row.add(v,BorderLayout.CENTER); p.add(row);
    }
}

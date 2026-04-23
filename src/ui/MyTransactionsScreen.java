package ui;

import dao.TransactionDAO;
import model.Transaction;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyTransactionsScreen {

    private final TransactionDAO dao = new TransactionDAO();
    private final User student;
    private DefaultTableModel tableModel;
    private JLabel pendingLbl = new JLabel("₹0");
    private JLabel paidLbl    = new JLabel("₹0");

    public MyTransactionsScreen(User student) {
        this.student = student;
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout(0,0));
        root.add(buildToolbar(),  BorderLayout.NORTH);
        root.add(buildTable(),    BorderLayout.CENTER);
        root.add(buildSummary(),  BorderLayout.SOUTH);
        AppFrame.get().setTitle("My Transactions — Library MS");
        AppFrame.get().navigate(root);
        load();
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack()); p.add(back);
        p.add(Box.createHorizontalStrut(12));
        JLabel h = UITheme.h2("📋  My Book Transactions"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        p.add(Box.createHorizontalStrut(16));
        UITheme.StyledButton ref = UITheme.neutralBtn("🔄 Refresh");
        ref.addActionListener(e -> load()); p.add(ref); return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"Code","Book Title","Issue Date","Return Date","Days","Pending Fine","Fine Paid","Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StatusRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new UITheme.AltRowRenderer(){
            @Override public java.awt.Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                String val=v==null?"":v.toString();
                if(!sel&&!val.equals("—")) setForeground(UITheme.ROSE); setHorizontalAlignment(SwingConstants.RIGHT); return this;
            }
        });
        table.getColumnModel().getColumn(6).setCellRenderer(new UITheme.AltRowRenderer(){
            @Override public java.awt.Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                String val=v==null?"":v.toString();
                if(!sel&&!val.equals("—")) setForeground(UITheme.AMBER); setHorizontalAlignment(SwingConstants.RIGHT); return this;
            }
        });
        int[] w={80,260,90,90,55,120,100,90};
        for(int i=0;i<w.length;i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        return UITheme.styledScroll(table);
    }

    private JPanel buildSummary() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 12));
        p.setBackground(UITheme.BG_SURFACE);
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_CLR));

        JLabel rule=new JLabel("Fine: ₹50/day after 15 days"); rule.setFont(UITheme.SMALL()); rule.setForeground(UITheme.TEXT_MUTED);
        JLabel pl=new JLabel("⚠️  Pending:"); pl.setFont(UITheme.BOLD()); pl.setForeground(UITheme.TEXT_SEC);
        pendingLbl.setFont(new Font("Segoe UI",Font.BOLD,16)); pendingLbl.setForeground(UITheme.ROSE);
        JLabel pal=new JLabel("✅  Paid:"); pal.setFont(UITheme.BOLD()); pal.setForeground(UITheme.TEXT_SEC);
        paidLbl.setFont(new Font("Segoe UI",Font.BOLD,16)); paidLbl.setForeground(UITheme.EMERALD);

        p.add(rule); p.add(pl); p.add(pendingLbl); p.add(pal); p.add(paidLbl); return p;
    }

    private void load() {
        tableModel.setRowCount(0);
        List<Transaction> list = dao.getTransactionsByUser(student.getId());
        double totalPend=0, totalPaid=0;
        for (Transaction t : list) {
            long   days = Transaction.daysElapsed(t.getIssueDate());
            double pend = t.getReturnDate()==null ? Transaction.calculatePendingFine(t.getIssueDate()) : 0;
            String st   = t.getReturnDate()!=null ? "Returned" : pend>0 ? "Overdue" : "Active";
            tableModel.addRow(new Object[]{t.getBookCode(),t.getBookTitle(),t.getIssueDate(),
                t.getReturnDate()!=null?t.getReturnDate():"—", days,
                pend>0?"₹"+String.format("%.0f",pend):"—",
                t.getFinePaid()>0?"₹"+String.format("%.0f",t.getFinePaid()):"—", st});
            totalPend+=pend; totalPaid+=t.getFinePaid();
        }
        pendingLbl.setText("₹"+String.format("%.0f",totalPend));
        paidLbl.setText("₹"+String.format("%.0f",totalPaid));
        pendingLbl.setForeground(totalPend>0?UITheme.ROSE:UITheme.EMERALD);
    }
}

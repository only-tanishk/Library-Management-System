package ui;

import dao.TransactionDAO;
import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ActiveStudentsScreen {

    private final UserDAO        userDAO = new UserDAO();
    private final TransactionDAO dao     = new TransactionDAO();
    private DefaultTableModel tableModel;

    public ActiveStudentsScreen() {
        JPanel root = UITheme.gradientPanel();
        root.setLayout(new BorderLayout());
        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(buildTable(),   BorderLayout.CENTER);
        AppFrame.get().setTitle("Active Students — Library MS");
        AppFrame.get().navigate(root);
        load();
    }

    private JPanel buildToolbar() {
        JPanel p = UITheme.toolbarPanel();
        UITheme.StyledButton back = UITheme.neutralBtn("← Back");
        back.addActionListener(e -> AppFrame.get().goBack()); p.add(back);
        p.add(Box.createHorizontalStrut(12));
        JLabel h = UITheme.h2("👥  Registered Students"); h.setForeground(UITheme.TEXT_PRI); p.add(h);
        p.add(Box.createHorizontalStrut(16));
        UITheme.StyledButton ref = UITheme.neutralBtn("🔄 Refresh");
        ref.addActionListener(e -> load()); p.add(ref); return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"ID", "Name", "Username", "Books Issued"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setCellRenderer(new UITheme.AltRowRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel) setForeground(v!=null&&v.toString().startsWith("2")?UITheme.AMBER:UITheme.EMERALD);
                return this;
            }
        });
        return UITheme.styledScroll(table);
    }

    private void load() {
        tableModel.setRowCount(0);
        for (User s : userDAO.getAllStudents())
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getUsername(), dao.getActiveBookCount(s.getId())+" / 2"});
    }
}

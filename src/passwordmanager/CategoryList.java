/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class CategoryList extends JFrame{
    
    private static HashMap<DbConnection, ArrayList<String>> visibility = new HashMap<DbConnection, ArrayList<String>>();
    
    public static String[] GetVisibilityFor(DbConnection con){
        if(visibility.containsKey(con)){
            ArrayList<String> list = visibility.get(con);
            return list.toArray(new String[list.size()]);
        }else{
            return new String[0];
        }
    }
    
    public CategoryList(DbConnection con){
        super();
        this.setTitle("Categories");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(300, 200);
        
        
        ArrayList<String> vis;
        if(visibility.containsKey(con)){
            vis = visibility.get(con);
        }else{
            vis = new ArrayList<String>();
            visibility.put(con, vis);
        }
        
        this.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent fe) {}

            @Override
            public void focusLost(FocusEvent fe) {
                dispose();
            }
        });
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        this.add(content);
        
        JPanel header = new JPanel();
        content.add(header, BorderLayout.NORTH);
        
        JPanel list = new JPanel();
        
        JTextField name = new JTextField();
        name.setPreferredSize(new Dimension(200, 24));
        header.add(name);
        JLabel add = new JLabel(new ImageIcon(Resources.resources.addIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH))); 
        add.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                String n = name.getText().trim();
                if(!n.equals("")){
                    con.CreateCategory(n);
                }
                UpdateList(list, vis, con);
            }
        });
        header.add(add);
        
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        JScrollPane listScroll = new JScrollPane(list);
        content.add(listScroll, BorderLayout.CENTER);
        
        UpdateList(list, vis, con);
    }

    private void UpdateList(JPanel parent, ArrayList<String> visibility, DbConnection con){
        parent.removeAll();
        try{
            Color evenColor = new Color(230, 230, 230);
            Color oddColor = new Color(166,166,166);
            ResultSet s = con.GetCategories();
            int i = 0;
            ImageIcon showI = new ImageIcon(Resources.resources.showIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
            ImageIcon hideI = new ImageIcon(Resources.resources.hideIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
            while(s != null && s.next()){
                String name = s.getString("name");
                String id = s.getString("id");
                JPanel detailPanel = new JPanel();
                detailPanel.setBorder(new EmptyBorder(10,10,10,10));
                detailPanel.setBackground(i % 2 == 0 ? evenColor : oddColor);
                detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.X_AXIS));
                detailPanel.add(new JLabel(name));
                detailPanel.add(Box.createHorizontalGlue());
                
                JLabel delete = new JLabel(new ImageIcon(Resources.resources.trashIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                delete.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent evt){
                        int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this category?");
                        if(i == JOptionPane.YES_OPTION){
                            con.DeleteCategory(id);
                            UpdateList(parent, visibility, con);
                        }
                    }
                });
                
                ImageIcon q = showI;
                if(visibility.contains(id)){
                    q = hideI;
                }
                JLabel show = new JLabel(q);
                show.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                       if(visibility.contains(id)){
                           visibility.remove(id);
                           show.setIcon(showI);
                       }else{
                           visibility.add(id);
                           show.setIcon(hideI);
                       }
                    }
                });
                
                detailPanel.add(show);
                detailPanel.add(delete);
                parent.add(detailPanel);
                i++;
            }
            parent.invalidate();
            parent.revalidate();
        }catch(Exception e){ e.printStackTrace(); }
    }
}

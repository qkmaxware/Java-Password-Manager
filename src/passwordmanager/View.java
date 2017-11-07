/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author Colin Halseth
 */
public class View extends JFrame{
    
    private Color navColor = Color.white;
    private Color detailColor = new Color(57,74,84);
    private Color titleColor = new Color(201, 45, 39);
    
    private DBmanager[] connections;
    private int activeConnectionIndex = 0;
    private String empty = "";
    
    private SiteDetailViewer view;
    
    public View(DBmanager... conns){
        super();
        
        this.connections = conns;
        if(conns.length == 0)
            return;
        
        JPanel mp = new JPanel();
        mp.setLayout(new BorderLayout());
        
        //Content Pane
        JPanel contentPane = new JPanel();
        CardLayout cards = new CardLayout();
        contentPane.setLayout(cards);
        
        JScrollPane SitesPane = MakeContentPane();
        contentPane.add(SitesPane, "Sites");
        JPanel sitesContent = ((JPanel)SitesPane.getViewport().getView());
        
        PopulateSites(sitesContent, connections[activeConnectionIndex].GetAllSites());
        
        //Header
        JPanel headPanel = new JPanel();
        headPanel.setBorder(new EmptyBorder(10,10,10,10));
        headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.LINE_AXIS));
        headPanel.setBackground(titleColor);
        mp.add(headPanel, BorderLayout.NORTH);
        
        JLabel icon = new JLabel(new ImageIcon(Resources.resources.searchIcon.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        icon.setPreferredSize(new Dimension(32,32));
        headPanel.add(icon);
        
        JTextField search = new JTextField();
        search.addActionListener((evt) -> {
            String s = search.getText().trim();
            if(s.equals(empty)){
                PopulateSites(sitesContent, connections[activeConnectionIndex].GetAllSites());
                return;
            }
            
            PopulateSites(sitesContent, connections[activeConnectionIndex].SearchSites(s));
            System.out.println("Search for: "+search.getText());
        });
        headPanel.add(search);
        
        headPanel.add(Box.createHorizontalStrut(240));
        
        String[] connNames = new String[connections.length];
        for(int i = 0; i < connNames.length; i++){
            connNames[i] = connections[i].GetName();
        }        
        JComboBox database = new JComboBox(connNames);
        headPanel.add(database);
        
        //Site details
        view = new SiteDetailViewer();
        view.setBackground(this.detailColor);
        JScrollPane viewScroll = new JScrollPane(view);
        viewScroll.setPreferredSize(new Dimension(200, 500));
        mp.add(viewScroll, BorderLayout.EAST);
        
        //Navigation
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(navColor);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setPreferredSize(new Dimension(32, 100));
        leftPanel.setBorder(new EmptyBorder(12,12,12,12));
        mp.add(leftPanel, BorderLayout.WEST);
        
        mp.add(contentPane, BorderLayout.CENTER);
        
        this.add(mp);
    }
 
    private JScrollPane MakeContentPane(){
        JPanel content = new JPanel();
        content.setLayout(new WrapLayout());
        
        JScrollPane scroll = new JScrollPane(content);
        
        return scroll;
    }
    
    private JPanel MakeSiteListing(String id, String siteName, String url){
        JPanel panel = new JPanel();
        panel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                view.SetDetails(id, connections[activeConnectionIndex]);
            }
        });
        
        //panel.setPreferredSize(new Dimension(160,120));
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(10, 10, 10, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel center = new JPanel();
        JLabel label = new JLabel(new ImageIcon(Resources.resources.unknownIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        center.add(label);
        panel.add(center);
        
        panel.add(new JSeparator());
        
        JPanel lower = new JPanel();
        lower.setLayout(new GridLayout(0, 1));
        
        JLabel name = new JLabel(siteName);
        name.setFont(name.getFont().deriveFont(Font.BOLD));
        lower.add(name);
        
        JLabel site = new JLabel(url);
        lower.add(site);
        
        panel.add(lower);
        
        return panel;
    }
    
    private void PopulateSites(JPanel panel, ResultSet set){
        //Clear panel
        panel.removeAll();
        
        try{
            while(set.next()){               
                String sid = set.getString("id");
                String sname = set.getString("name");
                String surl = set.getString("url");
                
                panel.add(MakeSiteListing(sid, sname, surl));
            }
        }catch(Exception e){
            
        }
        
        panel.invalidate();
        panel.revalidate();
        panel.repaint();
    }
    
}

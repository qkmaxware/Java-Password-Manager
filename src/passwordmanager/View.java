/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import passwordmanager.popup.NewAccountBuilder;
import passwordmanager.popup.NewDatabaseBuilder;
import passwordmanager.popup.NewSiteBuilder;
import passwordmanager.layouts.WrapLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
    
    private ArrayList<DBmanager> connections = new ArrayList<DBmanager>();
    private int activeConnectionIndex = 0;
    private String empty = "";
    
    private SiteDetailViewer view;
    
    private static View me;
    
    public View(DBmanager... conns){
        super();
        
        me = this;
        
        for(int i = 0; i < conns.length; i++) 
            connections.add(conns[i]);
        
        JPanel mp = new JPanel();
        mp.setLayout(new BorderLayout());
        
        //Content Pane
        JPanel contentPane = new JPanel();
        CardLayout cards = new CardLayout();
        contentPane.setLayout(cards);
        
        JScrollPane SitesPane = MakeContentPane();
        contentPane.add(SitesPane, "Sites");
        JPanel sitesContent = ((JPanel)SitesPane.getViewport().getView());
        
        if(conns.length > 0){
            this.UpdateSiteList(sitesContent, null);
        }
        
        //Header
        JPanel headPanel = new JPanel();
        headPanel.setBorder(new EmptyBorder(10,10,10,10));
        headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.X_AXIS));
        headPanel.setBackground(titleColor);
        mp.add(headPanel, BorderLayout.NORTH);
        
        JTextField search = new JTextField();
        ActionListener searchListener = (evt) -> {
            String s = search.getText().trim();
            if(s.equals(empty)){
                this.UpdateSiteList(sitesContent, null);
                return;
            }
            
            this.UpdateSiteList(sitesContent, s);
        };
        search.addActionListener(searchListener);
        search.setPreferredSize(new Dimension(300,28));
        JLabel icon = new JLabel(new ImageIcon(Resources.resources.searchIcon.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        icon.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                searchListener.actionPerformed(null);
            }
        });
        icon.setPreferredSize(new Dimension(32,32));
        headPanel.add(icon);
        
        headPanel.add(search);
        
        headPanel.add(Box.createHorizontalStrut(240));
              
        JComboBox database = new JComboBox();
        database.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeConnectionIndex = database.getSelectedIndex();
                UpdateSiteList(sitesContent, search.getText().trim());
            }
        });
        UpdateDatabaseList(database);
        headPanel.add(database);
        
        //Site details
        view = new SiteDetailViewer();
        view.setPreferredSize(new Dimension(180, 500));
        view.setBackground(this.detailColor);
        view.setPreferredSize(new Dimension(200, 500));
        mp.add(view, BorderLayout.EAST);
        
        //Navigation
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(navColor);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setBorder(new EmptyBorder(5,5,5,5));
        
        
        
        leftPanel.add(Box.createVerticalGlue());
        
        JPopupMenu createMenu = new JPopupMenu();
        JMenuItem createDb = new JMenuItem("Create Database");
        createDb.addActionListener((evt) -> {
            NewDatabaseBuilder ndb = new NewDatabaseBuilder((connection) -> {
                AddNewDatabase(database, connection);
                String s = search.getText().trim();
                this.UpdateSiteList(sitesContent, s.equals(empty) ? null : s);
            });
            ndb.setVisible(true);
            CenterFrameInWindow(ndb);
        });
        createMenu.add(createDb);
        JMenuItem createCollection = new JMenuItem("Create Account Collection");
        createCollection.addActionListener((evt) -> {
            DBmanager con = GetConnection();
            if(con == null)
                return;
            NewSiteBuilder nsb = new NewSiteBuilder(con);
            nsb.onCreate = () -> {
                String s = search.getText().trim();
                this.UpdateSiteList(sitesContent, s.equals(empty) ? null : s);
            };
            nsb.setVisible(true);
            CenterFrameInWindow(nsb);
        });
        createMenu.add(createCollection);
        createMenu.pack();
        
        JLabel newSite = new JLabel(new ImageIcon(Resources.resources.addIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        newSite.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                Point pos = new Point();
                Dimension size = createMenu.getPreferredSize();
                pos.x = newSite.getWidth() / 2;
                pos.y = newSite.getHeight() / 2 - size.height;
                createMenu.show(newSite, pos.x, pos.y);
            }
        });
        leftPanel.add(newSite);
        
        mp.add(leftPanel, BorderLayout.WEST);
        
        mp.add(contentPane, BorderLayout.CENTER);
        
        view.onCreateAccount = (siteId) -> {
            DBmanager con = GetConnection();
            if(con == null)
                return;
            NewAccountBuilder nab = new NewAccountBuilder(siteId, con);
            nab.onCreate = () -> {
                view.SetDetails(siteId, connections.get(activeConnectionIndex));
            };
            nab.setVisible(true);
            CenterFrameInWindow(nab);
            view.SetDetails(siteId, con);
        };
        
        view.onDelete = () -> {
            this.UpdateSiteList(sitesContent, search.getText().trim());
            view.SetDetails(null, null);
        };
        
        this.add(mp);
    }
    
    public static void CenterFrame(JFrame frame){
        if(me != null)
            me.CenterFrameInWindow(frame);
    }
    
    private void CenterFrameInWindow(JFrame frame){
        int myw = this.getWidth() / 2;
        int myh = this.getHeight() / 2;
        int myx = this.getX();
        int myy = this.getY();
        
        int fw = frame.getWidth() / 2;
        int fy = frame.getHeight() / 2;
        
        frame.setLocation(myx + myw - fw, myh + myy - fy);
    }
    
    private void AddNewDatabase(JComboBox list, DBmanager manager){
        this.connections.add(manager);
        UpdateDatabaseList(list);
    }
    
    public DBmanager GetConnection(){
        //Valid index
        if(activeConnectionIndex >= this.connections.size()){
            return null;
        }
        
        DBmanager connection = connections.get(activeConnectionIndex);
        return connection;
    }
    
    private void UpdateSiteList(JPanel sitesContent, String search){
        //Valid index
        if(activeConnectionIndex >= this.connections.size()){
            return;
        }
        
        DBmanager connection = connections.get(activeConnectionIndex);
        if(search == null || search.equals(empty)){
            PopulateSites(sitesContent, connection.GetAllSites());
        }else{
            PopulateSites(sitesContent, connection.SearchSites(search));
        }
    }
    
    private void UpdateDatabaseList(JComboBox list){
        String[] connNames = new String[connections.size()];
        for(int i = 0; i < connNames.length; i++){
            connNames[i] = connections.get(i).GetName();
        }  
        list.setModel(new DefaultComboBoxModel(connNames));
    }
    
    private JScrollPane MakeContentPane(){
        JPanel content = new JPanel();
        content.setLayout(new WrapLayout());
        
        JScrollPane scroll = new JScrollPane(content);
        scroll.getVerticalScrollBar().setUnitIncrement(32);
        
        return scroll;
    }
    
    private JPanel MakeSiteListing(String id, String siteName, String url){
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(140,120));
        panel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                view.SetDetails(id, connections.get(activeConnectionIndex));
            }
        });
        
        //panel.setPreferredSize(new Dimension(160,120));
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(10, 10, 10, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel center = new JPanel();
        
        DBmanager m = GetConnection();
        BufferedImage img = Resources.resources.unknownIcon;
        if(m != null){
            BufferedImage img2 = (BufferedImage)m.GetSiteImage(id);
            if(img2 != null)
                img = img2;
        }
        
        JLabel label = new JLabel(new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
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

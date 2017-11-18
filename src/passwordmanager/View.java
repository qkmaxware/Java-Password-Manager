/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import passwordmanager.popup.NewAccountBuilder;
import passwordmanager.popup.NewSiteBuilder;
import passwordmanager.layouts.WrapLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import passwordmanager.connections.DbConnection;
import passwordmanager.popup.ConnectionManager;

/**
 *
 * @author Colin Halseth
 */
public class View extends JFrame{
    
    private Color navColor = Color.white;
    private Color detailColor = new Color(57,74,84);
    private Color titleColor = new Color(201, 45, 39);
    
    private ArrayList<DbConnection> connections = new ArrayList<DbConnection>();
    private String empty = "";
    
    private SiteDetailViewer view;
    private static View me;
    private Config config;
    private JComboBox connectionList;
    private DefaultComboBoxModel connectionListModel;
    
    public View(Config config){
        super();
        this.config = config;
        
        me = this;
        
        JPanel mp = new JPanel();
        mp.setLayout(new BorderLayout());
        
        //Content Pane
        JPanel contentPane = new JPanel();
        CardLayout cards = new CardLayout();
        contentPane.setLayout(cards);
        
        JScrollPane SitesPane = MakeContentPane();
        contentPane.add(SitesPane, "Sites");
        JPanel sitesContent = ((JPanel)SitesPane.getViewport().getView());
        
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
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        database.setModel(model);
        connectionListModel = model;  
        connectionList = database;
        database.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateSiteList(sitesContent, search.getText().trim());
            }
        });
        headPanel.add(database);
        
        JLabel refresh = new JLabel(new ImageIcon(Resources.resources.refreshIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        refresh.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                UpdateDatabaseList();
            }
        });
        headPanel.add(refresh);
        
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
        createMenu.pack();
        
        JLabel newSite = new JLabel(new ImageIcon(Resources.resources.addIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        newSite.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                DbConnection con = GetConnection();
                if(con == null)
                    return;
                NewSiteBuilder nsb = new NewSiteBuilder(con);
                nsb.onCreate = () -> {
                    String s = search.getText().trim();
                    UpdateSiteList(sitesContent, s.equals(empty) ? null : s);
                };
                nsb.setVisible(true);
                CenterFrameInWindow(nsb);
                /*
                NewDatabaseBuilder ndb = new NewDatabaseBuilder((connection) -> {
                    Config.ConnectionConfig con = new Config.ConnectionConfig();
                    con.connectionString = "data/" + connection + ".db";
                    con.driver = "sqlite";
                    AddNewDatabase(database, config.AddConnection(con));
                    String s = search.getText().trim();
                    UpdateSiteList(sitesContent, s.equals(empty) ? null : s);
                });
                ndb.setVisible(true);
                CenterFrameInWindow(ndb);
                */
                /*
                Point pos = new Point();
                Dimension size = createMenu.getPreferredSize();
                pos.x = newSite.getWidth() / 2;
                pos.y = newSite.getHeight() / 2 - size.height;
                createMenu.show(newSite, pos.x, pos.y);*/
            }
        });
        leftPanel.add(newSite);
        
        JLabel connectionMgr = new JLabel(new ImageIcon(Resources.resources.connectIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        connectionMgr.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                ConnectionManager mgr = new ConnectionManager(config);
                mgr.OnNew = () -> {
                    UpdateDatabaseList();
                };
                mgr.OnChange = () -> {
                    UpdateDatabaseList();
                };
                mgr.OnDelete = () -> {
                    UpdateDatabaseList();
                };
                mgr.setVisible(true);
                View.CenterFrame(mgr);
            }
        });
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(connectionMgr);
        
        mp.add(leftPanel, BorderLayout.WEST);
        mp.add(contentPane, BorderLayout.CENTER);
        
        view.onCreateAccount = (siteId) -> {
            DbConnection con = GetConnection();
            if(con == null)
                return;
            NewAccountBuilder nab = new NewAccountBuilder(siteId, con);
            nab.onCreate = () -> {
                view.SetDetails(siteId, connections.get(this.GetActiveIndex()));
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
        
        UpdateDatabaseList();
        if(this.connections.size() > 0){
            this.UpdateSiteList(sitesContent, null);
        }
        
    }
   
    public int GetActiveIndex(){
        return connectionList.getSelectedIndex();
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
    
    public DbConnection GetConnection(){
        //Valid index
        int activeConnectionIndex = GetActiveIndex();
        if(activeConnectionIndex >= this.connections.size()){
            return null;
        }
        
        DbConnection connection = connections.get(activeConnectionIndex);
        return connection;
    }
    
    private void UpdateSiteList(JPanel sitesContent, String search){
        //Valid index
        int activeConnectionIndex = GetActiveIndex();
        if(activeConnectionIndex < 0 || activeConnectionIndex >= this.connections.size()){
            return;
        }
        
        DbConnection connection = connections.get(activeConnectionIndex);
        if(search == null || search.equals(empty)){
            PopulateSites(sitesContent, connection.GetAllSites());
        }else{
            PopulateSites(sitesContent, connection.SearchSites(search));
        }
    }
    
    private void UpdateDatabaseList(){
        connectionListModel.removeAllElements();
        this.connections.clear();
        for(int i = 0; i < this.config.CountConnections(); i++){
            DbConnection db = this.config.GetConnection(i).DeriveConnection();
            this.connections.add(db);
            connectionListModel.addElement(db.GetName());
        }  
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
                view.SetDetails(id, connections.get(GetActiveIndex()));
            }
        });
        
        //panel.setPreferredSize(new Dimension(160,120));
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(10, 10, 10, 10)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel center = new JPanel();
        
        DbConnection m = GetConnection();
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.popup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import passwordmanager.Config;
import passwordmanager.Config.ConnectionConfig;
import passwordmanager.Resources;
import passwordmanager.actions.Action;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class ConnectionManager extends JFrame{
    
    private JPanel connectionList;
    private Config config;
    private int selectedIndex = -1;
    
    private JComboBox driver;
    private JTextField name;
    private JTextField username;
    private JTextField password;
    private JCheckBox requiresPassword;
    
    public Action OnNew;
    public Action OnChange;
    public Action OnDelete;
    
    private static class Driver{
        private String name;
        private String driver;
        private BufferedImage img;
        public Driver(String name, String driver, BufferedImage icon){
            this.name = name; this.driver = driver; this.img = icon;
        }
        public String toString(){
            return this.name;
        }
        public String getDriver(){
            return driver;
        }
        public BufferedImage GetIcon(){
            return img;
        }
    }
    
    public ConnectionManager(Config config){
        super();
        
        this.config = config;
        this.setTitle("Connection Manager");
        this.setSize(480, 320);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = new JPanel(new BorderLayout());
        this.add(content);
        
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBorder(new EmptyBorder(5,5,5,5));
        JButton testConnection = new JButton("Test Connection");
        testConnection.addActionListener((evt) -> {
            boolean connected = TestConnection();
            JOptionPane.showMessageDialog(null, "Connection " + (connected ? "was successful" : "failed"));
        });
        footer.add(testConnection);
        footer.add(Box.createHorizontalGlue());
        
        JButton newConn = new JButton("New");
        newConn.addActionListener((evt) -> {
            NewConnectionDetails();
        });
        footer.add(newConn);
        footer.add(Box.createHorizontalStrut(5));
        
        JButton quit = new JButton("Save");
        quit.addActionListener((evt) -> {
            SaveConnectionEdits();
        });
        footer.add(quit);
        footer.add(Box.createHorizontalStrut(5));
        
        JButton delete = new JButton("Delete");
        delete.addActionListener((evt) -> {
            DeleteConnection();
        });
        footer.add(delete);
        
        content.add(footer, BorderLayout.SOUTH);
        
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
        left.setBorder(new EmptyBorder(5,5,5,5));
        connectionList = left;
        
        JScrollPane leftContainer = new JScrollPane(left);
        content.add(leftContainer, BorderLayout.WEST);
        
        JPanel detailPane = new JPanel();
        detailPane.setLayout(new BoxLayout(detailPane, BoxLayout.Y_AXIS));
        
        JComboBox drive = new JComboBox(new Driver[]{
            new Driver("Local SQlite Database","sqlite", Resources.resources.sqliteIcon),
            new Driver("PostgreSQL Database","postgres", Resources.resources.postgresIcon),
            new Driver("Restful Web Service","http", Resources.resources.httpIcon)
        });
        this.driver = drive;
        Dimension d = new Dimension(240, drive.getPreferredSize().height);
        detailPane.add(AddField("Driver", drive));
        
        JTextField name = new JTextField();
        this.name = name;
        name.setPreferredSize(d);
        JButton location = new JButton("Browse");
        location.addActionListener((evt) -> {
            JFileChooser fc = new JFileChooser();
            int returnValue = fc.showOpenDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String path = f.getAbsolutePath();
                String localPath = Paths.get("").toAbsolutePath() + File.separator;
                //If relative to current location, store relative path not absolute
                name.setText(path.replace(localPath, ""));
            }
        });
        detailPane.add(AddField("Location", name, location));
        
        JCheckBox checkBox = new JCheckBox();
        this.requiresPassword = checkBox;
        detailPane.add(AddField("Requires Password", checkBox));

        JTextField user = new JTextField();
        this.username = user;
        user.setPreferredSize(d);
        detailPane.add(AddField("Username", user));
        
        JTextField pass = new JTextField();
        pass.setPreferredSize(d);
        this.password = pass;
        detailPane.add(AddField("Password", pass));
        
        JScrollPane details = new JScrollPane(detailPane);
        content.add(details, BorderLayout.CENTER);
        
        RedoConnections();
    }
    
    private Component AddField(String field, Component... comp){
       JPanel box = new JPanel();
       box.setLayout(new FlowLayout(FlowLayout.LEFT));
       
       box.add(new JLabel(field + ": "));
       for(int i = 0; i < comp.length; i++)
            box.add(comp[i]);
       
       return box;
    }
    
    private void RedoConnections(){
        connectionList.removeAll();
        for(int i = 0; i < config.CountConnections(); i++){
            ConnectionConfig conn = config.GetConnection(i);
            int j = i;
            
            Image im = null;
            for(int q = 0; q < this.driver.getItemCount(); q++){
                Driver d = (Driver)this.driver.getItemAt(q);
                if(d.getDriver().equals(conn.driver)){
                    im = d.GetIcon();
                    break;
                }
            }
            im = (im == null) 
                    ? Resources.resources.unknownIcon.getScaledInstance(32, 32, Image.SCALE_SMOOTH)
                    : im.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            
            JLabel l = new JLabel(new ImageIcon(im));
            l.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e){
                    ShowConnectionDetails(j, conn);
                }
            });
            connectionList.add(l);
        }
        
        connectionList.invalidate();
        connectionList.revalidate();
        connectionList.repaint();
    }
    
    private boolean TestConnection(){
        if(this.selectedIndex < 0 || this.selectedIndex >= config.CountConnections())
            return false;
        
        DbConnection db = this.config.GetConnection(this.selectedIndex).DeriveConnection();
        return db.IsConnected() && db.TestConnection();
    }
    
    private void NewConnectionDetails(){
        ConnectionConfig conn = new ConnectionConfig();
        conn.connectionString = this.name.getText();
        conn.requiresPassword = this.requiresPassword.isSelected();
        conn.driver = ((Driver)this.driver.getSelectedItem()).getDriver();
        conn.user = this.username.getText();
        conn.password = this.password.getText();
        config.AddConnection(conn);
        
        RedoConnections();
        
        if(OnNew != null)
            OnNew.Invoke();
        
        this.selectedIndex = config.CountConnections() - 1;
    }
    
    private void DeleteConnection(){
        if(this.selectedIndex < 0 || this.selectedIndex >= config.CountConnections())
            return;
        
        config.DeleteConnection(this.selectedIndex);
        
        RedoConnections();
        
        if(OnDelete != null)
            OnDelete.Invoke();
        
        if(config.CountConnections() > 0){
            this.selectedIndex = 0;
            ShowConnectionDetails(0, config.GetConnection(0));
        }else{
            this.selectedIndex = -1;
        }
    }
    
    private void SaveConnectionEdits(){
        if(this.selectedIndex < 0 || this.selectedIndex >= config.CountConnections())
            return;
        
        ConnectionConfig conn = this.config.GetConnection(this.selectedIndex);
        conn.connectionString = this.name.toString();
        conn.requiresPassword = this.requiresPassword.isSelected();
        conn.driver = ((Driver)this.driver.getSelectedItem()).getDriver();
        conn.user = this.username.toString();
        conn.password = this.password.toString();
        
        config.MarkDirty();
        if(OnChange != null)
            OnChange.Invoke();
    }
    
    private void ShowConnectionDetails(int j, ConnectionConfig conn){
        this.selectedIndex = j;
        this.name.setText(conn.connectionString);
        Driver d = null;
        for(int i = 0; i < this.driver.getItemCount(); i++){
            Driver q = (Driver)this.driver.getItemAt(i);
            if(q.getDriver().equals(conn.driver)){
                d = q;
                break;
            }
        }
        this.driver.setSelectedItem(d);
        this.username.setText(conn.user != null ? conn.user : "");
        this.password.setText(conn.password != null ? conn.password : "");
        this.requiresPassword.setSelected(conn.requiresPassword);
    }
    
}

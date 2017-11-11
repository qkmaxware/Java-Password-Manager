/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.popup;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import passwordmanager.DBmanager;
import passwordmanager.ImgViewer;
import passwordmanager.actions.Action;
import passwordmanager.utils.WebsiteIconExtractor;

/**
 *
 * @author Colin Halseth
 */
public class NewSiteBuilder extends JFrame {
    
    public Action onCreate;
    
    private ImgViewer icon;
    private JTextField dbname;
    private JTextField dburl;
    private JTextField dbkeywords;
    
    private JButton button;
    
    private String idToModify = null;
    private WebsiteIconExtractor extractor = new WebsiteIconExtractor();
    
    public NewSiteBuilder(String siteid, DBmanager connection){
        this(connection);
        this.setTitle("Modify Collection");
        button.setText("Update");
        idToModify = siteid;
        
        try{
            ResultSet details = connection.GetSiteDetails(siteid);
            if(details.next()){
                dbname.setText(details.getString("name"));
                dburl.setText(details.getString("url"));
                dbkeywords.setText(details.getString("keywords"));
            }
            
            icon.image = (BufferedImage)connection.GetSiteImage(siteid);
        }catch(Exception e){}
    }
    
    public NewSiteBuilder(DBmanager connection){
        super();
        
        this.setTitle("New Collection");
        this.setSize(300, 340);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(10,10,10,10));
        content.setLayout(new GridLayout(0,1));
        
        icon = new ImgViewer();
        icon.setPreferredSize(new Dimension(150,150));
        content.add(icon);
        
        JPanel p0 = new JPanel();
        JButton fromUrl = new JButton("From URL");
        fromUrl.addActionListener((evt) -> {
            icon.image = extractor.getIconFromSiteUrl(dburl.getText());
            if(icon.image == null)
                JOptionPane.showMessageDialog(null, "Failed to obtain the favicon.ico file from the given url.");
            icon.repaint();
        });
        JButton fromFile = new JButton("From File");
        fromFile.addActionListener((evt)-> {
            List<String> l = Arrays.asList(new String[]{"jpeg", "jpg", "png", "bmp"});
            final JFileChooser fc = new JFileChooser(){
                @Override
                public boolean accept(File f){
                    String s = f.getName();
                    int i = s.lastIndexOf('.');
                    if (i > 0 &&  i < s.length() - 1) {
                        return l.contains(s.substring(i+1).toLowerCase());
                    }
                    return f.isDirectory();
                }
            };
            int returnVal = fc.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try{
                    BufferedImage io = ImageIO.read(file);
                    icon.image = io;
                    icon.repaint();
                }catch(Exception e){}
            } else {

            }
        });
        p0.add(new JLabel("Icon:"));
        p0.add(fromUrl);
        p0.add(fromFile);
        content.add(p0);
        
        dbname = new JTextField();
        dbname.setPreferredSize(new Dimension(150,24));
        content.add(new JLabel("Name:"));
        content.add(dbname);
        
        dburl = new JTextField();
        dburl.setPreferredSize(new Dimension(150,24));
        content.add(new JLabel("URL:"));
        content.add(dburl);
        
        dbkeywords = new JTextField();
        dbkeywords.setPreferredSize(new Dimension(150,24));
        content.add(new JLabel("Tags:"));
        content.add(dbkeywords);
        
        button = new JButton("Create");
        button.addActionListener((e) -> {
            if(idToModify == null){
                long nid = connection.CreateSite(dbname.getText(), dburl.getText(), dbkeywords.getText());
                if(icon.image != null)
                    connection.SetSiteImage(""+nid, icon.image);
            }else{
                connection.UpdateSite(idToModify, dbname.getText(), dburl.getText(), dbkeywords.getText());
                if(icon.image != null)
                    connection.SetSiteImage(idToModify, icon.image);
            }
            
            if(onCreate != null)
                onCreate.Invoke();
            
            dispose();
        });
        content.add(button);

        this.add(content);
    }
    
}

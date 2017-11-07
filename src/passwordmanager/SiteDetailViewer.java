/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Colin Halseth
 */
public class SiteDetailViewer extends JPanel {

    private static class ImgViewer extends JPanel {

        public BufferedImage image;

        public ImgViewer() {
            super();
            this.setBackground(new Color(255,255,255,0));
            this.setOpaque(false);
        }

        @Override
        public void paintComponent(Graphics g) {
            if (image == null) {
                return;
            }
            
            int cx = this.getWidth() / 2;
            int cy = this.getHeight() / 2;

            //Scale image to fill view size
            float scaleFactor = Math.min(this.getWidth(), this.getHeight()) / (float)Math.max(image.getWidth(), image.getHeight());

            
            g.drawImage(image, 0, 0, (int)(image.getWidth() * scaleFactor), (int)(image.getHeight() * scaleFactor), null);
        }

    }

    public SiteDetailViewer() {
        super();

        //Vertical layout with padding
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private boolean hidden = true;

    public void SetDetails(String siteId, DBmanager connection) {
        hidden = true;
        this.removeAll();
        if (siteId == null) {
            return;
        }

        //Add the image (square)
        ImgViewer img = new ImgViewer();
        img.setPreferredSize(new Dimension(80, 80));
        this.add(img);

        //The name field
        JLabel name = new JLabel();
        name.setForeground(Color.WHITE);
        name.setFont(name.getFont().deriveFont(32)); //Size 24 font
        this.add(name);

        //The url field
        JLabel url = new JLabel();
        url.setForeground(Color.WHITE);
        this.add(url);

        //The accounts
        //this.add(new JSeparator());
        JLabel ac = new JLabel("Accounts:");
        ac.setForeground(Color.WHITE);
        this.add(ac);

        try{
            ResultSet images = connection.GetSiteImage(siteId);
            //SELECT format, data FROM images WHERE wid = ?
            if (images.next()) {
                //Decode image
            } else {
                //Use "unknown" graphic
                img.image = Resources.resources.unknownIcon;
            }
        }catch(Exception e){img.image = Resources.resources.unknownIcon;}
        img.repaint();
        
        try{
            ResultSet details = connection.GetSiteDetails(siteId);
            //SELECT * FROM websites WHERE id = ?
            if (details.next()) {
                name.setText(details.getString("name"));
                url.setText(details.getString("url"));
            }
        }catch(Exception e){}

        try{
            ResultSet accounts = connection.GetSiteAccounts(siteId);
            //SELECT * FROM accounts WHERE id IN (SELECT aid FROM website_accounts WHERE wid = ?)
            //SELECT * FROM accounts LEFT JOIN website_accounts ON accounts.id = website_accounts.aid WHERE wid = ?
            while (accounts.next()) {
                JPanel accountPanel = new JPanel();
                accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.PAGE_AXIS));

                String aid = accounts.getString("id");

                JLabel aname = new JLabel(accounts.getString("username"));
                accountPanel.add(aname);
                JLabel aemail = new JLabel(accounts.getString("email"));
                accountPanel.add(aemail);

                String password = accounts.getString("password");
                String hiddenpass = repeat("*", password.length());
                JPanel sideBySide = new JPanel();
                JLabel copy = new JLabel("c");
                copy.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        //Copy password to clipboard
                        StringSelection sel = new StringSelection(password);
                        Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clp.setContents(sel, null);
                    }
                });
                JLabel apass = new JLabel(hiddenpass);

                JLabel show = new JLabel("v");
                show.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        hidden = !hidden;
                        if (hidden == false) {
                            apass.setText(password);
                        } else {
                            apass.setText(hiddenpass);
                        }
                    }
                });

                sideBySide.add(copy);
                sideBySide.add(show);
                sideBySide.add(apass);
            }
        }catch(Exception e){}
    }

    private String repeat(String base, int amount){
        StringBuilder b = new StringBuilder();
        for(int i = 0; i < amount; i++){
            b.append(base);
        }
        return b.toString();
    }
    
}

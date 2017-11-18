/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import passwordmanager.actions.Func;
import passwordmanager.popup.NewAccountBuilder;
import passwordmanager.popup.NewSiteBuilder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import passwordmanager.layouts.VerticalFlowLayout;
import passwordmanager.layouts.WrapLayout;
import passwordmanager.popup.NoteViewer;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class SiteDetailViewer extends JPanel {

    public Func<String> onCreateAccount;
    public passwordmanager.actions.Action onDelete;
    
    private GridBagConstraints c;
    
    public SiteDetailViewer() {
        super();

        //Vertical layout with padding
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;

        
        BoxLayout bt = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(new BorderLayout());
    }

    public void SetDetails(String siteId, DbConnection connection) {
        this.removeAll();
        if (siteId == null) {
            return;
        }
        
        JPanel container = new JPanel();
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        container.setOpaque(false);
        container.setLayout(new GridBagLayout());
        
        //Add the site options
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        JLabel editSite = new JLabel(new ImageIcon(Resources.resources.editIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        editSite.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                NewSiteBuilder nsb = new NewSiteBuilder(siteId, connection);
                nsb.setVisible(true);
                View.CenterFrame(nsb);
            }
        });
        JLabel deleteSite = new JLabel(new ImageIcon(Resources.resources.trashIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        deleteSite.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                int i = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete this collection and all its' associated accounts?");
                if(i == JOptionPane.YES_OPTION){
                    connection.DeleteSite(siteId);
                    if(onDelete != null)
                        onDelete.Invoke();
                }
            }
        });
        panel.add(editSite);
        panel.add(Box.createHorizontalGlue());
        panel.add(deleteSite);
        container.add(panel, c);
        c.gridy++;
        
        //Add the image (square)
        ImgViewer img = new ImgViewer();
        img.setPreferredSize(new Dimension(20, 20));
        c.ipady = 80;
        container.add(img, c);
        c.gridy++;
        c.ipady = 0;
        
        JPanel detail = new JPanel();
        detail.setOpaque(false);
        detail.setLayout(new GridLayout(0,1));
        
        //The name field
        JLabel name = new JLabel();
        name.setForeground(Color.WHITE);
        detail.add(name);

        //The url field
        JLabel url = new JLabel();
        url.setForeground(Color.WHITE);
        detail.add(url);

        //The accounts
        detail.add(new JSeparator());
        JPanel acc = new JPanel();
        acc.setOpaque(false);
        acc.setLayout(new BoxLayout(acc, BoxLayout.X_AXIS));
        JLabel ac = new JLabel("Accounts:");
        ac.setForeground(Color.WHITE);
        acc.add(ac);
        acc.add(Box.createHorizontalGlue());
        JLabel newAccount = new JLabel(new ImageIcon(Resources.resources.addIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        newAccount.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                if(onCreateAccount != null)
                    onCreateAccount.Invoke(siteId);
            }
        });
        acc.add(newAccount);
        detail.add(acc);

        container.add(detail, c);
        c.gridy++;
        
        JPanel accountCapsule = new JPanel(new BorderLayout());
        JPanel accountList = new JPanel(new GridLayout(0,1));
        accountList.setOpaque(false);
        this.add(container, BorderLayout.NORTH);
        accountCapsule.add(accountList, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(accountCapsule);
        scroll.getVerticalScrollBar().setUnitIncrement(32);
        this.add(scroll, BorderLayout.CENTER);
        c.gridy++;
        
        try{
            BufferedImage images = (BufferedImage)connection.GetSiteImage(siteId);
            //SELECT format, data FROM images WHERE wid = ?
            if (images != null) {
                //Decode image
                img.image = images;
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
                //accountPanel.setPreferredSize(new Dimension(140, 120));
                accountPanel.setBorder(new LineBorder(Color.black, 1, false));
                accountPanel.setLayout(new GridLayout(0,1));

                String aid = accounts.getString("id");
                
                JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                namePanel.add(new JLabel("Username:"));
                JTextField aname = new JTextField(accounts.getString("username"));
                JLabel copyn = new JLabel(new ImageIcon(Resources.resources.copyIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                copyn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        //Copy password to clipboard
                        StringSelection sel = new StringSelection(aname.getText());
                        Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clp.setContents(sel, null);
                    }
                });
                namePanel.add(copyn);
                aname.setEditable(false);
                aname.setAlignmentX(Component.LEFT_ALIGNMENT);
                accountPanel.add(namePanel);
                accountPanel.add(aname);
                
                
                JPanel mailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mailPanel.add(new JLabel("Email:"));
                JTextField aemail = new JTextField(accounts.getString("email"));
                JLabel copym = new JLabel(new ImageIcon(Resources.resources.copyIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                copym.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        //Copy password to clipboard
                        StringSelection sel = new StringSelection(aemail.getText());
                        Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clp.setContents(sel, null);
                    }
                });
                mailPanel.add(copym);
                aemail.setEditable(false);
                aemail.setAlignmentX(Component.LEFT_ALIGNMENT);
                accountPanel.add(mailPanel);
                accountPanel.add(aemail);

                String password = accounts.getString("password");
                String hiddenpass = repeat("*", password.length());
                JLabel copy = new JLabel(new ImageIcon(Resources.resources.copyIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                copy.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        //Copy password to clipboard
                        StringSelection sel = new StringSelection(password);
                        Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clp.setContents(sel, null);
                    }
                });
                
                JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JTextField apass = new JTextField(hiddenpass);
                apass.setEditable(false);

                JLabel show = new JLabel(new ImageIcon(Resources.resources.showIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                show.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        boolean hidden = !apass.getText().equals(hiddenpass);
                        if (hidden == false) {
                            apass.setText(password);
                        } else {
                            apass.setText(hiddenpass);
                        }
                    }
                });

                passPanel.add(new JLabel("Password:"));
                passPanel.add(show);
                passPanel.add(copy);
                accountPanel.add(passPanel);
                accountPanel.add(apass);
                
                JPanel accountOptions = new JPanel();
                accountOptions.setBorder(new EmptyBorder(5,5,5,5));
                accountOptions.setLayout(new BoxLayout(accountOptions, BoxLayout.X_AXIS));
                JLabel edit = new JLabel(new ImageIcon(Resources.resources.editIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                edit.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        NewAccountBuilder nab = new NewAccountBuilder(aid, aname.getText(), aemail.getText(), password, "-1", connection);
                        nab.setVisible(true);
                        View.CenterFrame(nab);
                    }
                });
                JLabel notes = new JLabel(new ImageIcon(Resources.resources.noteIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                notes.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        NoteViewer v = new NoteViewer(aid, connection);
                        v.setVisible(true);
                        View.CenterFrame(v);
                    }
                });
                JLabel delete = new JLabel(new ImageIcon(Resources.resources.trashIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                delete.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent evt){
                        int i = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete this account?");
                        if(i == JOptionPane.YES_OPTION){
                            connection.DeleteAccount(aid);
                            SetDetails(siteId, connection);
                        }
                    }
                });
                accountOptions.add(edit);
                accountOptions.add(Box.createHorizontalStrut(5));
                accountOptions.add(notes);
                accountOptions.add(Box.createHorizontalGlue());
                accountOptions.add(delete);
                accountPanel.add(accountOptions);
                
                accountList.add(accountPanel);
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

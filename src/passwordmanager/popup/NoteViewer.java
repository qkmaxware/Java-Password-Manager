/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import passwordmanager.Resources;
import passwordmanager.layouts.WrapLayout;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class NoteViewer extends JFrame{
    
    private DbConnection db;
    private String accountid;
    private JPanel contents;
    
    private static Color bg = new Color(201, 45, 39);
    private Color fg = new Color(57,74,84);
    
    public NoteViewer(String accountid, DbConnection db){
        super();
        
        this.accountid = accountid;
        this.db = db;
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Account Notes");
        this.setSize(480, 320);
        contents = new JPanel();
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(bg);
        contents.setLayout(new WrapLayout());
        this.setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);
        this.add(contents, BorderLayout.CENTER);
        
        
        
        JLabel edit = new JLabel(new ImageIcon(Resources.resources.editIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextArea tex = new JTextArea();
                tex.setPreferredSize(new Dimension(120, 120));
                int s = JOptionPane.showConfirmDialog(null,new JScrollPane(tex),"New Note", JOptionPane.OK_CANCEL_OPTION);
                if(db.IsConnected() && s == JOptionPane.OK_OPTION){
                    db.CreateNote(accountid, tex.getText());
                    Refresh();
                }
            }
        });
        header.add(edit);
        
        Refresh();
    }
    
    void Refresh(){
        contents.removeAll();
        if(!db.IsConnected())
            return;
            
        ResultSet notes = db.GetNotes(accountid);
        if(notes != null){
            try{
                while(notes.next()){
                    String note = notes.getString("note");
                    String id = notes.getString("id");
                    
                    JPanel notePanel = new JPanel(new BorderLayout());
                    notePanel.setMaximumSize(new Dimension(300,300));
                    notePanel.setBorder(new LineBorder(Color.black, 2, true));
                    
                    JPanel center = new JPanel();
                    JTextArea a = new JTextArea(note);
                    a.setBorder(new EmptyBorder(5,5,5,5));
                    a.setEditable(false);
                    center.add(new JScrollPane(a));
                    notePanel.add(center, BorderLayout.CENTER);
                    
                    JLabel delete = new JLabel(new ImageIcon(Resources.resources.trashIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                    delete.addMouseListener(new MouseAdapter(){
                        @Override
                        public void mouseClicked(MouseEvent evt){
                            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this note?");
                            if(n == JOptionPane.YES_OPTION){
                                db.DeleteNote(id);
                                Refresh();
                            }
                        }
                    });
                    
                    JPanel south = new JPanel();
                    south.setBackground(fg);
                    south.add(delete);
                    notePanel.add(south, BorderLayout.SOUTH);
                    
                    contents.add(notePanel);
                }
            }catch(Exception e){}
            this.invalidate();
            this.revalidate();
            this.repaint();
        }
    }
}

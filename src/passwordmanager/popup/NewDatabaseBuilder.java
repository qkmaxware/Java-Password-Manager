/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.popup;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import passwordmanager.actions.Func;

/**
 *
 * @author Colin Halseth
 */
public class NewDatabaseBuilder extends JFrame{
    
    public NewDatabaseBuilder(Func<String> onCreate){
        super();
        
        this.setTitle("Create Database");
        this.setSize(300, 150);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(10,10,10,10));
        content.setLayout(new GridLayout(0,1));

        JTextField dbname = new JTextField();
        dbname.setPreferredSize(new Dimension(150,24));
        content.add(new JLabel("Database Name:"));
        content.add(dbname);
        
        JButton button = new JButton("Create");
        button.addActionListener((e) -> {
            if(onCreate != null)
                onCreate.Invoke(dbname.getText());
            dispose();
        });
        content.add(button);
        
        this.add(content);
    }
    
}

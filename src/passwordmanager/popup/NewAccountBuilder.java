/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.popup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import passwordmanager.actions.Action;
import passwordmanager.utils.password.PasswordGenerator;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class NewAccountBuilder extends JFrame {
    
    public Action onCreate;
    
    private static PasswordGenerator genPass = new PasswordGenerator();
    private boolean editMode = false;
    private String aId;
    
    private JTextField dbname;
    private JTextField dbemail;
    private JTextField dbpass;
    private JButton button;
    
    public NewAccountBuilder(String accountId, String username, String email, String password, String siteId, DbConnection connection){
        this(siteId, connection);
        editMode = true;
        aId = accountId;
        this.setTitle("Modify Account");
        button.setText("Update");
        dbname.setText(username);
        dbemail.setText(email);
        dbpass.setText(password);
    }
    
    public NewAccountBuilder(String siteId, DbConnection connection){
        super();
        
        this.setTitle("New Account");
        this.setSize(380, 240);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel total = new JPanel();
        total.setLayout(new GridLayout(1,2));
        
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(10,10,10,10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dbname = new JTextField();
        dbname.setPreferredSize(new Dimension(150,24));
        p1.add(new JLabel("Username:"));
        p1.add(dbname);
        content.add(p1);
        
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dbemail = new JTextField();
        dbemail.setPreferredSize(new Dimension(150,24));
        p2.add(new JLabel("Email:"));
        p2.add(dbemail);
        content.add(p2);
        
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dbpass = new JTextField();
        dbpass.setPreferredSize(new Dimension(150,24));
        p3.add(new JLabel("Password:"));
        p3.add(dbpass);
        content.add(p3);
        
        button = new JButton("Create");
        button.addActionListener((e) -> {
            if(connection.IsConnected()){
                if(!editMode){
                    connection.CreateAccount(siteId, dbname.getText(), dbemail.getText(), dbpass.getText());
                }else{
                    connection.UpdateAccount(aId, dbname.getText(), dbemail.getText(), dbpass.getText());
                }
            }
            
            if(onCreate != null)
                onCreate.Invoke();
            
            dispose();
        });
        content.add(button);
        
        JPanel generator = new JPanel();
        generator.setBorder(new EmptyBorder(10,10,10,10));
        generator.setLayout(new BoxLayout(generator, BoxLayout.Y_AXIS));
        Color genBg = new Color(198,188,165);
        generator.setBackground(genBg);
        generator.add(new JLabel("Password Generator"));
        
        JFormattedTextField size = new JFormattedTextField(NumberFormat.getIntegerInstance());
        size.setValue((long)16); size.setPreferredSize(new Dimension(100, 32));
        generator.add(size);
        
        JCheckBox lowerCase = new JCheckBox("Use Lower Case");
        JCheckBox useSentance = new JCheckBox("Generate Sentence");
        useSentance.addChangeListener((evt) -> {
            if(useSentance.isSelected()){
                lowerCase.setSelected(true);
            }
        });
        lowerCase.addChangeListener((evt) -> {
            if(useSentance.isSelected() && !lowerCase.isSelected()){
                lowerCase.setSelected(true);
            }
        });
        useSentance.setSelected(false);
        useSentance.setBackground(genBg);
        generator.add(useSentance);
         
        lowerCase.setSelected(true); lowerCase.setBackground(genBg);
        generator.add(lowerCase);
        
        JCheckBox upperCase = new JCheckBox("Use Upper Case");
        upperCase.setSelected(true); upperCase.setBackground(genBg);
        generator.add(upperCase);
        
        JCheckBox digits = new JCheckBox("Use Digits");
        digits.setSelected(true); digits.setBackground(genBg);
        generator.add(digits);
        
        JCheckBox special = new JCheckBox("Use Special Chars");
        special.setSelected(false); special.setBackground(genBg);
        generator.add(special);
        
        generator.add(Box.createVerticalBox());
        
        JButton gen = new JButton("Generate");
        gen.addActionListener((evt) -> {
            String s;
            if(useSentance.isSelected())
                s = genPass.GeneratePassphrase((int)((long)size.getValue()), upperCase.isSelected(), digits.isSelected(), special.isSelected());
            else
                s = genPass.GeneratePassword((int)((long)size.getValue()), lowerCase.isSelected(), upperCase.isSelected(), digits.isSelected(), special.isSelected());
            dbpass.setText(s);
        });
        generator.add(gen);
        
        total.add(generator);
        total.add(content);
        this.add(total);
    }
    
}

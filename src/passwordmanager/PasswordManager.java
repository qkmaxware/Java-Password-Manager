/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import javax.swing.JFrame;

/**
 *
 * @author Colin Halseth
 */
public class PasswordManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DBmanager database = new DBmanager("pwd.db");
        //database.CreateSite("Youtube", "http://youtube.com");
        //database.CreateSite("Github", "http://github.com");
        //database.CreateAccount(1, "Colin", "Test@test.com", "password");
        
        View view = new View(database);
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setSize(640, 480);
        view.setVisible(true);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        //DBmanager database = new DBmanager("data/default.db");
        //database.CreateSite("Youtube", "http://youtube.com");
        //database.CreateSite("Github", "http://github.com");
        //database.CreateAccount("1", "Colin", "Test@test.com", "password");
        
        //Load all local databases
        File folder = new File("data");
        if(!folder.exists())
            folder.mkdirs();
        File[] dbs = folder.listFiles();
        ArrayList<DBmanager> managers = new ArrayList<DBmanager>();
        for(int i = 0; i < dbs.length; i++){
            File f = dbs[i];
            if(f.isDirectory())
                continue;
            int last = f.getName().lastIndexOf(".");
            
            if(last > -1){
                String ext = f.getName().substring(last);
                if(ext.equals(".db")){
                    managers.add(new DBmanager(f.getAbsolutePath()));
                }
            }
        }
        
        DBmanager[] connections = managers.toArray(new DBmanager[managers.size()]);
        
        View view = new View(connections);
        view.setTitle("My Passwords");
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setSize(640, 480);
        view.setVisible(true);
    }
    
}

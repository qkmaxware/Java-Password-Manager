/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import passwordmanager.connections.SqliteConnection;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JFrame;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class PasswordManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Load all local databases
        File folder = new File("data");
        if(!folder.exists())
            folder.mkdirs();
        File[] dbs = folder.listFiles();
        ArrayList<DbConnection> managers = new ArrayList<DbConnection>();
        for(int i = 0; i < dbs.length; i++){
            File f = dbs[i];
            if(f.isDirectory())
                continue;
            int last = f.getName().lastIndexOf(".");
            
            if(last > -1){
                String ext = f.getName().substring(last);
                if(ext.equals(".db")){
                    managers.add(new SqliteConnection(f.getAbsolutePath()));
                }
            }
        }
        
        DbConnection[] connections = managers.toArray(new DbConnection[managers.size()]);
        
        //Load in config file and set it up to auto serialize
        String configLocation = "config.xml";
        Config config = Config.Deserialize(configLocation);
        config.OnChange = () -> {
            Config.Serialize(configLocation, config);
        };
        
        View view = new View(config);
        view.setTitle("My Passwords");
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setSize(config.GetWidth(), config.GetHeight());
        view.setVisible(true);
    }
    
}

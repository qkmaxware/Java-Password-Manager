/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Colin Halseth
 */
public class PasswordManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Load in config file and set it up to auto serialize
        String configLocation = "config.xml";
        Config config = Config.Deserialize(configLocation);
        config.OnChange = () -> {
            Config.Serialize(configLocation, config);
        };
        
        //Create the view (program) on the swing thread
        SwingUtilities.invokeLater(() -> {
            View view = new View(config);
            view.setTitle("My Passwords");
            view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            view.setSize(config.GetWidth(), config.GetHeight());
            view.setVisible(true);
        });
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Colin Halseth
 */
public class Resources {
    
    public static Resources resources = new Resources();
    
    public BufferedImage searchIcon;
    public BufferedImage copyIcon;
    public BufferedImage showIcon;
    public BufferedImage unknownIcon;
    
    public Resources(){
        try{
        
            searchIcon = ImageIO.read(get("/search.png"));
            copyIcon = ImageIO.read(get("/copy.png")); 
            showIcon = ImageIO.read(get("/show.png"));
            unknownIcon = ImageIO.read(get("/unknown.png"));
            
        }catch(Exception e){
            throw new RuntimeException("Missing or failed to load resource file.");
        }
    }
    
    private URL get(String resource){
        URL url = Resources.class.getResource(resource);
        return url;
    }
    
}

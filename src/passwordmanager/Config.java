/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import passwordmanager.actions.Action;
import passwordmanager.connections.DbConnection;
import passwordmanager.connections.HttpConnection;
import passwordmanager.connections.PostgresConnection;
import passwordmanager.connections.SqliteConnection;

/**
 *
 * @author Colin Halseth
 */
public class Config {
    
    public static class ConnectionConfig{
        public String driver;
        public String connectionString;
        public String user;
        public String password;
        public boolean requiresPassword;
        
        public DbConnection DeriveConnection(){
            switch(driver){
                case "sqlite":
                    return new SqliteConnection(connectionString);
                case "postgres":
                    return new PostgresConnection(connectionString, user, password);
                case "http":
                    return new HttpConnection(); //TODO
            }
            return null;
        }
    }
    
    private Dimension defaultSize = new Dimension(640, 480);
    private ArrayList<ConnectionConfig> connections = new ArrayList<ConnectionConfig>();
    private String location;
    
    public Action OnChange;
    
    public int GetWidth(){
        return defaultSize.width;
    }
    
    public int GetHeight(){
        return defaultSize.height;
    }
    
    public ConnectionConfig GetConnection(int i){
        return connections.get(i);
    }
    
    public DbConnection AddConnection(ConnectionConfig e){
        connections.add(e);
        if(OnChange != null)
            OnChange.Invoke();
        return e.DeriveConnection();
    }
    
    public void DeleteConnection(int i){
        connections.remove(i);
        if(OnChange != null)
            OnChange.Invoke();
    }
    
    public DbConnection[] GetConnections(){
        DbConnection[] d = new DbConnection[this.connections.size()];
        int i = 0;
        for(ConnectionConfig c : connections){
            d[i] = c.DeriveConnection();
            i++;
        }
        return d;
    }
    
    public int CountConnections(){
        return connections.size();
    }
    
    public void MarkDirty(){
        if(OnChange != null)
            OnChange.Invoke();
    }
    
    public static void Serialize(String f, Config c){
        try{
            String s = c.ToXml();
            File file = new File(f);
            FileWriter writer = new FileWriter(file);
            writer.write(s);
            writer.flush();
            writer.close();
        }catch(Exception e){}
    }
    
    public static Config Deserialize(String file){
        File f = new File(file);
        if(!f.exists()){
            Config g = new Config();
            Config.Serialize(file, g);
            return g;
        }
        
        try {
            String s = String.join("\n", Files.readAllLines(Paths.get(file), Charset.defaultCharset()));
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));
            
            Config g = new Config();
            
            //Get saved size
            Element size = (Element)d.getElementsByTagName("size").item(0);
            Node width = size.getElementsByTagName("width").item(0);
            Node height = size.getElementsByTagName("height").item(0);
            g.defaultSize = new Dimension(Integer.parseInt(width.getTextContent()), Integer.parseInt(height.getTextContent()));
            
            //Get saved connections
            Element connections = (Element)d.getElementsByTagName("connections").item(0);
            NodeList allConnections = connections.getElementsByTagName("connection");
            for(int i = 0; i < allConnections.getLength(); i++){
                Element connection = (Element)allConnections.item(i);
                ConnectionConfig conn = new ConnectionConfig();
                g.connections.add(conn);
                
                conn.driver = connection.getElementsByTagName("driver").item(0).getTextContent();
                conn.connectionString = connection.getElementsByTagName("ref").item(0).getTextContent();
                conn.user = connection.getElementsByTagName("user").item(0).getTextContent();
                conn.password = connection.getElementsByTagName("password").item(0).getTextContent();
                conn.requiresPassword = Boolean.parseBoolean(connection.getElementsByTagName("usePassword").item(0).getTextContent());
            }
            
            return g;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public String ToXml(){
        StringBuilder b = new StringBuilder();
        
        b.append("<config>\r\n");
        
        b.append("\t<size>\r\n");
        b.append("\t\t<width>"); b.append(defaultSize.width); b.append("</width>\r\n");
        b.append("\t\t<height>"); b.append(defaultSize.height); b.append("</height>\r\n");
        b.append("\t</size>\r\n");
        
        b.append("\t<connections>\r\n");
        
        for(ConnectionConfig conf : connections){
            b.append("\t\t<connection>\r\n");
            b.append("\t\t\t<driver>"); b.append(conf.driver); b.append("</driver>\r\n");
            b.append("\t\t\t<ref>"); b.append(conf.connectionString); b.append("</ref>\r\n");
            b.append("\t\t\t<user>"); b.append(conf.user); b.append("</user>\r\n");
            b.append("\t\t\t<password>"); b.append(conf.password); b.append("</password>\r\n");
            b.append("\t\t\t<usePassword>"); b.append(conf.requiresPassword); b.append("</usePassword>\r\n");
            b.append("\t\t</connection>\r\n");
        }
        
        b.append("\t</connections>\r\n");
        
        b.append("</config>");
        return b.toString();
    }
    
    
}

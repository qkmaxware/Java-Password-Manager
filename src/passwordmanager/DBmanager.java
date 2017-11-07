/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.sql.*;
import java.util.Calendar;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Colin Halseth
 */
public class DBmanager {
    
    private String connectionString = "jdbc:sqlite:";
    private String databaseName = "pwd.db";
    
    private java.util.Calendar calendar = java.util.Calendar.getInstance();
    SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Connection conn;
    private DatabaseMetaData meta;
    
    public DBmanager(String database){
        databaseName = database;
        
        try{
            conn = DriverManager.getConnection(connectionString + databaseName);
            if(conn != null){
                meta = conn.getMetaData();
                CreateTables();
            }else{
                throw new Exception("Failed to establish connection to the database");
            }
        }catch(Exception e){
            System.out.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public String GetName(){
        return this.databaseName;
    }
    
    private void CreateTables() throws Exception {
        String columns = "{{def}}";
        String name = "{{name}}";
        String createstm = "CREATE TABLE IF NOT EXISTS "+name+" ("+columns+");";
        
        //Create websites list
        Statement cWebsites = conn.createStatement();
        cWebsites.execute(createstm.replace(name, "websites").replace(columns, 
                "id integer PRIMARY KEY autoincrement, name text NOT NULL, url text NOT NULL, keywords text NOT NULL, created text NOT NULL"
        ));
        
        //Create accounts list
        Statement cAccounts = conn.createStatement();
        cAccounts.execute(createstm.replace(name, "accounts").replace(columns, 
                "id integer PRIMARY KEY autoincrement,"+
                "username text NOT NULL,"+ 
                "email text NOT NULL,"+
                "password text NOT NULL,"+
                "created text NOT NULL"
        ));
        
        //Create link table between websites and accounts
        Statement cAW = conn.createStatement();
        cAW.execute(createstm.replace(name, "website_accounts").replace(columns, 
                "wid integer,"+
                "aid integer,"+
                "PRIMARY KEY (wid, aid),"+
                "FOREIGN KEY (wid) REFERENCES websites(id),"+
                "FOREIGN KEY (aid) REFERENCES accounts(id)"
        ));
        
        //2 Factor authentication codes
        Statement c2factor = conn.createStatement();
        c2factor.execute(createstm.replace(name, "sitecodes").replace(columns,
                "id integer PRIMARY KEY autoincrement,"+
                "wid integer NOT NULL,"+
                "code text NOT NULL,"+
                "FOREIGN KEY (wid) REFERENCES websites(id)"
        ));
        
        //Icons
        Statement cImages = conn.createStatement();
        cImages.execute(createstm.replace(name, "icons").replace(columns, 
                "id integer PRIMARY KEY autoincrement,"+
                "wid integer NOT NULL,"+
                "format text NOT NULL,"+
                "data text NOT NULL,"+
                "FOREIGN KEY (wid) REFERENCES websites(id)"
        ));
    }
    
    public ResultSet GetAllSites(){
        try{
            PreparedStatement c = conn.prepareStatement(
                "SELECT * FROM websites;"
            );
            
            c.execute();
            ResultSet res = c.getResultSet();
            return res;
            
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public ResultSet SearchSites(String term){
        try{
            PreparedStatement c = conn.prepareStatement(
                "SELECT *\n" +
                "FROM websites\n" +
                "WHERE \n" +
                "    lower(name || ',' || url || ', ' || ifnull(keywords,'')) \n" +
                "    LIKE lower(?);"
            );
            
            c.setString(1, "%"+term+"%");
            
            c.execute();
            
            ResultSet res = c.getResultSet();
            return res;
            
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void CreateAccount(int siteId, String username, String email, String password){
        try{
            PreparedStatement c = conn.prepareStatement(
                "INSERT INTO accounts (username, email, password, created) VALUES (?,?,?,datetime());",
                Statement.RETURN_GENERATED_KEYS
            );
            
            c.setString(1, username);
            c.setString(2, email);
            c.setString(3, password);
           
            c.executeUpdate();
            long l = c.getGeneratedKeys().getLong(1);

            PreparedStatement c2 = conn.prepareStatement("INSERT INTO website_accounts (wid, aid) VALUES (?,?);");
            c2.setLong(1, siteId);
            c2.setLong(2, l);
           
            c2.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void CreateSite(String site, String url, String... keywords){
        try{
            PreparedStatement c = conn.prepareStatement(
                    "INSERT INTO websites (name, url, keywords, created) VALUES (?,?,?,datetime());"
            );
            
            c.setString(1, site);
            c.setString(2, url);
            c.setString(3, String.join(",", keywords));

            c.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public ResultSet GetSiteImage(String siteid){
        try{
            PreparedStatement c = conn.prepareStatement(
                    "SELECT format, data FROM icons WHERE wid = ?;"
            );
            
            c.setString(1, siteid);

            c.execute();
            
            return c.getResultSet();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public ResultSet GetSiteDetails(String siteid){
        try{
            PreparedStatement c = conn.prepareStatement(
                    "SELECT * FROM websites WHERE id = ?;"
            );
            
            c.setString(1, siteid);

            c.execute();
            
            return c.getResultSet();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public ResultSet GetSiteAccounts(String siteid){
        try{
            PreparedStatement c = conn.prepareStatement(
                "SELECT * FROM accounts LEFT JOIN website_accounts ON accounts.id = website_accounts.aid WHERE wid = ?;"
            );
            
            c.setString(1, siteid);

            c.execute();
            
            return c.getResultSet();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private ResultSet Query(String query, Object... params){
         try{
            PreparedStatement c = conn.prepareStatement(
                query
            );
            
            for(int i = 0 ; i < params.length ; i++){
                Object o = params[i];
                if(o instanceof Integer){
                    c.setInt(i + 1, (Integer)o);
                }else if(o instanceof Long){
                    c.setLong(i + 1, (Long)o);
                }else if(o instanceof Float){
                    c.setFloat(i + 1, (Float)o);
                }else if(o instanceof Double){
                    c.setDouble(i + 1, (Double)o);
                }else if(o instanceof Byte){
                    c.setByte(i + 1, (Byte)o);
                }else{
                    c.setString(i + 1, o.toString());
                }
            }

            c.execute();
            
            return c.getResultSet();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}

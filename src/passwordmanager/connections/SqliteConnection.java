/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.connections;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import passwordmanager.connections.DbConnection;

/**
 *
 * @author Colin Halseth
 */
public class SqliteConnection implements DbConnection {
    
    private String connectionString = "jdbc:sqlite:";
    private String databaseName = "default.db";
    private String prettyName;
    
    private java.util.Calendar calendar = java.util.Calendar.getInstance();
    SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Connection conn;
    private DatabaseMetaData meta;
    
    public SqliteConnection(String database){
        databaseName = database;
        prettyName = Paths.get(database).getFileName().toString();
        
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
        return this.prettyName;
    }
    
    private void CreateTables() throws Exception {
        String columns = "{{def}}";
        String name = "{{name}}";
        String createstm = "CREATE TABLE IF NOT EXISTS "+name+" ("+columns+");";
        
        //Create websites list
        Statement cWebsites = conn.createStatement();
        cWebsites.execute(createstm.replace(name, "collections").replace(columns, 
                "id integer PRIMARY KEY autoincrement, name text NOT NULL, url text NOT NULL, keywords text NOT NULL, created text DEFAULT (datetime()) NOT NULL"
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
        cAW.execute(createstm.replace(name, "collection_accounts").replace(columns, 
                "wid integer,"+
                "aid integer,"+
                "PRIMARY KEY (wid, aid)"
        ));
        
        //2 Factor authentication codes
        Statement c2factor = conn.createStatement();
        c2factor.execute(createstm.replace(name, "sitecodes").replace(columns,
                "id integer PRIMARY KEY autoincrement,"+
                "wid integer NOT NULL,"+
                "code text NOT NULL,"+
                "FOREIGN KEY (wid) REFERENCES collections(id)"
        ));
        
        //Icons
        Statement cImages = conn.createStatement();
        cImages.execute(createstm.replace(name, "icons").replace(columns, 
                "id integer PRIMARY KEY autoincrement,"+
                "wid integer NOT NULL,"+
                "data text NOT NULL,"+
                "FOREIGN KEY (wid) REFERENCES collections(id)"
        ));
        
        //Groups
        Statement cGroups = conn.createStatement();
        cGroups.execute(createstm.replace(name, "categories").replace(columns, 
                "id integer PRIMARY KEY autoincrement,"+
                "name text NOT NULL,"+
                "created text DEFAULT (datetime()) NOT NULL"
        ));
        
        //Group members
        Statement cGroupLink = conn.createStatement();
        cGroupLink.execute(createstm.replace(name, "category_collections").replace(columns, 
                "gid integer NOT NULL,"+
                "wid integer NOT NULL,"+
                "PRIMARY KEY (gid, wid)"
        ));
        
        //Create notes
        Statement cNotes = conn.createStatement();
        cNotes.execute(createstm.replace(name, "notes").replace(columns, 
                "id integer PRIMARY KEY autoincrement,"+
                "aid integer NOT NULL,"+
                "created text DEFAULT (datetime()) NOT NULL,"+
                "note text"
        ));
    }
    
    public ResultSet GetAllSites(){
        try{
            return Query("SELECT * FROM collections ORDER BY id;");
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public ResultSet SearchSites(String term){
        try{
            String s = (
                "SELECT *\n" +
                "FROM collections\n" +
                "WHERE \n" +
                "    lower(name) LIKE lower(?) \n"
              + "    OR lower(url) LIKE lower(?) \n"
              + "    OR lower(ifnull(keywords,'')) LIKE lower(?) \n"
              + "ORDER BY id;"
            );
            String searchterm = "%"+term+"%";
            return Query(s, searchterm,searchterm,searchterm);
            
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public long CreateAccount(String siteId, String username, String email, String password){
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

            PreparedStatement c2 = conn.prepareStatement("INSERT INTO collection_accounts (wid, aid) VALUES (?,?);");
            c2.setString(1, siteId);
            c2.setLong(2, l);
           
            c2.executeUpdate();
            return l;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void UpdateAccount(String accountId, String username, String email, String password){
        try{
            Query("UPDATE accounts SET username = ?, email = ?, password = ? WHERE id = ?;", username, email, password, accountId);
        }catch(Exception e){
            
        }
    }
    
    public void DeleteAccount(String accountId){
        try{
            Query("DELETE FROM collection_accounts WHERE aid = ?;", accountId);
            Query("DELETE FROM accounts WHERE id = ?", accountId);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void CreateNote(String account, String contents){
        try{
            Query("INSERT INTO notes (aid,note) VALUES (?,?)", account, contents);
        }catch(Exception e){}
    }
    
    public void DeleteNote(String noteId){
        try{
            Query("DELETE FROM notes WHERE id = ?", noteId);
        }catch(Exception e){}
    }
    
    public ResultSet GetNotes(String account){
        try{
            return Query("SELECT * FROM notes WHERE aid = ?", account);
        }catch(Exception e){
            return null;
        }
    }
    
    public void DeleteSite(String site){
        try{
            Query("DELETE FROM accounts WHERE id IN (SELECT aid FROM collection_accounts WHERE wid = ?);", site);
            Query("DELETE FROM collection_accounts WHERE wid = ?;", site);
            Query("DELETE FROM category_collections WHERE wid = ?;", site);
            Query("DELETE FROM collections WHERE id = ?;", site);
            
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void UpdateSite(String siteid, String name, String url, String... keywords){
        try{
            Query("UPDATE collections SET name = ?, url = ?, keywords = ? WHERE id = ?;", name, url, String.join(" ", keywords), siteid);
        }catch(Exception e){
            
        }
    }
    
    public long CreateSite(String site, String url, String... keywords){
        try{
            PreparedStatement stm = QueryAll("INSERT INTO collections (name, url, keywords, created) VALUES (?,?,?,datetime());", site, url, String.join(" ", keywords));
            long id = stm.getGeneratedKeys().getLong(1);
            return id;
        }catch(Exception e){
            return -1;
        }
    }
    
    public void SetSiteImage(String siteid, RenderedImage img){
        try{
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "png", os);
            String s = java.util.Base64.getEncoder().encodeToString(os.toByteArray());
            
            Query( "DELETE FROM icons WHERE wid = ?", siteid); //avoid duplicate entries
            Query( "INSERT INTO icons (wid, data) VALUES (?,?);", siteid, s);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public RenderedImage GetSiteImage(String siteid){
        try{
            ResultSet s = Query( "SELECT data FROM icons WHERE wid = ?;", siteid);
            if(s.next()){
                String v = s.getString("data");
                byte[] bytes = java.util.Base64.getDecoder().decode(v);
                return ImageIO.read(new ByteArrayInputStream(bytes));
            }
            return null;
            
        }catch(Exception e){
            return null;
        }
    }
    
    @Override
    public void DeleteSiteImage(String siteId) {
        try{
            Query( "DELETE FROM icons WHERE wid = ?;", siteId); 
        }catch(Exception e){}
    }
    
    public ResultSet GetSiteDetails(String siteid){
        try{
            return Query("SELECT * FROM collections WHERE id = ?;", siteid);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public ResultSet GetSiteAccounts(String siteid){
        try{
            return Query("SELECT * FROM accounts LEFT JOIN collection_accounts ON accounts.id = collection_accounts.aid WHERE wid = ?;", siteid);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private PreparedStatement QueryAll(String query, Object... params) throws Exception{
        //Always return generated keys
        PreparedStatement c = conn.prepareStatement(
            query,
            Statement.RETURN_GENERATED_KEYS
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
            }else if(o instanceof InputStream){
                c.setBlob(i + 1, (InputStream)o);
            }else if(o instanceof Blob){
                c.setBlob(i + 1, (Blob)o);
            }else{
                c.setString(i + 1, o.toString());
            }
        }

        c.execute();

        return c;
    }
    
    private ResultSet Query(String query, Object... params) throws Exception{
        PreparedStatement c = QueryAll(query, params);
        return c.getResultSet();
    }
}

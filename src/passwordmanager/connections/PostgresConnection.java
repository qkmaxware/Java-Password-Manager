/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.connections;

import java.awt.image.RenderedImage;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 *
 * @author Colin Halseth
 */
public class PostgresConnection implements DbConnection{
    
    private static String connectionBase = "jdbc:postgresql:";
    private String prettyName;
    private Connection conn;
    
    private boolean successfulConnect = true;
    
    public PostgresConnection(String url){
        this(url, null, null);
    }
    public PostgresConnection(String url, String username, String password){
        String empty = "";
        
        username = username.trim();
        password = password.trim();
        
        if(username.equals(empty))
            username = null;
        if(password.equals(empty))
            password = null;
        
        String connectionString = connectionBase + url;
        try{
            URI uri = new URI(url);
            prettyName = uri.getHost();
            if(username != null && password != null){
                conn = DriverManager.getConnection(connectionString, username, password);
            }else{
                conn = DriverManager.getConnection(connectionString);
            }
            
            
        }catch(Exception e){
            successfulConnect = false;
        }
    }
    
    @Override
    public String GetName() {
        return prettyName;
    }

    @Override
    public ResultSet GetSites(String... hiddenGroups) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet GetSiteDetails(String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet GetSiteAccounts(String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet SearchSites(String term, String... hiddenGroups) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long CreateSite(String siteName, String url, String... keywords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void DeleteSite(String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void UpdateSite(String siteId, String name, String url, String... keywords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetSiteImage(String siteId, RenderedImage img) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RenderedImage GetSiteImage(String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void DeleteSiteImage(String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long CreateAccount(String siteId, String username, String email, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void UpdateAccount(String accountId, String username, String email, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void DeleteAccount(String accountId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void CreateNote(String accountId, String contents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void DeleteNote(String noteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet GetNotes(String accountId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean IsConnected() {
        return successfulConnect;
    }

    @Override
    public boolean TestConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet GetCategories() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void CreateCategory(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void DeleteCategory(String categoryId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void AddSiteToCategory(String categoryId, String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void RemoveSiteFromCategory(String categoryId, String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet GetCategoriesForSite(String siteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

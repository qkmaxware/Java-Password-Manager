/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.connections;

import java.awt.image.RenderedImage;
import java.sql.ResultSet;

/**
 *
 * @author Colin Halseth
 */
public interface DbConnection {
 
    //General Stuff
    public String GetName();
    
    //Site Stuff
    public ResultSet GetAllSites();
    public ResultSet GetSiteDetails(String siteId);
    public ResultSet GetSiteAccounts(String siteId);
    public ResultSet SearchSites(String term);
    public long CreateSite(String siteName, String url, String... keywords);
    public void DeleteSite(String siteId);
    public void UpdateSite(String siteId, String name, String url, String... keywords);
    
    //Icon Stuff
    public void SetSiteImage(String siteId, RenderedImage img);
    public RenderedImage GetSiteImage(String siteId);
    public void DeleteSiteImage(String siteId);
    
    //Account Stuff
    public long CreateAccount(String siteId, String username, String email, String password);
    public void UpdateAccount(String accountId, String username, String email, String password);
    public void DeleteAccount(String accountId);
    
    //Account Notes
    public void CreateNote(String accountId, String contents);
    public void DeleteNote(String noteId);
    public ResultSet GetNotes(String accountId);
    
}

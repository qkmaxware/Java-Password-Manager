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
    public boolean IsConnected();
    public boolean TestConnection();
    
    //Site Stuff
    public ResultSet GetSites(String... hiddenGroups);
    public ResultSet SearchSites(String term, String... hiddenGroups);
    public ResultSet GetSiteDetails(String siteId);
    public ResultSet GetSiteAccounts(String siteId);
    public long CreateSite(String siteName, String url, String... keywords);
    public void DeleteSite(String siteId);
    public void UpdateSite(String siteId, String name, String url, String... keywords);
    
    //Category Stuff
    public ResultSet GetCategories();
    public ResultSet GetCategoriesForSite(String siteId);
    public void CreateCategory(String name);
    public void DeleteCategory(String categoryId);
    public void AddSiteToCategory(String categoryId, String siteId);
    public void RemoveSiteFromCategory(String categoryId, String siteId);
    
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

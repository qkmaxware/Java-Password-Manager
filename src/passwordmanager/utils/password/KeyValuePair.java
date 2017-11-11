/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.utils.password;

/**
 *
 * @author Colin Halseth
 */
public class KeyValuePair {
    
    private String k;
    private String v;
    
    public KeyValuePair(String key, String value){
        k = key;
        v = value;
    }
    
    public String getKey(){
        return k;
    }
    
    public String getValue(){
        return v;
    }
    
}

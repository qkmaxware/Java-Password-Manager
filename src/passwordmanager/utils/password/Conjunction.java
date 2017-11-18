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
public class Conjunction implements GrammarElement{

    private String[] words = new String[]{
        "Or",
        "Nor",
        "And",
        "But"
    };
    
    @Override
    public String[] getWords() {
        return words;
    }
    
}

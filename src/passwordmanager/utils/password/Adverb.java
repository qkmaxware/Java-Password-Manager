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
public class Adverb implements GrammarElement{
    private String[] words = new String[]{
        "Adoringly",
        "Awkwardly",
        "Beautifuly",
        "Briskly",
        "Brutally",
        "Carefully",
        "Cheerfully",
        "Completely",
        "Eagerly",
        "Gracefully",
        "Grimly",
        "Happily",
        "Hungrily",
        "Lazily",
        "Lifelessly",
        "Quickly",
        "Quietly",
        "Recklessy",
        "Ruthlessly",
        "Slopily",
        "Slowly",
        "Stylishly",
        "Unevenly",
        "Wishfully"
    };

    @Override
    public String[] getWords() {
        return words;
    }
}

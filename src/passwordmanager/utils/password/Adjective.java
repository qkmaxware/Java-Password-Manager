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
public class Adjective implements GrammarElement{
    private String[] words = new String[]{
        "Good",
        "New",
        "Old",
        "First",
        "Last",
        "Great",
        "Little",
        "Other",
        "Big",
        "High",
        "Low",
        "Different",
        "Small",
        "Large",
        "Next",
        "Early",
        "Young",
        "Important",
        "Few",
        "Public",
        "Bad",
        "Same",
        "Able"
    };

    @Override
    public String[] getWords() {
        return words;
    }
}


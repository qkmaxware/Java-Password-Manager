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
public class Noun implements GrammarElement{
    private String[] words = new String[]{
        "Cat",
        "Dog",
        "Man",
        "Mountain",
        "State",
        "Province",
        "Territory",
        "Ocean",
        "Country",
        "Building",
        "Airline",
        "House",
        "Bird",
        "Goose",
        "Banana",
        "Light",
        "Sun",
        "Flowers",
        "Suitcase",
        "Bed",
        "Movie",
        "Train",
        "Book",
        "Phone",
        "Clock",
        "Speaker",
        "Horse",
        "Cow",
        "Lizard",
        "Pony",
        "Dragon"
    };

    @Override
    public String[] getWords() {
        return words;
    }
}

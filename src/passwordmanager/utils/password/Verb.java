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
public class Verb implements GrammarElement{
    private String[] words = new String[]{
        "Jumps",
        "Runs",
        "Skips",
        "Hops",
        "Walks",
        "Crawls",
        "Bends",
        "Sways",
        "Swings",
        "Shakes",
        "Twists",
        "Gallops",
        "Leaps",
        "Rolls",
        "Twirls",
        "Kicks",
        "Stamps",
        "Grabs",
        "Punches",
        "Pulls",
        "Pushes",
        "Wiggles",
        "Catches",
        "Throws",
        "Digs",
        "Waves",
        "Climbs",
        "Winks",
        "Claps",
        "Yawns",
        "Blinks",
        "Shuffles",
        "Marches",
        "Turns",
        "Rides",
        "Swims",
        "Dives",
        "Skates",
        "Dances",
        "Jogs",
        "Stomps"
    };

    @Override
    public String[] getWords() {
        return words;
    }
}

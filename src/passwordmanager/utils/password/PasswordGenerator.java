/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.utils.password;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Colin Halseth
 */
public class PasswordGenerator {

    private String[] lowerCase = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private String[] upperCase = transform(lowerCase, (a) -> {
        return a.toUpperCase();
    });
    private String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    private String[] specialCharacters = new String[]{"~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+", "[", "{", "]", "}", "\\", "|", ";", ":", "\'", "\"", ",", "<", ".", ">", "/", "?"};

    private SecureRandom rng = new java.security.SecureRandom();

    private Sentence[] sentances = new Sentence[]{
        new Sentence(new Article(), new Adjective(), new Noun(), new Adverb(), new Verb()),
        new Sentence(new Article(), new Noun(), new Adverb(), new Verb()),
        new Sentence(new Article(), new Adjective(), new Noun(), new Verb()),
        new Sentence(new Article(), new Noun(), new Verb())
    };
    
    private KeyValuePair[] wordsToNumbers = new KeyValuePair[]{
        new KeyValuePair("a","4"),
        new KeyValuePair("b","8"),
        new KeyValuePair("e","3"),
        new KeyValuePair("g","6"),
        new KeyValuePair("o","0"),
        new KeyValuePair("s","5"),
        new KeyValuePair("i","1"),
        new KeyValuePair("z","2")
    };
    
    private KeyValuePair[] wordsToSpecial = new KeyValuePair[]{
        new KeyValuePair("c","("),
        new KeyValuePair("o","@"),
        new KeyValuePair("t","+"),
        new KeyValuePair("h","#"),
        new KeyValuePair("i","1"),
        new KeyValuePair("s","$")
    };
    
    private static interface Transformation {
        String Transform(String a);
    }

    private String[] transform(String[] str, Transformation t) {
        String[] s = new String[str.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = t.Transform(str[i]);
        }
        return s;
    }

    public String GeneratePassphrase(int length, boolean useUpperCase, boolean useDigits, boolean useSpecial){
        if(sentances.length < 1)
            return null;
        
        int k = rng.nextInt(sentances.length);
        Sentence c = this.sentances[k];
        
        String s = c.generate();
        
        if(!useUpperCase)
            s = s.toLowerCase();
        
        if(useDigits){
            for(int i = 0 ; i < wordsToNumbers.length; i++){
                KeyValuePair kv = wordsToNumbers[i];
                Pattern p = Pattern.compile(kv.getKey(), Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(s);
                StringBuffer b = new StringBuffer();
                while(m.find()){
                    int chance = rng.nextInt(11);
                    boolean replace = chance >= 3; //50% chance
                    if(replace){
                        m.appendReplacement(b, kv.getValue());
                    }
                }
                m.appendTail(b);
                s = b.toString();
            }
        }
        
        if(useSpecial){
            for(int i = 0 ; i < wordsToSpecial.length; i++){
                KeyValuePair kv = wordsToSpecial[i];
                Pattern p = Pattern.compile(kv.getKey(), Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(s);
                StringBuffer b = new StringBuffer();
                while(m.find()){
                    int chance = rng.nextInt(11);
                    boolean replace = chance >= 3;//50% chance
                    if(replace){
                        m.appendReplacement(b, kv.getValue());
                    }
                }
                m.appendTail(b);
                s = b.toString();
            }
        } 
        
        if(s.length() > length)
            return s.substring(0, length);
        return s;
    }
    
    public String GeneratePassword(int length, boolean useLowerCase, boolean useUpperCase, boolean useDigits, boolean useSpecial) {
        ArrayList<String> characters = new ArrayList<String>();

        //Add choices
        if (useLowerCase) {
            characters.addAll(Arrays.asList(lowerCase));
        }
        if (useUpperCase) {
            characters.addAll(Arrays.asList(upperCase));
        }
        if (useDigits) {
            characters.addAll(Arrays.asList(digits));
        }
        if (useSpecial) {
            characters.addAll(Arrays.asList(specialCharacters));
        }

        //Ensure size;
        if (characters.size() < 1) {
            return null;
        }

        //Shuffle choices so that there is no specific ordering
        Collections.shuffle(characters);

        //Build password up to this length
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = rng.nextInt(characters.size());
            builder.append(characters.get(idx));
        }
        return builder.toString();
    }

}

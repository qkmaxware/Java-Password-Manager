/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.utils.password;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Colin Halseth
 */
public class Sentence {
    
    private static SecureRandom rng = new SecureRandom();
    
    private GrammarElement[] parts;
    
    public Sentence(GrammarElement... elements){
        this.parts = elements;
    }
    
    public String generate(){
        StringBuilder b = new StringBuilder();
        
        for(int i = 0; i < parts.length; i++){
            GrammarElement e = parts[i];
            String[] words = e.getWords();
            if(words.length < 1)
                continue;
            
            List<String> wordList = Arrays.<String>asList(words);
            Collections.shuffle(wordList);
            
            int j = rng.nextInt(wordList.size());
            String s = wordList.get(j);
            b.append(s);
        }
        
        return b.toString();
    }
    
}

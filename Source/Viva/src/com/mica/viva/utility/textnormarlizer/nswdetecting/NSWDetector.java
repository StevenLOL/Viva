package com.mica.viva.utility.textnormarlizer.nswdetecting;


import com.mica.viva.utility.textnormarlizer.common.Constant;
import com.mica.viva.utility.textnormarlizer.unit.NonStandardWord;
import com.mica.viva.utility.textnormarlizer.unit.StandardWord;
import com.mica.viva.utility.textnormarlizer.unit.WordUnit;

import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lelightwin
 */
public class NSWDetector {

    private PhonemesConnectionRuleManager pcrm ;
    private ArrayList<String> consonants ;
    private ArrayList<String> vowels;
    private ArrayList<String[]> finals;
    private ArrayList<WordUnit> nswDetectResult;

    public NSWDetector(String inputText, String phonemeRuleDir) {
        pcrm= new PhonemesConnectionRuleManager(phonemeRuleDir+"Vowels.txt", phonemeRuleDir+"Consonants.txt");
        consonants = pcrm.getConsonants();
        vowels = pcrm.getVowels();
        finals = pcrm.getFinals();
        nswDetectResult = new ArrayList<WordUnit>();
        
        String[] words = inputText.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            StandardWord cw = splitSign(word);
            boolean check1 = checkPhonemeRule(cw);
            if (check1) {
                nswDetectResult.add(cw);
                //System.out.println("sw  : "+word);
            } else {
                NonStandardWord nsw = new NonStandardWord(word);
                nswDetectResult.add(nsw);
                //System.out.println("nsw : "+word);
            }
        }
    }

    /**
     * @param word
     * @return StandardWord of word include three informations: word,
     * unsignedWord and sign
     */
    private StandardWord splitSign(String word) {
        String[] signedCharacters = Constant.VIETNAMESE_SIGNED_CHARACTER;
        for (int i = 1; i < signedCharacters.length; i++) {
            String str = signedCharacters[i];
            for (int j = 0; j < word.length(); j++) {
                char c = word.charAt(j);
                int index = str.indexOf(c);
                if (index != -1) {
                    String unsignedWord = word.replaceFirst(c + "", signedCharacters[0].charAt(index) + "");
                    StandardWord result = new StandardWord(unsignedWord, word, i);
                    return result;
                }
            }
        }
        return new StandardWord(word, word, 0);
    }

    /**
     * @function check out whether word fits with a phoneme rule
     * @param word
     * @return
     */
    private boolean checkPhonemeRule(StandardWord wu) {
        String word = wu.getUnsignedWord();
        //<editor-fold desc="process for consonant">
        String targetConsonant = "";
        for (int i = 0; i < consonants.size(); i++) {
            String conso = consonants.get(i);
            if (word.startsWith(conso)) {
                if (targetConsonant.length() < conso.length()) {
                    targetConsonant = conso;
                }
            }
        }
        //System.out.println(targetConsonant);
        //</editor-fold>

        //<editor-fold desc="process for vowel">
        String vowelPart = word.replaceFirst(targetConsonant, "");

        String targetVowel = "";
        int indexOfTargetVowel = -1;
        for (int i = 0; i < vowels.size(); i++) {
            String vowel = vowels.get(i);
            if (vowelPart.startsWith(vowel)) {
                if (targetVowel.length() < vowel.length()) {
                    targetVowel = vowel;
                    indexOfTargetVowel = i;
                }
            }
        }
        //System.out.println(targetVowel);
        //</editor-fold>

        //<editor-fold desc="process for final">
        if (targetVowel.equals("")) {
            return false;
        } else {
            String finalPart = vowelPart.replaceFirst(targetVowel, "");
            if ("".equals(finalPart)) {
                finalPart = "zero";
            }
            //System.out.println(finalPart);
            String[] selectedFinals = finals.get(indexOfTargetVowel);
            for (int i = 0; i < selectedFinals.length; i++) {
                String sf = selectedFinals[i];
                if (finalPart.equals(sf)) {
                    return true;
                }
            }
        }
        //</editor-fold>
        return false;
    }

    /**
     * @return the nswDetectResult
     */
    public ArrayList<WordUnit> getNswDetectResult() {
        return nswDetectResult;
    }

    public static void main(String[] args) {
    	//System.out.println(System.getProperty("user.dir"));
        //NSWDetector nswd = new NSWDetector("hãy nhắn tin tới số 329989043", System.getProperty("user.dir")+"/PhonemesConnectionRules");
        
    }
}
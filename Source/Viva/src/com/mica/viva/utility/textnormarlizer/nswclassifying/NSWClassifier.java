/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.utility.textnormarlizer.nswclassifying;


import java.util.ArrayList;

import com.mica.viva.utility.textnormarlizer.unit.NonStandardWord;
import com.mica.viva.utility.textnormarlizer.unit.WordUnit;

/**
 *
 * @author lelightwin
 */
public class NSWClassifier {

    public NSWClassifier(ArrayList<WordUnit> input) {
        for (int i = 0; i < input.size(); i++) {
            WordUnit wu = input.get(i);
            if (wu instanceof NonStandardWord) {
                classify((NonStandardWord) wu);
            }
        }
    }

    /**
     * @function classify nsw into these categories : number, alpha, compound or other
     * @param nsw 
     */
    private void classify(NonStandardWord nsw) {
        String word = nsw.getOriginalWord();
        boolean number = false;
        boolean alpha = false;
        boolean specialCharacter = false;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (Character.isDigit(ch)) {
                if (!number) {
                    number = true;
                }
            } else if (Character.isLetter(ch)) {
                if (!alpha) {
                    alpha = true;
                }
            } 
        }
        
        if (number){
            //<editor-fold desc="if nsw contains number characters">
            if (alpha){
                //<editor-fold desc="if nsw contains alphabert characters">
                nsw.setType(NonStandardWord.COMPOUND);
                //</editor-fold>
            } else {
                //<editor-fold desc="if nsw does not contain alphabert characters">
                nsw.setType(NonStandardWord.NUMBER);
                //</editor-fold>
            }
            //</editor-fold>
        } else {
            //<editor-fold desc="if nsw does not contain number characters">
            if (alpha){
                //<editor-fold desc="if nsw contains alphabert charactes">
                nsw.setType(NonStandardWord.ALPHA);
                //</editor-fold>
            } else {
                //<editor-fold desc="if nsw does not contain alphabert characters">
                nsw.setType(NonStandardWord.OTHER);
                //</editor-fold>
            }
            //</editor-fold>
        }
    }

    public static void main(String[] args) {
        System.out.println(Character.isLetter('ắ'));
    }
}
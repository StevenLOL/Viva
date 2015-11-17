package com.mica.viva.utility.textnormarlizer.nswdetecting;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lelightwin
 */
public class PhonemesConnectionRuleManager {

    private ArrayList<String> consonants = new ArrayList<String>();
    private ArrayList<String> vowels = new ArrayList<String>();
    private ArrayList<String[]> finals = new ArrayList<String[]>();

    public PhonemesConnectionRuleManager(String vowelFile, String consonantFile) {
        try {
            BufferedReader bfr1 = new BufferedReader(new InputStreamReader(new FileInputStream(consonantFile), "utf-8"));
            BufferedReader bfr2 = new BufferedReader(new InputStreamReader(new FileInputStream(vowelFile), "utf-8"));

            //<editor-fold desc="read consonants from file">
            consonants.addAll(Arrays.asList(bfr1.readLine().split(" ")));
//            for (int i = 0; i < consonants.size(); i++) {
//                String string = consonants.get(i);
//                System.out.println(string);
//            }
            //</editor-fold>

            //<editor-fold desc="read vowels from file">            
            String vowel;
            while ((vowel = bfr2.readLine()) != null) {
                String[] vowelDatas = vowel.split(" : ");
                vowels.add(vowelDatas[0]);
                finals.add(vowelDatas[1].split(" "));
            }
            //</editor-fold>

            bfr2.close();
            bfr1.close();
        } catch (IOException ex) {
            Logger.getLogger(PhonemesConnectionRuleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the consonants
     */
    public ArrayList<String> getConsonants() {
        return consonants;
    }

    /**
     * @return the vowels
     */
    public ArrayList<String> getVowels() {
        return vowels;
    }

    /**
     * @return the finals
     */
    public ArrayList<String[]> getFinals() {
        return finals;
    }

    public static void main(String[] args) {
        PhonemesConnectionRuleManager pcrm = new PhonemesConnectionRuleManager(
                System.getProperty("user.dir") + "/PhonemesConnectionRules/Vowels.txt",
                System.getProperty("user.dir") + "/PhonemesConnectionRules/Consonants.txt");
    }
}

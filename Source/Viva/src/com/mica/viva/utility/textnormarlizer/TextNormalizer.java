package com.mica.viva.utility.textnormarlizer;


import java.io.File;
import java.util.ArrayList;

import com.mica.viva.utility.textnormarlizer.common.ReadingRepository;
import com.mica.viva.utility.textnormarlizer.nswclassifying.NSWClassifier;
import com.mica.viva.utility.textnormarlizer.nswdetecting.NSWDetector;
import com.mica.viva.utility.textnormarlizer.nswpronunciation.NSWPronunciator;
import com.mica.viva.utility.textnormarlizer.unit.NonStandardWord;
import com.mica.viva.utility.textnormarlizer.unit.StandardWord;
import com.mica.viva.utility.textnormarlizer.unit.WordUnit;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lelightwin
 */
public class TextNormalizer {

    private ReadingRepository rearep;
    private String phonemeRuleDir;
    private String dictionaryDir;

    public TextNormalizer(String phonemeRuleDir, String dictionaryDir) {
        this.dictionaryDir = dictionaryDir;
        this.phonemeRuleDir = phonemeRuleDir;
        rearep = new ReadingRepository(new File(dictionaryDir));
    }

    public String normalize(String inputText) {
        NSWDetector nswDetect = new NSWDetector(inputText.toLowerCase(), phonemeRuleDir);
        ArrayList<WordUnit> wordArr = nswDetect.getNswDetectResult();
        NSWClassifier nswClassify = new NSWClassifier(wordArr);
        NSWPronunciator nswPronuncation = new NSWPronunciator(wordArr, rearep);

        String result = "";
        for (int i = 0; i < wordArr.size(); i++) {
            WordUnit wu = wordArr.get(i);
            if (wu instanceof NonStandardWord) {
                NonStandardWord nsw = (NonStandardWord) wu;
                result += " " + nsw.getFull();
            } else {
                StandardWord sw = (StandardWord) wu;
                result += " " + sw.getWord();
                //System.out.println(sw.getWord());
            }
        }

        return result.trim();
    }

    public static void main(String[] args) {
        String inputText = "nhắn tin cho tôi tới số của Viện MICA là 94";
        TextNormalizer tn = new TextNormalizer(System.getProperty("user.dir")+"/PhonemesConnectionRules/", System.getProperty("user.dir")+"/Dictionary/read/");
        System.out.println(tn.normalize(inputText));
    }
}

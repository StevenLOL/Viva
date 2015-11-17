/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.understanding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mica.viva.understanding.constant.KeywordCorpus;
import com.mica.viva.understanding.constant.KeywordType;


import vn.hus.nlp.tokenizer.VietTokenizer;

/**
 *
 * @author Truy Thu
 */
public class KeywordTokenizer {

    private String[] words;
    static KeywordCorpus keywordCorpus = new KeywordCorpus();
    private static final VietTokenizer tokenizer = new VietTokenizer();
    private int i;
    private int j;
    private List<Keyword> keyword;
    private String nowWord;

    public KeywordTokenizer(String sen) {
        i = 0;
        keyword = new ArrayList<Keyword>();

        parserString(sen);
    }

    public KeywordTokenizer() {
        i = 0;
        keyword = new ArrayList<Keyword>();
    }

    public void parserString(String sen) {
        sen = sen.toLowerCase();
        words = tokenizer.segment(sen).split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("_", " ");
        }
        keyword = new ArrayList<Keyword>();
        i = 0;
        j = 0;
        Keyword key = null;

        //bat dau parser String
        while (j < words.length) {
            key = checkKeyword();
            keyword.add(key);
        }

        key = new Keyword("", KeywordType.End);
        keyword.add(key);

    }

    public Keyword checkKeyword() {
        nowWord = words[j];
        Keyword key = new Keyword(nowWord, KeywordType.Other);
        
        if (keyword.size()>0)
        if ((keyword.get(keyword.size()-1)).type==KeywordType.ToContact) {
            if (checkContactName()) key = new Keyword(nowWord, KeywordType.ContactName);
            return key;
        }

        if (checkPlace()) {
            key = new Keyword(nowWord, KeywordType.Place);
        } else if (checkToMessage()) {
            key = new Keyword(nowWord, KeywordType.ToMessage);
        } else if (checkToWeather()) {
            key = new Keyword(nowWord, KeywordType.ToWeather);
        } else if (checkTime()) {
            key = new Keyword(nowWord, KeywordType.Time);
        } else if (checkToContact()) {
            key = new Keyword(nowWord, KeywordType.ToContact);
        } else if (checkToContent()) {
            key = new Keyword(nowWord, KeywordType.Tocontent);
        } else if (checkContactName()) {
            key = new Keyword(nowWord, KeywordType.ContactName);
        } else {
            j++;
        }

        return key;
    }

    public boolean checkPlace() {
        String temp = words[j];

        if (keywordCorpus.checkPlace(temp)) {
            j++;
            return true;
        }
        if (j + 1 == words.length) {
            return false;
        }
        temp += " " + words[j + 1];
        if (keywordCorpus.checkPlace(temp)) {
            nowWord = temp;
            j += 2;
            return true;
        }
        if (j + 2 == words.length) {
            return false;
        }
        temp += " " + words[j + 2];
        if (keywordCorpus.checkPlace(temp)) {
            nowWord = temp;
            j += 3;
            return true;
        }
        return false;
    }

    public boolean checkTime() {
        int t = 0;
        if (keywordCorpus.checkTimeKeyword(words[j])) {
            j++;
            return true;
        }
        try {
            int number = Integer.parseInt(words[j]);
            if (eat("ngĂ y",1)) {
                nowWord += " " + words[j + 1];
                if (eat("ná»¯a",2)||eat("sau",2)||eat("trÆ°á»›c",2)) {
                    nowWord += " " + words[j + 2];
                    j++;
                }
                j += 2;
                return true;
            }
        }
        catch (Exception ex) {
            
        }
        if (eat("thá»©", 0)) {
            if ((eat("2", 1)) || (eat("3", 1)) || (eat("4", 1)) || (eat("5", 1)) || (eat("6", 1)) || (eat("7", 1))) {
                nowWord += " " + words[j + 1];
                j += 2;
                return true;
            }
        }

        if (eat("ngĂ y", 0)) {
            if (eat("má»“ng", 1) || eat("mĂ¹ng", 1)) {
                t = 1;
            }

            if (j + t + 1 == words.length) {
                return false;
            }
            
            try {
            int day = Integer.parseInt(words[j + t + 1]);

            if ((day >= 1) && (day <= 31)) {
                if (t == 1) {
                    nowWord += " má»“ng";
                    j++;
                }
                nowWord += " " + words[j + 1];
                j += 2;
                return true;
            } else {
                return false;
            }
            }
            catch (Exception e) {
            	return false;
            }
        }

        if (eat("thĂ¡ng", 0)) {
            if (j + 1 == words.length) {
                return false;
            }
            if (eat("hai",1)||eat("ba",1)||eat("tÆ°",1)||eat("nÄƒm",1)||eat("sĂ¡u",1)||eat("báº£y",1)||eat("tĂ¡m",1)||eat("chĂ­n",1)||eat("mÆ°á»�i",1)||eat("mÆ°á»�i má»™t",1)||eat("mÆ°á»�i hai",1)) {
                nowWord += " " + words[j + 1];
                j += 2;
                return true;
            }
            
            int month = Integer.parseInt(words[j + 1]);
            if ((month >= 1) && (month <= 12)) {
                nowWord += " " + words[j + 1];
                j += 2;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean checkToContact() {
        if (eat("gá»­i", 0)) {
            j++;
        }
        if ((eat("tá»›i", 0)) || (eat("Ä‘áº¿n", 0)) || (eat("cho", 0)) || (eat("tá»›i sá»‘", 0))) {
            if (eat("sá»‘", 1)) {
                nowWord += " sá»‘";
                j++;
            }
            if (eat("Ä‘iá»‡n thoáº¡i", 1)) {
                nowWord += " Ä‘iá»‡n thoáº¡i";
                j++;
            }
            if (eat("thuĂª bao", 1)) {
                nowWord += " thuĂª bao";
                j++;
            }
            j++;
            return true;
        }
        return false;
    }

    public boolean checkToContent() {
        if ((eat("ráº±ng", 0)) || (eat("báº£o", 0))) {
            j++;
            return true;
        }
        if ((eat("vá»›i", 0)) && (eat("ná»™i dung", 1))) {
            nowWord += " ná»™i dung";
            j++;
            if ((eat("ráº±ng", 1))||(eat("lĂ ", 1))) {
                nowWord += " ráº±ng";
                j++;
            }
            j++;
            return true;
        }
        if (eat("ná»™i dung", 0)) {
            if ((eat("ráº±ng", 1))||(eat("lĂ ", 1))) {
                nowWord += " ráº±ng";
                j++;
            }
            j++;
            return true;
        }
        return false;
    }

    public boolean checkToMessage() {
        if (eat("nháº¯n", 0) || eat("nháº¯n tin", 0)) {
            j++;
            return true;
        }
        if (eat("tin", 0) && eat("nháº¯n", 1)) {
            nowWord += " nháº¯n";
            j += 2;
            return true;
        }
        if (eat("gá»­i", 0) || eat("má»Ÿ", 0) || eat("tráº£ lá»�i", 0)) {
            if (eat("tin", 1) && eat("nháº¯n", 2)) {
                nowWord += " tin nháº¯n";
                j += 3;
                return true;
            }
            if (eat("tin", 1)) {
                nowWord += " tin";
                j += 2;
                return true;
            }
        }
        if (eat("soáº¡n",0)&&(eat("tin",1))) {
            if (eat("nháº¯n",2)) {
                j += 3;
                return true;
            }
            j +=2;
            return true;
        }
        return false;
    }

    public boolean checkToWeather() {
        if (eat("thá»�i tiáº¿t", 0) || (eat("trá»�i", 0)) || (eat("Ä‘á»™ áº©m", 0)) || (eat("nhiá»‡t Ä‘á»™", 0)) || (eat("mÆ°a", 0)) || (eat("náº¯ng", 0))) {
            j++;
            return true;
        }
        if ((eat("cáº§n", 0)) || (eat("pháº£i", 0))) {
            if ((eat("Ä‘em", 1)) || (eat("mang", 1))) {
                nowWord += " Ä‘em";
                if (eat("theo", 2)) {
                    nowWord += " theo";
                    j++;
                }
                j++;
            }
            if (eat("Ă´", 1) || eat("Ă¡o mÆ°a", 1) || eat("dĂ¹", 1)) {
                j++;
                nowWord += " " + words[j];
            }
            j++;
            return true;
        }
        if (eat("Ă´", 0) || eat("Ă¡o mÆ°a", 0) || eat("dĂ¹", 0)) {
            j++;
            return true;
        }
        return false;
    }

    public boolean checkContactName() {
        if (checkPhoneNumber()) {
            return true;
        }
        String temp = words[j];

        if (keywordCorpus.checkRelation(temp)) {
            //truong hop co dai tu chi quan he
            j++;
            if (checkPerson()) {
                return true;
            } else {
                j--;
                return false;
            }
        } else {
            //truong hop khong co dai tu chi quan he
            return checkPerson();
        }

    }

    public boolean checkPerson() {
        int count = 0;
        if (j >= words.length) {
            return false;
        }
        String temp = words[j];
        while (keywordCorpus.checkPerson(temp) != -1) {
            nowWord = temp;
            j++;
            count++;
            if (j == words.length) {
                break;
            }
            temp += " " + words[j];
        }
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkPhoneNumber() {
        // can repair this rule
        Pattern pattern = Pattern.compile("0((1\\d{2})|(9\\d))\\d{7}");
        Matcher matcher = pattern.matcher(words[j]);
        if (matcher.matches()) {
            j++;
            return true;
        }
        return false;
    }

    private boolean eat(String food, int offset) {
        if (j + offset >= words.length) {
            return false;
        }
        if (words[j + offset].equals(food)) {
            return true;
        } else {
            return false;
        }
    }

    public Keyword next() {
        if (i < keyword.size()) {
            i++;
            return keyword.get(i - 1);
        } else {
            return null;
        }
    }

    public Keyword prev() {
        if (i > 0) {
            i--;
        }
        return keyword.get(i);
    }

    public boolean hasNext() {
        if (i < keyword.size()) {
            return true;
        } else {
            return false;
        }
    }
}

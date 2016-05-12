/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package understanding;

import Constant.KeywordCorpus;
import Constant.KeywordType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
            if (eat("ngày",1)) {
                nowWord += " " + words[j + 1];
                if (eat("nữa",2)||eat("sau",2)||eat("trước",2)) {
                    nowWord += " " + words[j + 2];
                    j++;
                }
                j += 2;
                return true;
            }
        }
        catch (Exception ex) {
            
        }
        if (eat("thứ", 0)) {
            if ((eat("2", 1)) || (eat("3", 1)) || (eat("4", 1)) || (eat("5", 1)) || (eat("6", 1)) || (eat("7", 1))) {
                nowWord += " " + words[j + 1];
                j += 2;
                return true;
            }
        }

        if (eat("ngày", 0)) {
            if (eat("mồng", 1) || eat("mùng", 1)) {
                t = 1;
            }

            if (j + t + 1 == words.length) {
                return false;
            }
            
            try {
            int day = Integer.parseInt(words[j + t + 1]);

            if ((day >= 1) && (day <= 31)) {
                if (t == 1) {
                    nowWord += " mồng";
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

        if (eat("tháng", 0)) {
            if (j + 1 == words.length) {
                return false;
            }
            if (eat("hai",1)||eat("ba",1)||eat("tư",1)||eat("năm",1)||eat("sáu",1)||eat("bảy",1)||eat("tám",1)||eat("chín",1)||eat("mười",1)||eat("mười một",1)||eat("mười hai",1)) {
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
        if (eat("gửi", 0)) {
            j++;
        }
        if ((eat("tới", 0)) || (eat("đến", 0)) || (eat("cho", 0)) || (eat("tới số", 0))) {
            if (eat("số", 1)) {
                nowWord += " số";
                j++;
            }
            if (eat("điện thoại", 1)) {
                nowWord += " điện thoại";
                j++;
            }
            if (eat("thuê bao", 1)) {
                nowWord += " thuê bao";
                j++;
            }
            j++;
            return true;
        }
        return false;
    }

    public boolean checkToContent() {
        if ((eat("rằng", 0)) || (eat("bảo", 0))) {
            j++;
            return true;
        }
        if ((eat("với", 0)) && (eat("nội dung", 1))) {
            nowWord += " nội dung";
            j++;
            if ((eat("rằng", 1))||(eat("là", 1))) {
                nowWord += " rằng";
                j++;
            }
            j++;
            return true;
        }
        if (eat("nội dung", 0)) {
            if ((eat("rằng", 1))||(eat("là", 1))) {
                nowWord += " rằng";
                j++;
            }
            j++;
            return true;
        }
        return false;
    }

    public boolean checkToMessage() {
        if (eat("nhắn", 0) || eat("nhắn tin", 0)) {
            j++;
            return true;
        }
        if (eat("tin", 0) && eat("nhắn", 1)) {
            nowWord += " nhắn";
            j += 2;
            return true;
        }
        if (eat("gửi", 0) || eat("mở", 0) || eat("trả lời", 0)) {
            if (eat("tin", 1) && eat("nhắn", 2)) {
                nowWord += " tin nhắn";
                j += 3;
                return true;
            }
            if (eat("tin", 1)) {
                nowWord += " tin";
                j += 2;
                return true;
            }
        }
        if (eat("soạn",0)&&(eat("tin",1))) {
            if (eat("nhắn",2)) {
                j += 3;
                return true;
            }
            j +=2;
            return true;
        }
        return false;
    }

    public boolean checkToWeather() {
        if (eat("thời tiết", 0) || (eat("trời", 0)) || (eat("độ ẩm", 0)) || (eat("nhiệt độ", 0)) || (eat("mưa", 0)) || (eat("nắng", 0))) {
            j++;
            return true;
        }
        if ((eat("cần", 0)) || (eat("phải", 0))) {
            if ((eat("đem", 1)) || (eat("mang", 1))) {
                nowWord += " đem";
                if (eat("theo", 2)) {
                    nowWord += " theo";
                    j++;
                }
                j++;
            }
            if (eat("ô", 1) || eat("áo mưa", 1) || eat("dù", 1)) {
                j++;
                nowWord += " " + words[j];
            }
            j++;
            return true;
        }
        if (eat("ô", 0) || eat("áo mưa", 0) || eat("dù", 0)) {
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

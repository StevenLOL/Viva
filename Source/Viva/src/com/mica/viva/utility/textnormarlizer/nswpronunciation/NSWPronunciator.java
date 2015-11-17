/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.utility.textnormarlizer.nswpronunciation;

import com.mica.viva.utility.textnormarlizer.common.ReadingRepository;
import com.mica.viva.utility.textnormarlizer.unit.NonStandardWord;
import com.mica.viva.utility.textnormarlizer.unit.WordUnit;
import com.mica.viva.utility.textnormarlizer.utilitize.ReadingLibrary;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author lelightwin
 */
public class NSWPronunciator {

    private HashMap<Character, String> numberDict = new HashMap<Character, String>();
    private ReadingRepository rearep;

    public NSWPronunciator(ArrayList<WordUnit> input, ReadingRepository rearep) {
        this.rearep = rearep;
        for (int i = 0; i < input.size(); i++) {
            WordUnit wu = input.get(i);
            if (wu instanceof NonStandardWord) {
                NonStandardWord nsw = (NonStandardWord) wu;
                String word = nsw.getOriginalWord();
                String extend = "";
                if (nsw.getType() == NonStandardWord.NUMBER) {
                    if (word.length() <= 4) {
                        extend = readNumber(word);
                    } else {
                        extend = readNumberSequence(word);
                    }


                } else {
                    extend = readOther(word.toLowerCase());
                }
                nsw.setFull(extend);
                //System.out.println(nsw.getFull());
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // <editor-fold desc="for number">
    /**
     * *
     * Đọc các số 0 - 9 theo 2 cách Vd năm và lăm
     *
     * @param digit số
     * @param type. Nếu type = 1 -> đọc thường; type = 2 -> đọc kiểu sau hàng
     * chục
     * @return cách đọc
     */
    public String readDigit(char digit, int type) {
        String output = "";
        switch (digit) {
            case '0':
                if (type == 1) {
                    output = "không";
                } else if (type == 2) {
                    output = "";
                }
                break;
            case '1':
                if (type == 1) {
                    output = "một";
                } else if (type == 2) {
                    output = "mốt";
                }
                break;
            case '2':
                output = "hai";
                break;
            case '3':
                output = "ba";
                break;
            case '4':
                if (type == 1) {
                    output = "bốn";
                } else if (type == 2) {
                    output = "tư";
                }
                break;
            case '5':
                if (type == 1) {
                    output = "năm";
                } else if (type == 2) {
                    output = "lăm";
                }
                break;
            case '6':
                output = "sáu";
                break;
            case '7':
                output = "bẩy";
                break;
            case '8':
                output = "tám";
                break;
            case '9':
                output = "chín";
                break;
        }
        return output;
    }

    /**
     * *
     * Đọc các số nhỏ hơn 1000 và lớn hơn 0
     *
     * @param input_number số truyền vào dạng xâu
     * @return cách đọc các số nhỏ hơn 1000 và lớn hơn 0
     */
    private String readThreeDigit(String input_number) {
        String output = "";
        try {
            //Xét theo số các chữ số để đọc
            switch (input_number.length()) {
                case 1: {
                    output = readDigit(input_number.charAt(0), 1);
                    break;
                }
                case 2: {
                    char chuc = input_number.charAt(0);
                    char donvi = input_number.charAt(1);
                    if (chuc == 1) {
                        output = "mười";
                        switch (donvi) {
                            case 0:
                                break;
                            case 1: {
                                output += " " + readDigit(donvi, 1);
                                break;
                            }
                            case 4: {
                                output += " " + readDigit(donvi, 1);
                                break;
                            }
                            default: {
                                output += " " + readDigit(donvi, 2);
                                break;
                            }
                        }
                    } else {
                        output = readDigit(chuc, 1);
                        if (donvi != 0) {
                            output += " " + readDigit(donvi, 2);
                        } else {
                            output += " mươi";
                        }
                    }
                    break;
                }
                case 3: {
                    char tram = input_number.charAt(0);
                    char chuc = input_number.charAt(1);
                    char donvi = input_number.charAt(2);
                    if (tram == chuc && chuc == donvi && donvi == 0) {
                        output = "";
                        break;
                    }
                    output = readDigit(tram, 1) + " trăm";
                    if (chuc == 0) {
                        if (donvi != 0) {
                            output += " linh " + readDigit(donvi, 1);
                        }
                    } else if (chuc == 1) {
                        output += " mười";
                        switch (donvi) {
                            case 0:
                                break;
                            case 1: {
                                output += " " + readDigit(donvi, 1);
                                break;
                            }
                            case 4: {
                                output += " " + readDigit(donvi, 1);
                                break;
                            }
                            default: {
                                output += " " + readDigit(donvi, 2);
                                break;
                            }
                        }
                    } else if (chuc > 1) {
                        output += " " + readDigit(chuc, 1);
                        if (donvi != 0) {
                            output += " " + readDigit(donvi, 2);
                        } else {
                            output += " mươi";
                        }
                    }
                    break;
                }
            }
        } catch (NumberFormatException nfe) {
        }
        return output;
    }

    /**
     * *
     * Đọc dãy của tỉ
     *
     * @param input_number String 9 số
     * @return cách đọc
     */
    private String readNineDigit(String input_number) {
        String output = "";
        if (input_number.length() >= 7 && input_number.length() <= 9) {
            switch (input_number.length()) {
                case 7: {
                    output = readThreeDigit(input_number.substring(0, 1)) + " triệu";
                    String nghin = readThreeDigit(input_number.substring(1, 4));
                    if (!(nghin.equalsIgnoreCase(""))) {
                        output += " " + nghin + " nghìn";
                    }
                    String donvi = readThreeDigit(input_number.substring(4, 7));
                    if (!(donvi.equalsIgnoreCase(""))) {
                        output += " " + donvi;
                    }
                    break;
                }
                case 8: {
                    output = readThreeDigit(input_number.substring(0, 2)) + " triệu";
                    String nghin = readThreeDigit(input_number.substring(2, 5));
                    if (!(nghin.equalsIgnoreCase(""))) {
                        output += " " + nghin + " nghìn";
                    }
                    String donvi = readThreeDigit(input_number.substring(5, 8));
                    if (!(donvi.equalsIgnoreCase(""))) {
                        output += " " + donvi;
                    }
                    break;
                }
                case 9: {
                    output = readThreeDigit(input_number.substring(0, 3)) + " triệu";
                    String nghin = readThreeDigit(input_number.substring(3, 6));
                    if (!(nghin.equalsIgnoreCase(""))) {
                        output += " " + nghin + " nghìn";
                    }
                    String donvi = readThreeDigit(input_number.substring(6, 9));
                    if (!(donvi.equalsIgnoreCase(""))) {
                        output += " " + donvi;
                    }
                    break;
                }
            }
        } else if (input_number.length() >= 4 && input_number.length() <= 6) {
            switch (input_number.length()) {
                case 4: {
                    output = readThreeDigit(input_number.substring(0, 1)) + " nghìn";
                    String donvi = readThreeDigit(input_number.substring(1, 4));
                    if (!(donvi.equalsIgnoreCase(""))) {
                        output += " " + donvi;
                    }
                    break;
                }
                case 5: {
                    output = readThreeDigit(input_number.substring(0, 2)) + " nghìn";
                    String donvi = readThreeDigit(input_number.substring(2, 5));
                    if (!(donvi.equalsIgnoreCase(""))) {
                        output += " " + donvi;
                    }
                    break;
                }
                case 6: {
                    output = readThreeDigit(input_number.substring(0, 3)) + " nghìn";
                    String donvi = readThreeDigit(input_number.substring(3, 6));
                    if (!(donvi.equalsIgnoreCase(""))) {
                        output += " " + donvi;
                    }
                    break;
                }
            }
        } else if (input_number.length() >= 1 && input_number.length() <= 3) {
            output = readThreeDigit(input_number);
        }
        return output;
    }

    /**
     * *
     * Chuẩn hóa xâu số hiện ra
     *
     * @param input_string_number xâu số truyền vào
     * @return xâu số trả ra
     */
    public String standardStringNumber(String input_string_number) {
        int n = 0;
        String output = input_string_number;
        while (output.startsWith(" ")) {
            output = output.substring(1);
        }
        while (output.endsWith(" ")) {
            n = output.length();
            output = output.substring(n, n + 1);
        }
        output.replace("  ", " ");
        return output;
    }

    /**
     * *
     * Đọc số truyền vào
     *
     * @param input_number string số truyền vào
     * @return Cách đọc chuẩn của số truyền vào
     */
    private String readNumber(String input_number) {
        String output = "", prefix_number = "", suffix_number = "";
        input_number = standardStringNumber(input_number);
        input_number.replace(".", "");
        //đọc số âm
        if (input_number.startsWith("-")) {
            output = "âm ";
            input_number = input_number.substring(1);          //Cắt đi dấu trừ
        }
        if (input_number.contains(",")) {
            String[] number_array = input_number.split(",");
            prefix_number = number_array[0];
            suffix_number = number_array[1];
        } else {
            prefix_number = input_number;
        }
        //chia thành chuỗi 9 kí tự 1 roi doc.
        int n = prefix_number.length();
        int k = n / 9;
        int mod = n % 9;
        if (mod == 0) {
            for (int i = 1; i <= k; i++) {
                output += readNineDigit(prefix_number.substring(9 * (i - 1), 9 * i));
                for (int j = 1; j <= k - i; j++) {
                    output += " tỉ ";
                }
            }
        } else {
            int i = 0, j = 0;
            output += readNineDigit(prefix_number.substring(0, mod));
            for (j = 1; j <= k; j++) {
                output += " tỉ ";
            }
            prefix_number = prefix_number.substring(mod);
            for (i = 1; i <= k; i++) {
                output += readNineDigit(prefix_number.substring(9 * (i - 1), 9 * i));
                for (j = 1; j <= k - i; j++) {
                    output += " tỉ ";
                }
            }
        }
        output = standardStringNumber(output);
        if (!suffix_number.equalsIgnoreCase("")) {
            output += " phẩy";
            for (int i = 0; i < suffix_number.length(); i++) {
                output += " " + readDigit(suffix_number.charAt(i), 1);
            }
        }
        return output;
    }

    /**
     *
     * @param input_number
     * @return
     */
    private String readNumberSequence(String input_number) {
        String result = "";
        for (int i = 0; i < input_number.length(); i++) {
            result += " " + readDigit(input_number.charAt(i), 1);
        }
        return result.trim();
    }
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // <editor-fold desc="for others">
    private String readOther(String word) {
        return rearep.getDictionary().get(word);
    }
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

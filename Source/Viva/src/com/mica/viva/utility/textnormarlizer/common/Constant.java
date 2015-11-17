package com.mica.viva.utility.textnormarlizer.common;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lelightwin
 */
public class Constant {
	private static String databaseDirPrefix_;
	
	public static void setDatabaseDirPrefix(String str){
		databaseDirPrefix_ = str;
	}
	
    public static final String VOWELS_FILE_PATH = databaseDirPrefix_+"/PhonemesConnectionRules/Vowels.txt";
    public static final String CONSONANTS_FILE_PATH = databaseDirPrefix_+"/PhonemesConnectionRules/Consonants.txt";
    public static final String[] VIETNAMESE_SIGNED_CHARACTER = new String[]{
        "aăâeêioơôyuư",
        "áắấéếíóớốýúứ",
        "àằầèềìòờồỳùừ",
        "ảẳẩẻểỉỏởổỷủử",
        "ãẵẫẽễĩõỡỗỹũữ",
        "ạặậẹệịọợộỵụự",
    };
    public static final String CHARACTER_DATABASE = databaseDirPrefix_+"/Dictionary/read/Char.db";
    public static final String GREEK_DATABASE = databaseDirPrefix_+"/Dictionary/read/greek.db";
    public static final String LOAN_DATABASE = databaseDirPrefix_+"/Dictionary/read/loan.db";
    public static final String UNIT_MONEY_DATABASE = databaseDirPrefix_+"/Dictionary/read/unitMoney.db";
    public static final String GENERAL_DATABASE = databaseDirPrefix_+"/Dictionary/read/";
    
    public static void main(String[] args) {
        //System.out.println(VIETNAMESE_SIGNED_CHARACTER[0][2]);
    }
}
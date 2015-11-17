package com.mica.viva.utility.textnormarlizer.unit;

import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lelightwin
 */
public class NonStandardWord extends WordUnit {

    public static final int NUMBER = 0;
    public static final int ALPHA = 1;
    public static final int COMPOUND = 2;
    public static final int OTHER = 3;
    private ArrayList<NonStandardWord> subNSW;
    private String originalWord;
    private String full;
    private int type;

    public NonStandardWord(String originalWord) {
        this.originalWord = originalWord;
        this.subNSW = new ArrayList<NonStandardWord>();
    }

    /**
     * @return the originalWord
     */
    public String getOriginalWord() {
        return originalWord;
    }

    /**
     * @param originalWord the originalWord to set
     */
    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    /**
     * @return the full
     */
    public String getFull() {
        return full;
    }

    /**
     * @param full the full to set
     */
    public void setFull(String full) {
        this.full = full;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the subNSW
     */
    public ArrayList<NonStandardWord> getSubNSW() {
        return subNSW;
    }

    /**
     * @param subNSW the sNSW for adding into subNSW
     */
    public void addSubNSW(NonStandardWord sNSW) {
        this.subNSW.add(sNSW);
    }
}
package com.mica.viva.utility.textnormarlizer.unit;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lelightwin
 */
public class StandardWord extends WordUnit{
    private String unsignedWord;
    private String word;
    private int sign;
    
    public StandardWord(){
        
    }
    
    public StandardWord(String unsignedWord, String word, int sign){
        this.unsignedWord = unsignedWord;
        this.word = word;
        this.sign = sign;
    }

    /**
     * @return the unsignedWord
     */
    public String getUnsignedWord() {
        return unsignedWord;
    }

    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @return the sign
     */
    public int getSign() {
        return sign;
    }
}

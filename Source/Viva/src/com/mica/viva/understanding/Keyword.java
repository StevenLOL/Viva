/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.understanding;

import constant.KeywordType;


/**
 *
 * @author Truy Thu
 */
public class Keyword {
    public String keyword;
    public KeywordType type;
    
    public Keyword(String key, KeywordType type) {
        this.keyword = key;
        this.type = type;
    }
    
}

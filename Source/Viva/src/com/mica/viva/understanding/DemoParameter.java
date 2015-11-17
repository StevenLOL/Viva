/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.understanding;

/**
 *
 * @author Truy Thu
 */
public class DemoParameter {
    private String key;
    private String  value;
    
    public String getParameter() {
        return key;
    }
    
    public String getValue() {
        return value;
    }
    
    public DemoParameter() {
        key = "";
        value = "";
    }
    
    public DemoParameter(String param,String value) {
        this.key = param;
        this.value = value;
    }
    
}

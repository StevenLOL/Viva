/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.understanding;

/**
 *
 * @author Truy Thu
 */
public class DemoResult {
    
    private String functionCode;
    private String moduleCode;
    private DemoParameter[] paramaters;
    int i;
    
    
    /**
     * @function : constructor
     * @param _functionCode 
     */
    public DemoResult (String _functionCode, String _moduleCode) {
        functionCode = _functionCode;
        moduleCode = _moduleCode;
        paramaters = new DemoParameter[5];
        i = 0;
    }
    
    public DemoResult(){
        functionCode = "";
        moduleCode = "";
        paramaters = new DemoParameter[5];
        i = 0;
    }
    
    public String getfunctionCode() {
        return functionCode;
    }
    
    public void setFunction(String _functionCode, String _moduleCode) {
        functionCode = _functionCode;
        moduleCode = _moduleCode;
    }
    
    public String getModuleCode() {
        return moduleCode;
    }
    
    public DemoParameter[] getParameters() {
        return paramaters;
    } 
    
    public void addParameter(DemoParameter param) {
        paramaters[i] = param;
        i++;
    }
    
    public int getNumberofParameter() {
        return i;
    }
}

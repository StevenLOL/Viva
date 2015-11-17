///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mica.viva.understanding;
//
///**
// *
// * @author Truy Thu
// */
//public class Result {
//    
//    private String functionCode;
//    private String moduleCode;
//    private Parameter[] paramaters;
//    int i;
//    
//    
//    /**
//     * @funtion : constructor
//     * @param _funtionCode 
//     */
//    public Result (String _funtionCode, String _moduleCode) {
//        functionCode = _funtionCode;
//        moduleCode = _moduleCode;
//        paramaters = new Parameter[5];
//        i = 0;
//    }
//    
//    public Result(){
//        functionCode = "";
//        moduleCode = "";
//        paramaters = new Parameter[5];
//        i = 0;
//    }
//    
//    public String getFuntionCode() {
//        return functionCode;
//    }
//    
//    public void setFunction(String _funtionCode, String _moduleCode) {
//        functionCode = _funtionCode;
//        moduleCode = _moduleCode;
//    }
//    
//    public String getModuleCode() {
//        return moduleCode;
//    }
//    
//    public Parameter[] getParameters() {
//        return paramaters;
//    } 
//    
//    public void addParameter(Parameter param) {
//        paramaters[i] = param;
//        i++;
//    }
//    
//    public int getNumberofParameter() {
//        return i;
//    }
//}

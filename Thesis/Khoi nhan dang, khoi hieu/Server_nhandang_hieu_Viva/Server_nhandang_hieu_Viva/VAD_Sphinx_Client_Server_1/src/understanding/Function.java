/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package understanding;

/**
 *
 * @author Truy Thu
 */
public class Function {
    
    private String funtionCode;
    private String moduleCode;
    private Parameter[] paramaters;
    int i;
    
    
    /**
     * @funtion : constructor
     * @param _funtionCode 
     */
    public Function (String _funtionCode, String _moduleCode) {
        funtionCode = _funtionCode;
        moduleCode = _moduleCode;
        paramaters = new Parameter[5];
        i = 0;
    }
    
    public Function(){
        funtionCode = "";
        moduleCode = "";
        paramaters = new Parameter[5];
        i = 0;
    }
    
    public String getFuntionCode() {
        return funtionCode;
    }
    
    public void setFunction(String _funtionCode, String _moduleCode) {
        funtionCode = _funtionCode;
        moduleCode = _moduleCode;
    }
    
    public String getModuleCode() {
        return moduleCode;
    }
    
    public Parameter[] getParameters() {
        return paramaters;
    } 
    
    public void addParameter(Parameter param) {
        paramaters[i] = param;
        i++;
    }
    
    public int getNumberofParameter() {
        return i;
    }
}

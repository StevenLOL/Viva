package com.mica.viva.entity;

public class Module {
	private String moduleCode_;
	private String moduleName_;
	
	/**
	 * 
	 * @return
	 */
	public String getModuleCode()
	{
		return moduleCode_;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getModuleName()
	{		
		return moduleName_;
	}
	
	/**
	 * 
	 * @param moduleCode
	 * @param moduleName
	 */
	public Module(String moduleCode, String moduleName)
	{
		moduleCode_ = moduleCode;
		moduleName_ = moduleName;
	}
}

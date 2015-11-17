package com.mica.viva.entity;

public class Sentence {
	private String type_;
	private String content_;
	
	/**
	 * 
	 * @return
	 */
	public String getType()
	{
		return type_;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContent()
	{
		return content_;
	}
	
	/**
	 * 
	 * @param type
	 * @param content
	 */
	public Sentence(String type, String content){
		type_ = type;
		content_ = content;
	}
	
}

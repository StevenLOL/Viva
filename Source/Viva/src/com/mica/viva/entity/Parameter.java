package com.mica.viva.entity;

import com.mica.viva.resource.SentencesManager;

public class Parameter {
	private String key_;
	private String name_;
	private String requirementSentenceKey_;
	private boolean isRequired_;
	private Object value_;
	
	public Parameter(String key, String name, String requirementSentences,
			boolean isRequired) {
		key_ = key;
		name_ = name;
		requirementSentenceKey_ = requirementSentences;
		isRequired_ = isRequired;
		value_ = null;
	}
	
	public Parameter(String key, String name) {
		key_ = key;
		name_ = name;
		value_ = null;
	}

	public Object getValue() {
		return value_;
	}
	
	public void setValue(Object value) {
		value_ = value;
		if(value != null){
			if(value.toString().trim().equals("")){
				value_ = null;
			}
		}		
	}
	
	public String getKey() {
		return key_;
	}

	public String getName() {
		return name_;
	}

	public String getRequirementSentenceKey() {
		return requirementSentenceKey_;
	}

	public String getRequirementMessage(){
		return SentencesManager.getInstance().getRandom(requirementSentenceKey_).getContent();
	}
	
	public boolean isRequired() {
		return isRequired_;
	}
}

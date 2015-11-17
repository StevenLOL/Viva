package com.mica.viva.entity;

import java.util.ArrayList;

import android.R.integer;

import com.mica.viva.resource.SentencesManager;

public class Function {
	private String functionCode_;
	private String activity_;
	private String functionName_;
	private ArrayList<Parameter> parameters_;
	private ArrayList<Sentence> confirmSentences_;
	private ArrayList<Sentence> successSentences_;
	private ArrayList<Sentence> errorSentences_;
	private String confirmSentenceType_;
	private String successSentenceType_;
	private String errorSentenceType_;
	private Module module_;
	
	public Module getModule(){
		return module_;
	}
	
	public Function() {
	}

	/**
	 * 
	 * @return
	 */
	public String getFucntionCode() {
		return functionCode_;
	}

	/**
	 * 
	 * @return
	 */
	public String getActivity() {
		return activity_;
	}

	/**
	 * 
	 * @return
	 */
	public String getFucntionName() {
		return functionName_;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Parameter> getParameters() {
		return parameters_;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Sentence> getConfirmSentences() {
		return confirmSentences_;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Sentence> getSucessSentences() {
		return successSentences_;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Sentence> getErrorSentences() {
		return errorSentences_;
	}

	public String getConfirmSentenceType() {
		return confirmSentenceType_;
	}

	public String getSuccessSentenceType() {
		return successSentenceType_;
	}

	public String getErrorSentenceType() {
		return errorSentenceType_;
	}

	public int getFunctionHash() {
		return functionCode_.hashCode();
	}

	/**
	 * 
	 * @return
	 */
	public String getSuccessMessage() {
		String successMessage = SentencesManager.getInstance()
				.getRandom(successSentenceType_).getContent();
		for (Parameter param : parameters_) {
			if (param.getValue() != null
					&& successMessage.contains("{" + param.getKey() + "}")) {
				successMessage = successMessage.replace("{" + param.getKey()
						+ "}", param.getValue().toString());
			}
		}
		return successMessage;
	}

	/**
	 * 
	 * @return
	 */
	public String getErrorMessage() {
		String errorMessage = SentencesManager.getInstance()
				.getRandom(errorSentenceType_).getContent();
		for (Parameter param : parameters_) {
			if (param.getValue() != null
					&& errorMessage.contains("{" + param.getKey() + "}")) {
				errorMessage = errorMessage.replace("{" + param.getKey() + "}",
						param.getValue().toString());
			}
		}
		return errorMessage;
	}

	/**
	 * 
	 * @return
	 */
	public String getConfirmMessage() {
		String confirmMessage = SentencesManager.getInstance()
				.getRandom(confirmSentenceType_).getContent();
		for (Parameter param : parameters_) {
			if (param.getValue() != null
					&& confirmMessage.contains("{" + param.getKey() + "}")) {
				confirmMessage = confirmMessage.replace("{" + param.getKey()
						+ "}", param.getValue().toString());
			}
		}
		return confirmMessage;
	}

	/**
	 * 
	 * @param functionCode
	 * @param functionName
	 * @param activity
	 * @param parameters
	 * @param confirmSentences
	 * @param successSentences
	 * @param errorSentences
	 */
	public Function(String functionCode, String functionName, String activity,
			ArrayList<Parameter> parameters, String confirmSentences,
			String successSentences, String errorSentences, Module module) {
		functionCode_ = functionCode;
		functionName_ = functionName;
		activity_ = activity;
		parameters_ = parameters;
		confirmSentenceType_ = confirmSentences;
		successSentenceType_ = successSentences;
		errorSentenceType_ = errorSentences;

		confirmSentences_ = SentencesManager.getInstance().getSentences(
				confirmSentences);
		successSentences_ = SentencesManager.getInstance().getSentences(
				successSentences);
		errorSentences_ = SentencesManager.getInstance().getSentences(
				errorSentences);
		module_= module;
	}

	/**
	 * Clear function's parameters' value
	 */
	public void clearParametersValue() {
		for (Parameter param : getParameters()) {
			param.setValue(null);
		}
	}

	/**
	 * set value for parameter of function by key, return true if success, false
	 * if param's key not exist in this function
	 * 
	 * @param paramKey
	 * @param value
	 * @return
	 */
	public boolean setParameterValue(String paramKey, Object value) {
		if (paramKey != null) {
			for (Parameter param : getParameters()) {
				if (param.getKey().equals(paramKey.toUpperCase())) {
					param.setValue(value);
					return true;
				}
			}
		}

		return false;
	}

}

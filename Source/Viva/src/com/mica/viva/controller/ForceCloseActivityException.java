package com.mica.viva.controller;

public class ForceCloseActivityException extends Exception {
	public static final int REASON_USER_CANCEL = 0;
	public static final int REASON_INPUT_VOICE_ERROR = 1;
	
	private int reason_;
	
	public int getReason(){
		return reason_;
	}
	public ForceCloseActivityException(int reason){
		reason_ = reason;
	}
}

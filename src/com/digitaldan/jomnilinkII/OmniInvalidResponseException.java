package com.digitaldan.jomnilinkII;

public class OmniInvalidResponseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1431237320596739009L;
	
	private Message response;
	
	public OmniInvalidResponseException(Message response) {
		super();
		this.response = response;
	}

	public OmniInvalidResponseException(String message) {
		super(message);
	}
	
	public Message getInvalidResponse(){
		return response;
	}

}

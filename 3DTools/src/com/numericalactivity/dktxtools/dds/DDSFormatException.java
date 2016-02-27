package com.numericalactivity.dktxtools.dds;

public class DDSFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public DDSFormatException(String message) {
		this(message, null);
	}
	public DDSFormatException(String message, Throwable t) {
		super(message, t);
	}
	
}

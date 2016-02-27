package com.numericalactivity.dktxtools.ktx;

public class KTXFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public KTXFormatException(String message) {
        this(message, null);
    }

    public KTXFormatException(String message, Throwable t) {
        super(message, t);
    }

}

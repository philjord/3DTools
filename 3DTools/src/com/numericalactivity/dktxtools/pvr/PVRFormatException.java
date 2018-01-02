package com.numericalactivity.dktxtools.pvr;

public class PVRFormatException extends Exception {
    private static final long serialVersionUID = 1L;

    public PVRFormatException(String message) {
        this(message, null);
    }

    public PVRFormatException(String message, Throwable t) {
        super(message, t);
    }
}

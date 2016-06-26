package com.soikonomakis.rxfirebase.exceptions;

public class FirebaseDatabaseError extends Exception {

    private final int code;

    public FirebaseDatabaseError(int code, String detailMessage) {
        super(detailMessage);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

package com.company.aiinterview.common;

public enum ErrorCode {
    BAD_REQUEST(40001, "bad request"),
    NOT_FOUND(40401, "not found"),
    INTERNAL_ERROR(50000, "internal error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

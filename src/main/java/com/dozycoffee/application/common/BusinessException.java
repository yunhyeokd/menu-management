package com.dozycoffee.application.common;

public class BusinessException extends RuntimeException {

    private final BusinessCode businessCode;
    private final int errorCode;

    public BusinessException(BusinessCode businessCode, int errorCode, String message) {
        super(message);
        this.businessCode = businessCode;
        this.errorCode = errorCode;
    }

    public BusinessCode getBusinessCode() {
        return businessCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

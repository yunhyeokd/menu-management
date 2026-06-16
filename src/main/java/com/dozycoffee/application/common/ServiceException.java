package com.dozycoffee.application.common;

public class ServiceException extends RuntimeException {

    private final ServiceCode serviceCode;
    private final int errorCode;

    public ServiceException(ServiceCode serviceCode, int errorCode, String message) {
        super(message);
        this.serviceCode = serviceCode;
        this.errorCode = errorCode;
    }

    public String getServiceErrorCode() {
        return serviceCode + "_" + errorCode;
    }

}

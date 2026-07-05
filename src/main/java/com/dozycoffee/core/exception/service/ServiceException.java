package com.dozycoffee.core.exception.service;

public class ServiceException extends RuntimeException {

    private final ServiceCode serviceCode;
    private final int errorCode;

    public ServiceException(ServiceCode serviceCode, ServiceError error) {
        super(error.getMessage());
        this.serviceCode = serviceCode;
        this.errorCode = error.getErrorCode();
    }

    public ServiceCode getServiceCode() {
        return serviceCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

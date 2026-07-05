package com.dozycoffee.core.exception.service;

public class SystemException extends ServiceException {
    public SystemException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

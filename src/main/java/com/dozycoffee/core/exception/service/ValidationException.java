package com.dozycoffee.core.exception.service;

public class ValidationException extends ServiceException {
    public ValidationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

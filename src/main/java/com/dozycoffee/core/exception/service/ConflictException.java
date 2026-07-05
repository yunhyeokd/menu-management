package com.dozycoffee.core.exception.service;

public class ConflictException extends ServiceException {
    public ConflictException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

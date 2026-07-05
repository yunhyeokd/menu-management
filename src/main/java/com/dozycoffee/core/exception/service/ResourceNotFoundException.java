package com.dozycoffee.core.exception.service;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

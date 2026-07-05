package com.dozycoffee.core.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

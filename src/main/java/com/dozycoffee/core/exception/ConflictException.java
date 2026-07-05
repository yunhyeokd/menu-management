package com.dozycoffee.core.exception;

public class ConflictException extends AppException {
    public ConflictException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

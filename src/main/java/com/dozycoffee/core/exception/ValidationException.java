package com.dozycoffee.core.exception;

public class ValidationException extends AppException {
    public ValidationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

package com.dozycoffee.core.exception;

public class AuthorizationException extends AppException {
    public AuthorizationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

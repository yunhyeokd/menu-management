package com.dozycoffee.core.exception;

public class AuthenticationException extends AppException {
    public AuthenticationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

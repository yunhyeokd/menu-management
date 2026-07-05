package com.dozycoffee.core.exception.service;

public class AuthenticationException extends ServiceException {
    public AuthenticationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

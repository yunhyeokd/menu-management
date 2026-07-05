package com.dozycoffee.core.exception.service;

public class AuthorizationException extends ServiceException {
    public AuthorizationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

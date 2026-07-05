package com.dozycoffee.core.exception;

public class SystemException extends AppException {
    public SystemException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

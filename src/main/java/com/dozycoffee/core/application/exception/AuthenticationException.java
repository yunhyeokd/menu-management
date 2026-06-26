package com.dozycoffee.core.application.exception;

import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceCode;
import com.dozycoffee.core.application.ServiceError;

public class AuthenticationException extends AppException {
    public AuthenticationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

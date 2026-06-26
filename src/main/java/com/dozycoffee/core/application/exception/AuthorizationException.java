package com.dozycoffee.application.common.exception;

import com.dozycoffee.application.common.AppException;
import com.dozycoffee.application.common.ServiceCode;
import com.dozycoffee.application.common.ServiceError;

public class AuthorizationException extends AppException {
    public AuthorizationException(ServiceCode serviceCode, ServiceError error) {
        super(serviceCode, error);
    }
}

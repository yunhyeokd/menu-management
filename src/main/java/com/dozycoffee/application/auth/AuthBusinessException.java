package com.dozycoffee.application.auth;

import com.dozycoffee.application.common.BusinessCode;
import com.dozycoffee.application.common.BusinessException;

public class AuthBusinessException extends BusinessException {

    public AuthBusinessException(int errorCode, String message) {
        super(BusinessCode.AUTH, errorCode, message);
    }

    public static AuthBusinessException with(AuthErrors error) {
        return new AuthBusinessException(error.errorCode, error.message);
    }

}

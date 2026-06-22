package com.dozycoffee.application.admin.service;

import com.dozycoffee.application.common.BusinessCode;
import com.dozycoffee.application.common.BusinessException;

public class AdminBusinessException extends BusinessException {
    public AdminBusinessException(int errorCode, String message) {
        super(BusinessCode.ADM, errorCode, message);
    }

    public static AdminBusinessException of(AdminErrors error) {
        return new AdminBusinessException(error.errorCode, error.message);
    }
}

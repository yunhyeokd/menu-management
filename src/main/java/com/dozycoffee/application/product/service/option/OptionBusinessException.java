package com.dozycoffee.application.product.service.option;

import com.dozycoffee.application.common.BusinessCode;
import com.dozycoffee.application.common.BusinessException;

public class OptionBusinessException extends BusinessException {

    public OptionBusinessException(int errorCode, String message) {
        super(BusinessCode.OPT, errorCode, message);
    }

    public static OptionBusinessException of(OptionErrors error) {
        return new OptionBusinessException(error.errorCode, error.message);
    }

}

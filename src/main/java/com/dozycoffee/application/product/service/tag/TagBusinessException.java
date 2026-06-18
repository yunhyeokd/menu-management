package com.dozycoffee.application.product.service.tag;

import com.dozycoffee.application.common.BusinessException;
import com.dozycoffee.application.common.BusinessCode;

public class TagBusinessException extends BusinessException {

    public TagBusinessException() {
        super(BusinessCode.TAG, TagErrors.UNKNOWN_ERROR.errorCode, TagErrors.UNKNOWN_ERROR.message);
    }

    public TagBusinessException(int errorCode, String message) {
        super(BusinessCode.TAG, errorCode, message);
    }

    public static TagBusinessException with(TagErrors error) {
        return new TagBusinessException(error.errorCode, error.message);
    }
}

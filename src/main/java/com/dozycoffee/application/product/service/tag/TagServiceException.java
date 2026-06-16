package com.dozycoffee.application.product.service.tag;

import com.dozycoffee.application.common.ServiceException;
import com.dozycoffee.domain.common.DomainCode;

public class TagServiceException extends ServiceException {

    public TagServiceException() {
        super(DomainCode.TAG, TagErrors.UNKNOWN_ERROR.errorCode, TagErrors.UNKNOWN_ERROR.message);
    }

    public TagServiceException(int errorCode, String message) {
        super(DomainCode.TAG, errorCode, message);
    }

    public static TagServiceException with(TagErrors error) {
        return new TagServiceException(error.errorCode, error.message);
    }
}

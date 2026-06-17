package com.dozycoffee.application.product.service.category;

import com.dozycoffee.application.common.ServiceException;
import com.dozycoffee.domain.common.DomainCode;

public class CategoryServiceException extends ServiceException {

    public CategoryServiceException() {
        super(DomainCode.CAT, CategoryErrors.UNKNOWN_ERROR.errorCode, CategoryErrors.UNKNOWN_ERROR.message);
    }

    public CategoryServiceException(int errorCode, String message) {
        super(DomainCode.CAT, errorCode, message);
    }

    public static CategoryServiceException with(CategoryErrors error) {
        return new CategoryServiceException(error.errorCode, error.message);
    }

}

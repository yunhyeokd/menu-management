package com.dozycoffee.application.product.service.category;

import com.dozycoffee.application.common.BusinessException;
import com.dozycoffee.application.common.BusinessCode;

public class CategoryBusinessException extends BusinessException {

    public CategoryBusinessException() {
        super(BusinessCode.CAT, CategoryErrors.UNKNOWN_ERROR.errorCode, CategoryErrors.UNKNOWN_ERROR.message);
    }

    public CategoryBusinessException(int errorCode, String message) {
        super(BusinessCode.CAT, errorCode, message);
    }

    public static CategoryBusinessException with(CategoryErrors error) {
        return new CategoryBusinessException(error.errorCode, error.message);
    }

}

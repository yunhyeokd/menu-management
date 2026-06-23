package com.dozycoffee.application.product.service;

import com.dozycoffee.application.common.BusinessCode;
import com.dozycoffee.application.common.BusinessException;

public class ProductBusinessException extends BusinessException {

    public ProductBusinessException(int errorCode, String message) {
        super(BusinessCode.PRD, errorCode, message);
    }

    public static ProductBusinessException of(ProductErrors error) {
        return new ProductBusinessException(error.errorCode, error.message);
    }
}

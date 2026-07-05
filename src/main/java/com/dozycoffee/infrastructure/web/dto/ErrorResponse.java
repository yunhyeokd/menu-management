package com.dozycoffee.infrastructure.web.dto;

import com.dozycoffee.core.exception.service.ServiceCode;
import com.dozycoffee.core.exception.service.ServiceError;
import com.dozycoffee.core.exception.service.ServiceException;

import java.util.List;

public record ErrorResponse(
        String serviceCode,
        int errorCode,
        String message,
        List<FieldErrorDetail> fieldErrors
) {

    public static ErrorResponse from(ServiceException ex) {
        return new ErrorResponse(ex.getServiceCode().name(), ex.getErrorCode(), ex.getMessage(), null);
    }

    public static ErrorResponse of(ServiceCode serviceCode, ServiceError error) {
        return new ErrorResponse(serviceCode.name(), error.getErrorCode(), error.getMessage(), null);
    }

    public static ErrorResponse of(ServiceCode serviceCode, ServiceError error, List<FieldErrorDetail> fieldErrors) {
        return new ErrorResponse(serviceCode.name(), error.getErrorCode(), error.getMessage(), fieldErrors);
    }
}

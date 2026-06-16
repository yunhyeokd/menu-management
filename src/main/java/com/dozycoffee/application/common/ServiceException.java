package com.dozycoffee.application.common;

import com.dozycoffee.domain.common.DomainCode;

public class ServiceException extends RuntimeException {

    private final DomainCode domainCode;
    private final int errorCode;

    public ServiceException(DomainCode domainCode, int errorCode, String message) {
        super(message);
        this.domainCode = domainCode;
        this.errorCode = errorCode;
    }

    public DomainCode getDomainCode() {
        return domainCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

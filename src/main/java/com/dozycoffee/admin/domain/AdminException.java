package com.dozycoffee.admin.domain;

import com.dozycoffee.core.exception.base.DomainException;

public class AdminException extends DomainException {
    public AdminException() {
    }

    public AdminException(String message) {
        super(message);
    }

    public AdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdminException(Throwable cause) {
        super(cause);
    }

    public AdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.dozycoffee.core.id;

import com.dozycoffee.core.exception.base.DomainException;

public class IdentifierException extends DomainException {
    public IdentifierException() {
    }

    public IdentifierException(String message) {
        super(message);
    }

    public IdentifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentifierException(Throwable cause) {
        super(cause);
    }

    public IdentifierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

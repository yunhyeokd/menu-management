package com.dozycoffee.branch.domain;

import com.dozycoffee.core.domain.DomainException;

public class BranchException extends DomainException {
    public BranchException() {
    }

    public BranchException(String message) {
        super(message);
    }

    public BranchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BranchException(Throwable cause) {
        super(cause);
    }

    public BranchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

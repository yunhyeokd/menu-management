package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.common.ServiceException;
import com.dozycoffee.domain.common.DomainCode;

public class BranchServiceException extends ServiceException {

    public BranchServiceException(int errorCode, String message) {
        super(DomainCode.BRN, errorCode, message);
    }

    public static BranchServiceException with(BranchErrors error) {
        return new BranchServiceException(error.errorCode, error.message);
    }

}

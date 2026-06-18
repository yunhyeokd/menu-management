package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.common.BusinessException;
import com.dozycoffee.application.common.BusinessCode;

public class BranchBusinessException extends BusinessException {

    public BranchBusinessException(int errorCode, String message) {
        super(BusinessCode.BRN, errorCode, message);
    }

    public static BranchBusinessException with(BranchErrors error) {
        return new BranchBusinessException(error.errorCode, error.message);
    }

}

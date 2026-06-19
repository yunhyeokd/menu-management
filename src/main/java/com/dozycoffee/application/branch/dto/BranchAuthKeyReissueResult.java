package com.dozycoffee.application.branch.dto;

import com.dozycoffee.domain.branch.BranchCode;
import com.dozycoffee.domain.branch.BranchId;

public record BranchAuthKeyReissueResult(
        BranchId branchId,
        BranchCode branchCode,
        String rawAuthKey
) {
}

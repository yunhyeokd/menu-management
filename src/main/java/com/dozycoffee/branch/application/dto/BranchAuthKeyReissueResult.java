package com.dozycoffee.branch.application.dto;

import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;

public record BranchAuthKeyReissueResult(
        BranchId branchId,
        BranchCode branchCode,
        String rawAuthKey
) {
}

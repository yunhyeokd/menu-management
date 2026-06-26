package com.dozycoffee.branch.application.dto;

import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;

import java.time.Instant;

public record BranchCreateResult(
        BranchId branchId,
        BranchCode branchCode,
        String rawAuthKey,
        String name,
        String address,
        Instant createdAt
) {
}

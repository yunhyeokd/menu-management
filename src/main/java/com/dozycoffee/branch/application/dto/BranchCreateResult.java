package com.dozycoffee.application.branch.dto;

import com.dozycoffee.domain.branch.BranchCode;
import com.dozycoffee.domain.branch.BranchId;

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

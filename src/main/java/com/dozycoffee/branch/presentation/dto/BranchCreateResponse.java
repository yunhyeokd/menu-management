package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchCreateResult;

import java.time.Instant;

public record BranchCreateResponse(
        String branchId,
        String branchCode,
        String authKey,
        String name,
        String address,
        Instant createdAt
) {

    public static BranchCreateResponse from(BranchCreateResult result) {
        return new BranchCreateResponse(
                result.branchId().getValue(),
                result.branchCode().getValue(),
                result.rawAuthKey(),
                result.name(),
                result.address(),
                result.createdAt()
        );
    }


}

package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchCreateResult;

import java.time.Instant;

public record BranchCreateResponse(
        String branch_id,
        String branch_code,
        String auth_key,
        String name,
        String address,
        Instant created_at
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

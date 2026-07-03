package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.domain.Branch;

import java.time.Instant;

public record BranchProfileResponse(
        String branchId,
        String branchCode,
        String name,
        String address,
        String status,
        Instant createdAt
) {

    public static BranchProfileResponse from(Branch branch) {
        return new BranchProfileResponse(
                branch.getId().getValue(),
                branch.getCode().getValue(),
                branch.getName(),
                branch.getAddress(),
                branch.getStatus().name(),
                branch.getCreatedAt()
        );
    }
}

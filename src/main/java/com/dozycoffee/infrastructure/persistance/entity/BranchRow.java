package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.branch.domain.*;

import java.time.Instant;

public record BranchRow(
        String branchId,
        String code,
        String authKeyHash,
        String status,
        Instant createdAt,
        Instant deletedAt,
        String name,
        String address
) {

    public Branch toBranch() {
        return Branch.of(
                BranchId.of(branchId),
                BranchCode.of(code),
                authKeyHash,
                BranchStatus.of(status),
                createdAt,
                deletedAt,
                name,
                address
        );
    }
}
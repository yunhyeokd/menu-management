package com.dozycoffee.branch.application.repository;

import com.dozycoffee.branch.domain.BranchAccount;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;

import java.util.Optional;

public interface BranchAccountRepository {
    void save(BranchAccount branchAccount);
    Optional<BranchAccount> findById(BranchId branchId);
    Optional<BranchAccount> findByBranchCode(BranchCode branchCode);
    void deleteById(BranchId branchId);
}

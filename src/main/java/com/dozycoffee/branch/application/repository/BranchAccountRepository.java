package com.dozycoffee.application.branch.repository;

import com.dozycoffee.domain.branch.BranchAccount;
import com.dozycoffee.domain.branch.BranchCode;
import com.dozycoffee.domain.branch.BranchId;

import java.util.Optional;

public interface BranchAccountRepository {
    void save(BranchAccount branchAccount);
    Optional<BranchAccount> findById(BranchId branchId);
    Optional<BranchAccount> findByBranchCode(BranchCode branchCode);
    void deleteById(BranchId branchId);
}

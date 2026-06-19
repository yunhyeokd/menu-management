package com.dozycoffee.application.branch.repository;

import com.dozycoffee.domain.branch.BranchAccount;
import com.dozycoffee.domain.branch.BranchId;

public interface BranchAccountRepository {
    BranchAccount save(BranchAccount branchAccount);
    BranchAccount findById(BranchId branchId);
    void deleteById(BranchId branchId);
}

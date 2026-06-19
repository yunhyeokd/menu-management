package com.dozycoffee.application.branch.repository;

import com.dozycoffee.domain.branch.BranchAccount;

public interface BranchAccountRepository {
    BranchAccount findByName(String branchName);

    BranchAccount save(BranchAccount branchAccount);

    BranchAccount findById(long branchId);

    void deleteById(long branchId);
}

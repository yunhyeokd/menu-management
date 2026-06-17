package com.dozycoffee.application.branch.repository;

import com.dozycoffee.domain.branch.Branch;

public interface BranchRepository {
    Branch findByName(String branchName);

    Branch save(Branch branch);
}

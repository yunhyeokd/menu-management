package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchProfile;

public interface BranchProfileRepository {
    BranchProfile findById(long branchId) throws RepositoryException;

    void save(BranchProfile branchProfile) throws RepositoryException;

    void deleteById(long branchId);
}

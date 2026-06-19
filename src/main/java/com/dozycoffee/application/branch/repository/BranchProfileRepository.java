package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.BranchProfile;

public interface BranchProfileRepository {
    BranchProfile findById(BranchId branchId) throws RepositoryException;
    BranchProfile findByName(String name) throws RepositoryException;
    void save(BranchProfile branchProfile) throws RepositoryException;
    void deleteById(BranchId branchId);
}

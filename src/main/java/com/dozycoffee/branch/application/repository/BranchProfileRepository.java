package com.dozycoffee.branch.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.domain.BranchProfile;

import java.util.Optional;

public interface BranchProfileRepository {
    Optional<BranchProfile> findById(BranchId branchId) throws RepositoryException;
    Optional<BranchProfile> findByName(String name) throws RepositoryException;
    void save(BranchProfile branchProfile) throws RepositoryException;
    void deleteById(BranchId branchId) throws RepositoryException;
}

package com.dozycoffee.application.branch.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.BranchProfile;

import java.util.Optional;

public interface BranchProfileRepository {
    Optional<BranchProfile> findById(BranchId branchId) throws RepositoryException;
    Optional<BranchProfile> findByName(String name) throws RepositoryException;
    void save(BranchProfile branchProfile) throws RepositoryException;
    void deleteById(BranchId branchId) throws RepositoryException;
}

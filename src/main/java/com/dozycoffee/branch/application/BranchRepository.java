package com.dozycoffee.branch.application;

import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public interface BranchRepository {
    void save(Branch branch) throws RepositoryException;
    Optional<Branch> findById(BranchId branchId) throws RepositoryException;
    Optional<Branch> findByCode(BranchCode code) throws RepositoryException;
    Optional<Branch> findByName(String name) throws RepositoryException;
    List<Branch> findAll() throws RepositoryException;
    void deleteById(BranchId branchId) throws RepositoryException;
}

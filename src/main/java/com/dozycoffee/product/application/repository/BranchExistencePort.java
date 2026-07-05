package com.dozycoffee.product.application.repository;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;

public interface BranchExistencePort {
    boolean existsById(BranchId branchId) throws RepositoryException;
}

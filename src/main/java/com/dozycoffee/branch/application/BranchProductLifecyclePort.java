package com.dozycoffee.branch.application;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.RepositoryException;

public interface BranchProductLifecyclePort {
    void deactivateAllByBranchId(BranchId branchId) throws RepositoryException;
    void deleteAllByBranchId(BranchId branchId) throws RepositoryException;
}

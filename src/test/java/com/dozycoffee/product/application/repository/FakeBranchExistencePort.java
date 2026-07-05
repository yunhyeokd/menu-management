package com.dozycoffee.product.application.repository;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.RepositoryException;

import java.util.HashSet;
import java.util.Set;

public class FakeBranchExistencePort implements BranchExistencePort {

    private final Set<BranchId> existingIds = new HashSet<>();
    private boolean throwOnNext = false;

    public void register(BranchId branchId) {
        existingIds.add(branchId);
    }

    public void throwOnNextCall() {
        throwOnNext = true;
    }

    @Override
    public boolean existsById(BranchId branchId) throws RepositoryException {
        if (throwOnNext) {
            throwOnNext = false;
            throw new RepositoryException("fake error");
        }
        return existingIds.contains(branchId);
    }
}

package com.dozycoffee.branch.application;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.RepositoryException;

import java.util.HashSet;
import java.util.Set;

public class FakeBranchProductLifecyclePort implements BranchProductLifecyclePort {

    private final Set<BranchId> deactivated = new HashSet<>();
    private final Set<BranchId> deleted = new HashSet<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void deactivateAllByBranchId(BranchId branchId) {
        checkThrow();
        deactivated.add(branchId);
    }

    @Override
    public void deleteAllByBranchId(BranchId branchId) {
        checkThrow();
        deleted.add(branchId);
    }

    public boolean wasDeactivated(BranchId branchId) {
        return deactivated.contains(branchId);
    }

    public boolean wasDeleted(BranchId branchId) {
        return deleted.contains(branchId);
    }
}

package com.dozycoffee.infrastructure.adapter;

import com.dozycoffee.branch.application.BranchProductLifecyclePort;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.catalog.application.repository.ProductRepository;
import com.dozycoffee.catalog.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BranchProductLifecycleAdapter implements BranchProductLifecyclePort {

    private final ProductRepository productRepository;

    @Override
    public void deactivateAllByBranchId(BranchId branchId) throws RepositoryException {
        productRepository.updateStatusByBranchId(branchId, ProductStatus.INACTIVE);
    }

    @Override
    public void deleteAllByBranchId(BranchId branchId) throws RepositoryException {
        productRepository.deleteAllByBranchId(branchId);
    }
}

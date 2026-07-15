package com.dozycoffee.catalog.application.repository;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.core.exception.base.RepositoryException;

import java.util.List;
import java.util.Optional;

public interface ProductSalesOverrideRepository {
    void save(ProductSalesOverride productSalesOverride) throws RepositoryException;
    Optional<ProductSalesOverride> findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
    List<ProductSalesOverride> findAllByBranchId(BranchId branchId) throws RepositoryException;
    void deleteByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
}

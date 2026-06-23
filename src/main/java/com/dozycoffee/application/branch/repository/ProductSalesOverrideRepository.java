package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.ProductSalesOverride;
import com.dozycoffee.domain.product.ProductId;

import java.util.Optional;

public interface ProductSalesOverrideRepository {
    void save(ProductSalesOverride productSalesOverride) throws RepositoryException;
    Optional<ProductSalesOverride> findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
    void deleteByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
}

package com.dozycoffee.branch.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.domain.ProductSalesOverride;
import com.dozycoffee.product.domain.ProductId;

import java.util.Optional;

public interface ProductSalesOverrideRepository {
    void save(ProductSalesOverride productSalesOverride) throws RepositoryException;
    Optional<ProductSalesOverride> findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
    void deleteByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
}

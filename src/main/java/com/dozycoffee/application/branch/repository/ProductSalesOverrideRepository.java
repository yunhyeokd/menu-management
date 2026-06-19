package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.ProductSalesOverride;
import com.dozycoffee.domain.product.ProductId;

public interface ProductSalesOverrideRepository {
    void save(ProductSalesOverride productSalesOverride) throws RepositoryException;
    ProductSalesOverride findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException;
    ProductSalesOverride findById(Long id) throws RepositoryException;
    void deleteById(Long id) throws RepositoryException;
}

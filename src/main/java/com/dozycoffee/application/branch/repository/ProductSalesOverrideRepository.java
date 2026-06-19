package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.ProductSalesOverride;

import java.util.List;

public interface ProductSalesOverrideRepository {
    void save(ProductSalesOverride productSalesOverride) throws RepositoryException;
    ProductSalesOverride findByBranchIdAndProductId(long branchId, long productId) throws RepositoryException;
    void deleteById(long id) throws RepositoryException;

    List<Integer> findAllProductIdsByBranchId(long branchId) throws RepositoryException;

    ProductSalesOverride findById(long productSalesOverrideId) throws RepositoryException;
}

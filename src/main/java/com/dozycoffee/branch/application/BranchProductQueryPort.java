package com.dozycoffee.branch.application;

import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;
import java.util.Optional;

public interface BranchProductQueryPort {
    Optional<BranchProduct> findById(ProductId productId) throws RepositoryException;
    List<BranchProduct> findOverridableProducts(BranchId branchId) throws RepositoryException;
}

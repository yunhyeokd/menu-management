package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.product.CategoryId;
import com.dozycoffee.domain.product.Product;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product) throws RepositoryException;
    Optional<Product> findById(ProductId productId) throws RepositoryException;
    List<Product> findAllByCategoryId(CategoryId categoryId) throws RepositoryException;
    List<Product> findAllActiveCommon() throws RepositoryException;
    List<Product> findAllActiveBranchExclusive(BranchId branchId) throws RepositoryException;
    void updateStatusByBranchId(BranchId branchId, ProductStatus status) throws RepositoryException;
    void deleteAllByBranchId(BranchId branchId) throws RepositoryException;

    void deleteById(ProductId productId) throws RepositoryException;
}

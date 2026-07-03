package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.Product;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product) throws RepositoryException;

    boolean existsById(ProductId productId) throws RepositoryException;

    Optional<Product> findById(ProductId productId) throws RepositoryException;

    List<Product> findAllByCategoryId(CategoryId categoryId) throws RepositoryException;

    List<Product> findAllActiveCommon() throws RepositoryException;

    List<Product> findAllActiveBranchExclusive(BranchId branchId) throws RepositoryException;

    Optional<Product> findActiveById(ProductId productId) throws RepositoryException;

    void updateStatusByBranchId(BranchId branchId, ProductStatus status) throws RepositoryException;

    void deleteAllByBranchId(BranchId branchId) throws RepositoryException;

    void deleteById(ProductId productId) throws RepositoryException;
}

package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Product;
import com.dozycoffee.domain.product.ProductStatus;

import java.util.Collection;
import java.util.List;

public interface ProductRepository {

    public Product save(Product product) throws RepositoryException;
    public List<Product> findAllByCategoryId(long categoryId) throws RepositoryException;

    void updateStatusByBranchId(long branchId, ProductStatus productStatus) throws RepositoryException;

    List<Product> findAllActiveCommon() throws RepositoryException;
    List<Product> findAllActiveBranchExclusive(long branchId) throws RepositoryException;

    Product findById(long productId) throws RepositoryException;

    List<Product> findAllActiveNotInIds(List<Integer> productIds);

    void deleteAllByBranchId(long branchId);
}

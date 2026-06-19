package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.CategoryId;
import com.dozycoffee.domain.product.Product;

import java.util.List;

public interface ProductRepository {
    void save(Product product) throws RepositoryException;
    List<Product> findAllByCategoryId(CategoryId categoryId) throws RepositoryException;
}

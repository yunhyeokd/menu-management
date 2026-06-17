package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Product;

import java.util.List;

public interface ProductRepository {

    public Product save(Product product) throws RepositoryException;
    public List<Product> findAllByCategoryId(long categoryId) throws RepositoryException;

}

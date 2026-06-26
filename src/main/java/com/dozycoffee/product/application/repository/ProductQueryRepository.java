package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductFilterQuery;

import java.util.List;

public interface ProductQueryRepository {
    List<ProductDetailResult> findByFilter(ProductFilterQuery filter) throws RepositoryException;
}

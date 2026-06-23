package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.dto.ProductDetailResult;
import com.dozycoffee.application.product.dto.ProductFilterQuery;

import java.util.List;

public interface ProductQueryRepository {
    List<ProductDetailResult> findByFilter(ProductFilterQuery filter) throws RepositoryException;
}

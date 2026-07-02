package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;
import java.util.Map;

public interface ProductQueryRepository {
    List<ProductDetailResult> findByFilter(ProductFilterQuery filter) throws RepositoryException;
    Map<ProductId, List<TagData>> findTagsByProductIds(List<ProductId> productIds) throws RepositoryException;
}

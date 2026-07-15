package com.dozycoffee.catalog.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.catalog.application.dto.ProductDetailResult;
import com.dozycoffee.catalog.application.dto.ProductFilterQuery;
import com.dozycoffee.catalog.application.dto.ProductSummaryResult;
import com.dozycoffee.catalog.application.dto.TagData;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductQueryRepository {
    List<ProductSummaryResult> findByFilter(ProductFilterQuery filter) throws RepositoryException;
    Optional<ProductDetailResult> findDetailById(ProductId productId) throws RepositoryException;
    Map<ProductId, List<TagData>> findTagsByProductIds(List<ProductId> productIds) throws RepositoryException;
}

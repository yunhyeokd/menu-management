package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import com.dozycoffee.product.application.dto.ProductSummaryResult;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductQueryRepository {
    List<ProductSummaryResult> findByFilter(ProductFilterQuery filter) throws RepositoryException;
    Optional<ProductDetailResult> findDetailById(ProductId productId) throws RepositoryException;
    Map<ProductId, List<TagData>> findTagsByProductIds(List<ProductId> productIds) throws RepositoryException;
}

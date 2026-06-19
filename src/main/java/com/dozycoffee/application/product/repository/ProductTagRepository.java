package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.TagId;

import java.util.List;

public interface ProductTagRepository {
    void deleteAllByTagId(TagId tagId) throws RepositoryException;
    List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException;
}

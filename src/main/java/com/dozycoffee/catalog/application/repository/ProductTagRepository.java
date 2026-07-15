package com.dozycoffee.catalog.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductTag;
import com.dozycoffee.catalog.domain.Tag;
import com.dozycoffee.catalog.domain.TagId;

import java.util.List;

public interface ProductTagRepository {
    void save(ProductTag productTag) throws RepositoryException;
    void deleteAllByTagId(TagId tagId) throws RepositoryException;
    void deleteAllByProductId(ProductId productId) throws RepositoryException;
    List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException;
    List<Tag> findTagsByProductId(ProductId productId) throws RepositoryException;
}

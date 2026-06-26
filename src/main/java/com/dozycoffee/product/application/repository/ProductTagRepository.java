package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductTag;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;

import java.util.List;

public interface ProductTagRepository {
    void save(ProductTag productTag) throws RepositoryException;
    void deleteAllByTagId(TagId tagId) throws RepositoryException;
    void deleteAllByProductId(ProductId productId) throws RepositoryException;
    List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException;
    List<Tag> findTagsByProductId(ProductId productId) throws RepositoryException;
}

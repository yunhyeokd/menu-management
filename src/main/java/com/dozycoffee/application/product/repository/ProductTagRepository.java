package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.Tag;
import com.dozycoffee.domain.product.TagId;

import java.util.List;

public interface ProductTagRepository {
    void save(ProductTag productTag) throws RepositoryException;
    void deleteAllByTagId(TagId tagId) throws RepositoryException;
    void deleteAllByProductId(ProductId productId) throws RepositoryException;
    List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException;
    List<Tag> findTagsByProductId(ProductId productId) throws RepositoryException;
}

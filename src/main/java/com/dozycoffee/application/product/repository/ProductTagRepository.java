package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductTag;

import java.util.List;

public interface ProductTagRepository {
    void deleteAllByTagId(long tagId) throws RepositoryException;

    List<ProductTag> findAllByTagId(long tagId) throws RepositoryException;
}

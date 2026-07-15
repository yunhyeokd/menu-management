package com.dozycoffee.catalog.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductOptionGroup;

import java.util.List;
import java.util.Optional;

public interface ProductOptionGroupRepository {

    void save(ProductOptionGroup productOptionGroup) throws RepositoryException;
    List<ProductOptionGroup> findAllByOptionGroupId(OptionGroupId id) throws RepositoryException;
    Optional<ProductOptionGroup> findByProductIdAndOptionGroupId(ProductId productId, OptionGroupId optionGroupId) throws RepositoryException;
    void deleteAllByProductId(ProductId productId) throws RepositoryException;
}

package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductOptionGroup;

import java.util.List;
import java.util.Optional;

public interface ProductOptionGroupRepository {

    void save(ProductOptionGroup productOptionGroup) throws RepositoryException;
    List<ProductOptionGroup> findAllByOptionGroupId(OptionGroupId id) throws RepositoryException;
    Optional<ProductOptionGroup> findByProductIdAndOptionGroupId(ProductId productId, OptionGroupId optionGroupId) throws RepositoryException;
    void deleteAllByProductId(ProductId productId) throws RepositoryException;
}

package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.OptionGroupId;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductOptionGroup;
import com.dozycoffee.domain.product.ProductOptionGroupId;

import java.util.List;
import java.util.Optional;

public interface ProductOptionGroupRepository {

    void save(ProductOptionGroup productOptionGroup) throws RepositoryException;
    Optional<ProductOptionGroup> findById(ProductOptionGroupId id) throws RepositoryException;
    List<ProductOptionGroup> findAllByOptionGroupId(OptionGroupId id) throws RepositoryException;
    Optional<ProductOptionGroup> findByProductIdAndOptionGroupId(ProductId productId, OptionGroupId optionGroupId) throws RepositoryException;

    void deleteAllByProductId(ProductId productId) throws RepositoryException;
}

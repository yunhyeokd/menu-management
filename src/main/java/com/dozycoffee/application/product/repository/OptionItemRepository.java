package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.OptionGroupId;
import com.dozycoffee.domain.product.OptionItem;
import com.dozycoffee.domain.product.OptionItemId;

import java.util.List;
import java.util.Optional;

public interface OptionItemRepository {

    void save(OptionItem optionItem) throws RepositoryException;
    Optional<OptionItem> findById(OptionItemId id) throws RepositoryException;
    List<OptionItem> findAll() throws RepositoryException;
    List<OptionItem> findAllByOptionGroupId(OptionGroupId optionGroupId) throws RepositoryException;

    void deleteByOptionGroupId(OptionGroupId optionGroupId) throws RepositoryException;
}

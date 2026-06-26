package com.dozycoffee.application.product.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.OptionItem;
import com.dozycoffee.product.domain.OptionItemId;

import java.util.List;
import java.util.Optional;

public interface OptionItemRepository {

    void save(OptionItem optionItem) throws RepositoryException;
    Optional<OptionItem> findById(OptionItemId id) throws RepositoryException;
    List<OptionItem> findAll() throws RepositoryException;
    List<OptionItem> findAllByOptionGroupId(OptionGroupId optionGroupId) throws RepositoryException;

    void deleteByOptionGroupId(OptionGroupId optionGroupId) throws RepositoryException;
}

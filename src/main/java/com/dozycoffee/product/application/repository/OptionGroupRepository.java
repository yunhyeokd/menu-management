package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.product.domain.OptionGroup;
import com.dozycoffee.product.domain.OptionGroupId;

import java.util.List;
import java.util.Optional;

public interface OptionGroupRepository {

    void save(OptionGroup optionGroup) throws RepositoryException;
    Optional<OptionGroup> findById(OptionGroupId id) throws RepositoryException;
    List<OptionGroup> findAll() throws RepositoryException;

    void deleteById(OptionGroupId id) throws RepositoryException;
}

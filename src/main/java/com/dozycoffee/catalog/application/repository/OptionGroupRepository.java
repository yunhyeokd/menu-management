package com.dozycoffee.catalog.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.catalog.domain.OptionGroup;
import com.dozycoffee.catalog.domain.OptionGroupId;

import java.util.List;
import java.util.Optional;

public interface OptionGroupRepository {

    void save(OptionGroup optionGroup) throws RepositoryException;
    Optional<OptionGroup> findById(OptionGroupId id) throws RepositoryException;
    List<OptionGroup> findAll() throws RepositoryException;

    void deleteById(OptionGroupId id) throws RepositoryException;
}

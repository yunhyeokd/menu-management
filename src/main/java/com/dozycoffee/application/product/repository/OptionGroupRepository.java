package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.OptionGroup;
import com.dozycoffee.domain.product.OptionGroupId;

import java.util.List;
import java.util.Optional;

public interface OptionGroupRepository {

    void save(OptionGroup optionGroup) throws RepositoryException;
    Optional<OptionGroup> findById(OptionGroupId id) throws RepositoryException;
    List<OptionGroup> findAll() throws RepositoryException;

    void deleteById(OptionGroupId id) throws RepositoryException;
}

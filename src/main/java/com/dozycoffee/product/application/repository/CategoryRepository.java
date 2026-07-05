package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.product.domain.Category;
import com.dozycoffee.product.domain.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    void save(Category category) throws RepositoryException;
    List<Category> searchByName(String categoryName) throws RepositoryException;
    List<Category> findAll() throws RepositoryException;
    Optional<Category> findById(CategoryId id) throws RepositoryException;
    Optional<Category> findByName(String name) throws RepositoryException;
    void deleteById(CategoryId id) throws RepositoryException;
}

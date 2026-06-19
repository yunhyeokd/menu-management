package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.CategoryId;

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

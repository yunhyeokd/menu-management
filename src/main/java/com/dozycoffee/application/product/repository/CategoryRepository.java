package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Category;

import java.util.List;

public interface CategoryRepository {
    Category save(Category category) throws RepositoryException;
    List<Category> searchByName(String categoryName) throws RepositoryException;
    List<Category> findAll() throws RepositoryException;
    Category findById(long id) throws RepositoryException;
    Category findByName(String name) throws RepositoryException;
    void deleteById(long id) throws RepositoryException;
}

package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.CategoryRow;
import com.dozycoffee.infrastructure.persistance.mapper.CategoryMapper;
import com.dozycoffee.product.application.repository.CategoryRepository;
import com.dozycoffee.product.domain.Category;
import com.dozycoffee.product.domain.CategoryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisCategoryRepository implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public void save(Category category) throws RepositoryException {
        categoryMapper.upsert(toRow(category));
    }

    @Override
    public List<Category> searchByName(String categoryName) throws RepositoryException {
        return categoryMapper.searchByName(categoryName).stream()
                .map(CategoryRow::toCategory).toList();
    }

    @Override
    public List<Category> findAll() throws RepositoryException {
        return categoryMapper.findAll().stream()
                .map(CategoryRow::toCategory).toList();
    }

    @Override
    public Optional<Category> findById(CategoryId id) throws RepositoryException {
        return categoryMapper.findById(id.getValue()).map(CategoryRow::toCategory);
    }

    @Override
    public Optional<Category> findByName(String name) throws RepositoryException {
        return categoryMapper.findByName(name).map(CategoryRow::toCategory);
    }

    @Override
    public void deleteById(CategoryId id) throws RepositoryException {
        categoryMapper.deleteById(id.getValue());
    }

    private static CategoryRow toRow(Category category) {
        return new CategoryRow(
                category.getId().getValue(),
                category.getName(),
                category.getCreatedAt()
        );
    }
}

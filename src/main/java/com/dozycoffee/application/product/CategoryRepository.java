package com.dozycoffee.application.product;

import com.dozycoffee.domain.product.Category;

import java.util.List;

public interface CategoryRepository {
    Category save(Category category);
    List<Category> findAll();
    List<Category> findAllByName(String name);
    Category findById(long id);
    Category findByName(String name);
    void deleteById(long id);
}

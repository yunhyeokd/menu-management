package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.CategoryRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {

    void upsert(CategoryRow row);
    List<CategoryRow> searchByName(String name);
    List<CategoryRow> findAll();
    Optional<CategoryRow> findById(String categoryId);
    Optional<CategoryRow> findByName(String name);
    void deleteById(String categoryId);
}

package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.ProductTagRow;
import com.dozycoffee.infrastructure.persistance.entity.TagRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductTagMapper {

    void insert(ProductTagRow row);
    void deleteAllByTagId(String tagId);
    void deleteAllByProductId(String productId);
    List<ProductTagRow> findAllByTagId(String tagId);
    List<TagRow> findTagsByProductId(String productId);
}

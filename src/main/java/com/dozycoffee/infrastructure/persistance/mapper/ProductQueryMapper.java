package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.ProductOptionGroupItemRow;
import com.dozycoffee.infrastructure.persistance.entity.ProductTagNameRow;
import com.dozycoffee.infrastructure.persistance.entity.ProductWithCategoryRow;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductQueryMapper {

    List<ProductWithCategoryRow> findByFilter(ProductFilterQuery filter);
    List<ProductTagNameRow> findTagsByProductIds(List<String> productIds);
    List<ProductOptionGroupItemRow> findOptionGroupsByProductIds(List<String> productIds);
}

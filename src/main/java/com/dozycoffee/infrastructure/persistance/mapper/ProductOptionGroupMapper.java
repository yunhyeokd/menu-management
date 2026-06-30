package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.ProductOptionGroupRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductOptionGroupMapper {

    void upsert(ProductOptionGroupRow row);
    List<ProductOptionGroupRow> findAllByOptionGroupId(String optionGroupId);
    Optional<ProductOptionGroupRow> findByProductIdAndOptionGroupId(
            @Param("productId") String productId,
            @Param("optionGroupId") String optionGroupId
    );
    void deleteAllByProductId(String productId);
}

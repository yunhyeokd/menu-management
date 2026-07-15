package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.ProductSalesOverrideRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductSalesOverrideMapper {

    void upsert(ProductSalesOverrideRow row);
    Optional<ProductSalesOverrideRow> findByBranchIdAndProductId(
            @Param("branchId") String branchId,
            @Param("productId") String productId
    );
    List<ProductSalesOverrideRow> findAllByBranchId(@Param("branchId") String branchId);
    void deleteByBranchIdAndProductId(
            @Param("branchId") String branchId,
            @Param("productId") String productId
    );
}

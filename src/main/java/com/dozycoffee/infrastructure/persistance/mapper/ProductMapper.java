package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.ProductRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {

    void upsert(ProductRow row);
    boolean existsById(String productId);
    Optional<ProductRow> findById(String productId);
    List<ProductRow> findAllByCategoryId(String categoryId);
    List<ProductRow> findAllActiveCommon();
    List<ProductRow> findAllActiveBranchExclusive(String branchId);
    Optional<ProductRow> findActiveById(String productId);
    void updateStatusByBranchId(@Param("branchId") String branchId, @Param("status") String status);
    void deleteAllByBranchId(String branchId);
    void deleteById(String productId);
}

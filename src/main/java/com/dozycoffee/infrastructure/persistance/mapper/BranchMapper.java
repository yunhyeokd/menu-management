package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.BranchRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface BranchMapper {

    void upsert(BranchRow row);
    Optional<BranchRow> findById(String branchId);
    Optional<BranchRow> findByCode(String code);
    Optional<BranchRow> findByName(String name);
    void deleteById(String branchId);
}
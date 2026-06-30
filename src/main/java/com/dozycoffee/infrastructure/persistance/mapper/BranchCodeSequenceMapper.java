package com.dozycoffee.infrastructure.persistance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BranchCodeSequenceMapper {

    void initializeIfAbsent(@Param("year") int year);

    int lockAndGetSeq(@Param("year") int year);

    void incrementSeq(@Param("year") int year);
}

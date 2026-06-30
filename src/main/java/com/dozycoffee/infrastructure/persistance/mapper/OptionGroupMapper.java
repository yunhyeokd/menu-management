package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.OptionGroupRow;
import com.dozycoffee.infrastructure.persistance.entity.OptionItemRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OptionGroupMapper {

    void upsert(OptionGroupRow row);
    void insertItem(OptionItemRow row);
    void deleteItemsByGroupId(String optionGroupId);
    Optional<OptionGroupRow> findById(String optionGroupId);
    List<OptionItemRow> findItemsByGroupId(String optionGroupId);
    List<OptionGroupRow> findAll();
    void deleteById(String optionGroupId);
}

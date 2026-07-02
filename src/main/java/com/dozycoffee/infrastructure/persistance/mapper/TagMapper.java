package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.TagRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TagMapper {

    void upsert(TagRow row);
    boolean existsById(String tagId);
    List<TagRow> findAll();
    List<TagRow> searchByName(String name);
    Optional<TagRow> findById(String tagId);
    Optional<TagRow> findByName(String name);
    List<TagRow> findAllByNames(List<String> names);
    void deleteById(String tagId);
}

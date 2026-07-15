package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.OptionGroupRow;
import com.dozycoffee.infrastructure.persistance.entity.OptionItemRow;
import com.dozycoffee.infrastructure.persistance.mapper.OptionGroupMapper;
import com.dozycoffee.catalog.application.repository.OptionGroupRepository;
import com.dozycoffee.catalog.domain.OptionGroup;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.OptionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisOptionGroupRepository implements OptionGroupRepository {

    private final OptionGroupMapper optionGroupMapper;

    @Override
    @Transactional
    public void save(OptionGroup optionGroup) throws RepositoryException {
        optionGroupMapper.upsert(toGroupRow(optionGroup));
        optionGroupMapper.deleteItemsByGroupId(optionGroup.getId().getValue());
        List<OptionItem> items = optionGroup.getItems();
        for (int position = 0; position < items.size(); position++) {
            optionGroupMapper.insertItem(toItemRow(optionGroup.getId().getValue(), position, items.get(position)));
        }
    }

    @Override
    public Optional<OptionGroup> findById(OptionGroupId id) throws RepositoryException {
        return optionGroupMapper.findById(id.getValue()).map(row -> {
            List<OptionItem> items = optionGroupMapper.findItemsByGroupId(id.getValue()).stream()
                    .map(OptionItemRow::toOptionItem).toList();
            return OptionGroup.of(id, row.name(), row.description(), items, row.createdAt());
        });
    }

    @Override
    public List<OptionGroup> findAll() throws RepositoryException {
        return optionGroupMapper.findAll().stream().map(row -> {
            OptionGroupId id = OptionGroupId.of(row.optionGroupId());
            List<OptionItem> items = optionGroupMapper.findItemsByGroupId(row.optionGroupId()).stream()
                    .map(OptionItemRow::toOptionItem).toList();
            return OptionGroup.of(id, row.name(), row.description(), items, row.createdAt());
        }).toList();
    }

    @Override
    public void deleteById(OptionGroupId id) throws RepositoryException {
        optionGroupMapper.deleteById(id.getValue());
    }

    private static OptionGroupRow toGroupRow(OptionGroup optionGroup) {
        return new OptionGroupRow(
                optionGroup.getId().getValue(),
                optionGroup.getName(),
                optionGroup.getDescription(),
                optionGroup.getCreatedAt()
        );
    }

    private static OptionItemRow toItemRow(String optionGroupId, int position, OptionItem item) {
        return new OptionItemRow(optionGroupId, position, item.getName(), item.getDescription(), item.getPrice(), item.getCreatedAt());
    }
}

package com.dozycoffee.application.product.service.option;

import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.dto.*;
import com.dozycoffee.application.product.repository.OptionGroupRepository;
import com.dozycoffee.application.product.repository.OptionItemRepository;
import com.dozycoffee.application.product.repository.ProductOptionGroupRepository;
import com.dozycoffee.domain.product.*;

import java.util.*;

public class OptionService {

    private final OptionGroupRepository optionGroupRepository;
    private final OptionItemRepository optionItemRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final IdentifierGenerator<OptionGroupId> optionGroupIdGenerator;
    private final IdentifierGenerator<OptionItemId> optionItemIdGenerator;
    private final IdentifierGenerator<ProductOptionGroupId> productOptionGroupIdGenerator;

    public OptionService(
            OptionGroupRepository optionGroupRepository,
            OptionItemRepository optionItemRepository,
            ProductOptionGroupRepository productOptionGroupRepository,
            IdentifierGenerator<OptionGroupId> optionGroupIdGenerator,
            IdentifierGenerator<OptionItemId> optionItemIdGenerator,
            IdentifierGenerator<ProductOptionGroupId> productOptionGroupIdGenerator
    ) {
        this.optionGroupRepository = optionGroupRepository;
        this.optionItemRepository = optionItemRepository;
        this.productOptionGroupRepository = productOptionGroupRepository;
        this.optionGroupIdGenerator = optionGroupIdGenerator;
        this.optionItemIdGenerator = optionItemIdGenerator;
        this.productOptionGroupIdGenerator = productOptionGroupIdGenerator;
    }

    private OptionGroup getOptionGroup(OptionGroupId id) {
        return optionGroupRepository.findById(id).orElseThrow(() -> OptionBusinessException.of(OptionErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    private OptionGroupData composeOptionGroupData(OptionGroup optionGroup, List<OptionItem> optionItems) {
        List<OptionItemData> optionItemDatas = optionItems
                .stream()
                .map(OptionItemData::from)
                .toList();

        return new OptionGroupData(
                optionGroup.getId(),
                optionGroup.getName(),
                optionGroup.getDescription(),
                optionItemDatas,
                optionGroup.getCreatedAt()
        );
    }

    private OptionGroupData readOptionGroupData(OptionGroup optionGroup) {
        List<OptionItem> optionItems = optionItemRepository
                .findAllByOptionGroupId(optionGroup.getId());

        return composeOptionGroupData(optionGroup, optionItems);
    }

    public OptionGroupData create(OptionGroupCreateCommand command) {
        try {
            if (command.items().isEmpty()) {
                throw OptionBusinessException.of(OptionErrors.EMPTY_OPTION_GROUP_ERROR);
            }
            OptionGroupId optionGroupId = optionGroupIdGenerator.generate();
            OptionGroup optionGroup = OptionGroup.create(optionGroupId, command.name(), command.description().orElse(null));
            List<OptionItem> optionItems = new ArrayList<>();
            command.items().forEach(itemCreateCommand -> {
                OptionItemId optionItemId = optionItemIdGenerator.generate();
                OptionItem optionItem = OptionItem.create(
                        optionItemId,
                        optionGroupId,
                        itemCreateCommand.name(),
                        itemCreateCommand.description().orElse(null),
                        itemCreateCommand.price()
                );
                optionItems.add(optionItem);
            });

            optionGroupRepository.save(optionGroup);
            optionItems.forEach(optionItemRepository::save);
            return composeOptionGroupData(optionGroup, optionItems);
        } catch (ProductException e) {
            throw OptionBusinessException.of(OptionErrors.INVALID_OPTION_ERROR);
        } catch (RepositoryException e) {
            throw  OptionBusinessException.of(OptionErrors.UNKNOWN_ERROR);
        }
    }

    public List<OptionGroupData> findAll() {
        try {
            return optionGroupRepository
                    .findAll()
                    .stream()
                    .map(this::readOptionGroupData)
                    .toList();
        }
        catch (ProductException e) {
            throw OptionBusinessException.of(OptionErrors.INVALID_OPTION_ERROR);
        }
        catch (RepositoryException e) {
            throw OptionBusinessException.of(OptionErrors.UNKNOWN_ERROR);
        }
    }

    public void updateOptionGroupProfile(OptionGroupProfileUpdateCommand command) {
        try {
            OptionGroup optionGroup = getOptionGroup(command.optionGroupId());
            optionGroup.updateName(command.name());
            optionGroup.updateDescription(command.description().orElse(null));
            optionGroupRepository.save(optionGroup);
        }
        catch (ProductException e) {
            throw OptionBusinessException.of(OptionErrors.INVALID_OPTION_ERROR);
        }
        catch (RepositoryException e) {
            throw OptionBusinessException.of(OptionErrors.UNKNOWN_ERROR);
        }
    }

    public void updateOptionGroupItems(OptionGroupItemUpdateCommand command) {
        try {
            OptionGroup optionGroup = getOptionGroup(command.optionGroupId());

            if (command.items().isEmpty()) {
                throw OptionBusinessException.of(OptionErrors.EMPTY_OPTION_GROUP_ERROR);
            }
            List<OptionItem> optionItems = command.items().stream().map(itemUpdateCommand -> {
                OptionItemId optionItemId = optionItemIdGenerator.generate();
                return OptionItem.create(
                        optionItemId,
                        optionGroup.getId(),
                        itemUpdateCommand.name(),
                        itemUpdateCommand.description().orElse(null),
                        itemUpdateCommand.price()
                );
            }).toList();
            optionItemRepository.deleteByOptionGroupId(optionGroup.getId());
            optionItems.forEach(optionItemRepository::save);
        }
        catch (ProductException e) {
            throw OptionBusinessException.of(OptionErrors.INVALID_OPTION_ERROR);
        }
        catch (RepositoryException e) {
            throw OptionBusinessException.of(OptionErrors.UNKNOWN_ERROR);
        }
    }

    public void deleteOptionGroup(OptionGroupId id) {
        try {
            List<ProductOptionGroup> productOptionGroups = productOptionGroupRepository.findAllByOptionGroupId(id);
            if (!productOptionGroups.isEmpty()) {
                throw OptionBusinessException.of(OptionErrors.LINKED_PRODUCT_EXISTS_ERROR);
            }
            optionItemRepository.deleteByOptionGroupId(id);
            optionGroupRepository.deleteById(id);
        }
        catch (RepositoryException e) {
            throw OptionBusinessException.of(OptionErrors.UNKNOWN_ERROR);
        }
    }

}

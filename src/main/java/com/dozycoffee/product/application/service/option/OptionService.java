package com.dozycoffee.product.application.service.option;

import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.core.application.exception.ResourceNotFoundException;
import com.dozycoffee.core.application.exception.SystemException;
import com.dozycoffee.core.application.exception.ValidationException;
import com.dozycoffee.product.application.dto.OptionGroupCreateCommand;
import com.dozycoffee.product.application.dto.OptionGroupData;
import com.dozycoffee.product.application.dto.OptionGroupItemUpdateCommand;
import com.dozycoffee.product.application.dto.OptionGroupProfileUpdateCommand;
import com.dozycoffee.product.application.repository.OptionGroupRepository;
import com.dozycoffee.product.application.repository.OptionItemRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.*;

import java.util.*;

public class OptionService {

    private final OptionGroupRepository optionGroupRepository;
    private final OptionItemRepository optionItemRepository;
    private final IdentifierGenerator<OptionGroupId> optionGroupIdGenerator;
    private final IdentifierGenerator<OptionItemId> optionItemIdGenerator;

    public OptionService(
            OptionGroupRepository optionGroupRepository,
            OptionItemRepository optionItemRepository,
            IdentifierGenerator<OptionGroupId> optionGroupIdGenerator,
            IdentifierGenerator<OptionItemId> optionItemIdGenerator
    ) {
        this.optionGroupRepository = optionGroupRepository;
        this.optionItemRepository = optionItemRepository;
        this.optionGroupIdGenerator = optionGroupIdGenerator;
        this.optionItemIdGenerator = optionItemIdGenerator;
    }

    /*
    Id로 OptionGroup 조회
    !! OptionGroup이 존재하지 않을 시 예외 발생
     */
    private OptionGroup getOptionGroup(OptionGroupId id) {
        return optionGroupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    /*
    옵션그룹에 대해 모든 옵션 아이템을 받아와서 OptionGroupData로 결합
     */
    private OptionGroupData readOptionGroupData(OptionGroup optionGroup) {
        List<OptionItem> optionItems = optionItemRepository.findAllByOptionGroupId(optionGroup.getId());
        return OptionGroupData.from(optionGroup, optionItems);
    }

    /*
    옵션그룹과 옵션 아이템을 생성
    !! 옵션 아이템이 하나도 없을 경우 예외 발생
    !! 잘못된 옵션그룹 속성, 옵션 아이템 속성 입력시 예외 발생
     */
    public OptionGroupData create(OptionGroupCreateCommand command) {
        try {
            if (command.items().isEmpty()) {
                throw new ValidationException(ProductServiceCode.PRD, ProductErrors.EMPTY_OPTION_GROUP_ERROR);
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
            return OptionGroupData.from(optionGroup, optionItems);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_OPTION_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    /*
    모든 옵션 그룹과 각 옵션 그룹별 아이템을 불러와 OptionGroupData 리스트로 전달
     */
    public List<OptionGroupData> findAll() {
        try {
            return optionGroupRepository
                    .findAll()
                    .stream()
                    .map(this::readOptionGroupData)
                    .toList();
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_OPTION_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    /*
    옵션그룹명, 옵션그룹 설명을 업데이트
    !! 잘못된 옵션그룹명, 옵션그룹 설명 입력시 예외 발생
     */
    public void updateOptionGroupProfile(OptionGroupProfileUpdateCommand command) {
        try {
            OptionGroup optionGroup = getOptionGroup(command.optionGroupId());
            optionGroup.updateName(command.name());
            optionGroup.updateDescription(command.description().orElse(null));
            optionGroupRepository.save(optionGroup);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_OPTION_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    /*
    옵션그룹을 구성하는 아이템들을 입력받아 기존 아이템들을 제거하고 새로 구성
    !! 대상 옵션 그룹이 존재하지 않을 시 예외 발생
    !! 옵션 아이템이 하나도 없을 시 예외 발생
    !! 잘못된 옵션 아이템 속성 입력 시 예외 발생
     */
    public void updateOptionGroupItems(OptionGroupItemUpdateCommand command) {
        try {
            OptionGroup optionGroup = getOptionGroup(command.optionGroupId());

            if (command.items().isEmpty()) {
                throw new ValidationException(ProductServiceCode.PRD, ProductErrors.EMPTY_OPTION_GROUP_ERROR);
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
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_OPTION_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    /*
    옵션 그룹과 그 연관아이템들을 삭제
    * 연결된 상품 존재 여부 확인은 호출 전에 수행해야 함 (ProductOptionGroupService.assertNoLinkedProducts)
     */
    public void deleteOptionGroup(OptionGroupId id) {
        try {
            optionItemRepository.deleteByOptionGroupId(id);
            optionGroupRepository.deleteById(id);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}

package com.dozycoffee.product.application.service.option;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.core.exception.service.ValidationException;
import com.dozycoffee.product.application.dto.OptionGroupCreateCommand;
import com.dozycoffee.product.application.dto.OptionGroupData;
import com.dozycoffee.product.application.dto.OptionGroupItemUpdateCommand;
import com.dozycoffee.product.application.dto.OptionGroupProfileUpdateCommand;
import com.dozycoffee.product.application.repository.OptionGroupRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.*;

import java.util.*;

@Service
@Transactional
public class OptionService {

    private final OptionGroupRepository optionGroupRepository;
    private final IdentifierGenerator<OptionGroupId> optionGroupIdGenerator;

    public OptionService(
            OptionGroupRepository optionGroupRepository,
            IdentifierGenerator<OptionGroupId> optionGroupIdGenerator
    ) {
        this.optionGroupRepository = optionGroupRepository;
        this.optionGroupIdGenerator = optionGroupIdGenerator;
    }

    /*
    Id로 OptionGroup 조회
    !! OptionGroup이 존재하지 않을 시 예외 발생
     */
    private OptionGroup getOptionGroup(OptionGroupId id) {
        return optionGroupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    /*
    옵션그룹과 옵션 아이템을 생성
    !! 옵션 아이템이 하나도 없을 경우 예외 발생
    !! 잘못된 옵션그룹 속성, 옵션 아이템 속성 입력시 예외 발생
     */
    public OptionGroupData create(OptionGroupCreateCommand command) {
        try {
            OptionGroupId optionGroupId = optionGroupIdGenerator.generate();
            List<OptionItem> optionItems = command.items()
                    .stream()
                    .map(itemUpdateCommand -> OptionItem.create(
                            itemUpdateCommand.name(),
                            itemUpdateCommand.description(),
                            itemUpdateCommand.price()
                    )).toList();
            OptionGroup optionGroup = OptionGroup.create(optionGroupId, command.name(), command.description(), optionItems);
            optionGroupRepository.save(optionGroup);
            return OptionGroupData.from(optionGroup);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_OPTION_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    /*
    모든 옵션 그룹과 각 옵션 그룹별 아이템을 불러와 OptionGroupData 리스트로 전달
     */
    @Transactional(readOnly = true)
    public List<OptionGroupData> findAll() {
        try {
            return optionGroupRepository
                    .findAll()
                    .stream()
                    .map(OptionGroupData::from)
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
    public void updateOptionGroupProfile(OptionGroupId id, OptionGroupProfileUpdateCommand command) {
        try {
            OptionGroup optionGroup = getOptionGroup(id);
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
    public void updateOptionGroupItems(OptionGroupId id, OptionGroupItemUpdateCommand command) {
        try {
            OptionGroup optionGroup = getOptionGroup(id);

            List<OptionItem> optionItems = command.items()
                    .stream()
                    .map(itemUpdateCommand -> OptionItem.create(
                            itemUpdateCommand.name(),
                            itemUpdateCommand.description(),
                            itemUpdateCommand.price()
                    )).toList();
            optionGroup.replaceItems(optionItems);
            optionGroupRepository.save(optionGroup);
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
            optionGroupRepository.deleteById(id);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}

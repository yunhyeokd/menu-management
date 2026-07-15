package com.dozycoffee.catalog.application.service.option;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.catalog.application.ProductServiceCode;
import com.dozycoffee.catalog.application.dto.OptionGroupLinkCommand;
import com.dozycoffee.catalog.application.repository.OptionGroupRepository;
import com.dozycoffee.catalog.application.repository.ProductOptionGroupRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductOptionGroup;

import java.util.List;

@Service
@Transactional
public class ProductOptionGroupService {

    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final OptionGroupRepository optionGroupRepository;

    public ProductOptionGroupService(
            ProductOptionGroupRepository productOptionGroupRepository,
            OptionGroupRepository optionGroupRepository
    ) {
        this.productOptionGroupRepository = productOptionGroupRepository;
        this.optionGroupRepository = optionGroupRepository;
    }

    public void saveOptionGroups(ProductId productId, List<OptionGroupLinkCommand> specs) {
        try {
            for (OptionGroupLinkCommand spec : specs) {
                optionGroupRepository.findById(spec.optionGroupId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR)
                        );
                productOptionGroupRepository.save(ProductOptionGroup.create(
                        productId,
                        spec.optionGroupId(),
                        spec.isRequired(),
                        spec.allowMultiple()
                ));
            }
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void replaceOptionGroups(ProductId productId, List<OptionGroupLinkCommand> specs) {
        try {
            productOptionGroupRepository.deleteAllByProductId(productId);
            saveOptionGroups(productId, specs);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deleteAllByProductId(ProductId productId) {
        try {
            productOptionGroupRepository.deleteAllByProductId(productId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public void assertNoLinkedProducts(OptionGroupId optionGroupId) {
        try {
            List<ProductOptionGroup> linked = productOptionGroupRepository.findAllByOptionGroupId(optionGroupId);
            if (!linked.isEmpty()) {
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.LINKED_PRODUCT_EXISTS_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}

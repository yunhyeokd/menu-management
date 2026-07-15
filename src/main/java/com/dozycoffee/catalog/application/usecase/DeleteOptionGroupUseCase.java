package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.catalog.application.service.option.OptionService;
import com.dozycoffee.catalog.application.service.option.ProductOptionGroupService;
import com.dozycoffee.catalog.domain.OptionGroupId;

@Service
@Transactional
public class DeleteOptionGroupUseCase {

    private final ProductOptionGroupService productOptionGroupService;
    private final OptionService optionService;

    public DeleteOptionGroupUseCase(
            ProductOptionGroupService productOptionGroupService,
            OptionService optionService
    ) {
        this.productOptionGroupService = productOptionGroupService;
        this.optionService = optionService;
    }

    public void execute(OptionGroupId optionGroupId) {
        productOptionGroupService.assertNoLinkedProducts(optionGroupId);
        optionService.deleteOptionGroup(optionGroupId);
    }
}

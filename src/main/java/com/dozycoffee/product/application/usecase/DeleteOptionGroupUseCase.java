package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.service.option.OptionService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.domain.OptionGroupId;

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

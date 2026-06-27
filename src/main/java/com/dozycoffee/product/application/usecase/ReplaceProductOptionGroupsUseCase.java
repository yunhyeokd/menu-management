package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.dto.OptionGroupLinkSpec;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;

public class ReplaceProductOptionGroupsUseCase {

    private final ProductService productService;
    private final ProductOptionGroupService productOptionGroupService;

    public ReplaceProductOptionGroupsUseCase(
            ProductService productService,
            ProductOptionGroupService productOptionGroupService
    ) {
        this.productService = productService;
        this.productOptionGroupService = productOptionGroupService;
    }

    public void execute(ProductId productId, List<OptionGroupLinkSpec> specs) {
        productService.assertExists(productId);
        productOptionGroupService.replaceOptionGroups(productId, specs);
    }
}

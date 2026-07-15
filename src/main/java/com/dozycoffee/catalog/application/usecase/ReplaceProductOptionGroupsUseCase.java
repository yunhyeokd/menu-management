package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.catalog.application.dto.OptionGroupLinkCommand;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.application.service.option.ProductOptionGroupService;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.List;

@Service
@Transactional
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

    public void execute(ProductId productId, List<OptionGroupLinkCommand> specs) {
        productService.assertExists(productId);
        productOptionGroupService.replaceOptionGroups(productId, specs);
    }
}

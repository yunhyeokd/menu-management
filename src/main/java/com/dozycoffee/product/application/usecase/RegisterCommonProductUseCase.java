package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.dto.CommonProductRegisterCommand;
import com.dozycoffee.product.application.dto.ProductData;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.domain.Product;

import java.util.List;

public class RegisterCommonProductUseCase {

    private final ProductService productService;
    private final ProductTagService productTagService;
    private final ProductOptionGroupService productOptionGroupService;

    public RegisterCommonProductUseCase(
            ProductService productService,
            ProductTagService productTagService,
            ProductOptionGroupService productOptionGroupService
    ) {
        this.productService = productService;
        this.productTagService = productTagService;
        this.productOptionGroupService = productOptionGroupService;
    }

    public ProductData execute(CommonProductRegisterCommand command) {
        Product product = productService.registerCommonProduct(command);
        List<TagData> tags = productTagService.saveTags(product.getId(), command.tagNames());
        productOptionGroupService.saveOptionGroups(product.getId(), command.optionGroups());
        return ProductData.from(product, tags);
    }
}

package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.dto.ProductData;
import com.dozycoffee.product.application.dto.ProductRegisterCommand;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.domain.Product;

import java.util.List;

public class RegisterProductUseCase {

    private final ProductService productService;
    private final ProductTagService productTagService;
    private final ProductOptionGroupService productOptionGroupService;

    public RegisterProductUseCase(
            ProductService productService,
            ProductTagService productTagService,
            ProductOptionGroupService productOptionGroupService
    ) {
        this.productService = productService;
        this.productTagService = productTagService;
        this.productOptionGroupService = productOptionGroupService;
    }

    public ProductData execute(ProductRegisterCommand command) {
        Product product = productService.register(command);
        List<TagData> tags = productTagService.saveTags(product.getId(), command.tagNames());
        productOptionGroupService.saveOptionGroups(product.getId(), command.optionGroups());
        return ProductData.from(product, tags);
    }
}

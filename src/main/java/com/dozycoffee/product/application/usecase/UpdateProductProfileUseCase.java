package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.dto.ProductData;
import com.dozycoffee.product.application.dto.ProductProfileUpdateCommand;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.domain.Product;

import java.util.List;

public class UpdateProductProfileUseCase {

    private final ProductService productService;
    private final ProductTagService productTagService;

    public UpdateProductProfileUseCase(ProductService productService, ProductTagService productTagService) {
        this.productService = productService;
        this.productTagService = productTagService;
    }

    public ProductData execute(ProductProfileUpdateCommand command) {
        Product product = productService.updateProfile(command);
        List<TagData> tags = productTagService.replaceTags(product.getId(), command.tags());
        return ProductData.from(product, tags);
    }
}

package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.domain.ProductId;

public class DeleteProductUseCase {

    private final ProductService productService;
    private final ProductTagService productTagService;
    private final ProductOptionGroupService productOptionGroupService;

    public DeleteProductUseCase(
            ProductService productService,
            ProductTagService productTagService,
            ProductOptionGroupService productOptionGroupService
    ) {
        this.productService = productService;
        this.productTagService = productTagService;
        this.productOptionGroupService = productOptionGroupService;
    }

    public void execute(ProductId productId) {
        productTagService.deleteAllByProductId(productId);
        productOptionGroupService.deleteAllByProductId(productId);
        productService.deleteById(productId);
    }
}

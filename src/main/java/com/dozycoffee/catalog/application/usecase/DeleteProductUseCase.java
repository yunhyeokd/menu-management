package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.application.service.option.ProductOptionGroupService;
import com.dozycoffee.catalog.application.service.tag.ProductTagService;
import com.dozycoffee.catalog.domain.ProductId;

@Service
@Transactional
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
        productService.assertExists(productId);
        productTagService.deleteAllByProductId(productId);
        productOptionGroupService.deleteAllByProductId(productId);
        productService.deleteById(productId);
    }
}

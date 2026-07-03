package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.domain.ProductId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.product.application.dto.ProductSnapshot;
import com.dozycoffee.product.application.dto.ProductProfileUpdateCommand;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.domain.Product;

import java.util.List;

@Service
@Transactional
public class UpdateProductProfileUseCase {

    private final ProductService productService;
    private final ProductTagService productTagService;

    public UpdateProductProfileUseCase(ProductService productService, ProductTagService productTagService) {
        this.productService = productService;
        this.productTagService = productTagService;
    }

    public ProductSnapshot execute(ProductId productId, ProductProfileUpdateCommand command) {
        Product product = productService.updateProfile(productId, command);
        List<TagData> tags = productTagService.replaceTags(productId, command.tags());
        return ProductSnapshot.from(product, tags);
    }
}

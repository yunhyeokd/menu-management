package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.catalog.application.dto.ProductSnapshot;
import com.dozycoffee.catalog.application.dto.ProductRegisterCommand;
import com.dozycoffee.catalog.application.dto.TagData;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.application.service.option.ProductOptionGroupService;
import com.dozycoffee.catalog.application.service.tag.ProductTagService;
import com.dozycoffee.catalog.domain.Product;

import java.util.List;

@Service
@Transactional
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

    public ProductSnapshot execute(ProductRegisterCommand command) {
        Product product = productService.register(command);
        List<TagData> tags = productTagService.saveTags(product.getId(), command.tagNames());
        productOptionGroupService.saveOptionGroups(product.getId(), command.optionGroups());
        return ProductSnapshot.from(product, tags);
    }
}

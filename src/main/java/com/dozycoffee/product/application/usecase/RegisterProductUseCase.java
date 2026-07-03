package com.dozycoffee.product.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.product.application.dto.ProductSnapshot;
import com.dozycoffee.product.application.dto.ProductRegisterCommand;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.domain.Product;

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

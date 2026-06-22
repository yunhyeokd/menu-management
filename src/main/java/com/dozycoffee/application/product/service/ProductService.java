package com.dozycoffee.application.product.service;

import com.dozycoffee.application.branch.repository.BranchAccountRepository;
import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.dto.CommonProductRegisterCommand;
import com.dozycoffee.application.product.dto.ProductData;
import com.dozycoffee.application.product.dto.TagData;
import com.dozycoffee.application.product.repository.*;
import com.dozycoffee.application.product.service.tag.TagService;
import com.dozycoffee.application.product.service.ProductBusinessException;
import com.dozycoffee.application.product.service.ProductErrors;
import com.dozycoffee.domain.product.Product;
import com.dozycoffee.domain.product.ProductException;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final BranchAccountRepository branchAccountRepository;
    private final IdentifierGenerator<ProductId> idGenerator;
    private final TagService tagService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, ProductTagRepository productTagRepository, ProductOptionGroupRepository productOptionGroupRepository, BranchAccountRepository branchAccountRepository, IdentifierGenerator<ProductId> idGenerator, TagService tagService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.productTagRepository = productTagRepository;
        this.productOptionGroupRepository = productOptionGroupRepository;
        this.branchAccountRepository = branchAccountRepository;
        this.idGenerator = idGenerator;
        this.tagService = tagService;
    }

    public ProductData registerCommonProduct(CommonProductRegisterCommand command) {
        try {
            categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> ProductBusinessException.of(ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            ProductId productId = idGenerator.generate();
            Product product = Product.createCommonProduct(
                    productId,
                    command.name(),
                    command.description().orElse(null),
                    command.imageUrl().orElse(null),
                    command.categoryId(),
                    command.price(),
                    command.kcal().orElse(null),
                    command.allergenInfo().orElse(null)
            );
            List<TagData> tags = command.tagNames().stream()
                    .map(tagService::findOrCreate)
                    .map(TagData::from)
                    .toList();
            return ProductData.from(product, tags);
        } catch (ProductException e) {
            throw ProductBusinessException.of(ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

}

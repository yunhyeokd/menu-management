package com.dozycoffee.product.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.core.application.exception.SystemException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import com.dozycoffee.product.application.dto.ProductSearchCommand;
import com.dozycoffee.product.application.dto.ProductSummaryResult;
import com.dozycoffee.product.application.repository.TagRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchProductsUseCase {

    private final ProductService productService;
    private final TagRepository tagRepository;

    public SearchProductsUseCase(ProductService productService, TagRepository tagRepository) {
        this.productService = productService;
        this.tagRepository = tagRepository;
    }

    public List<ProductSummaryResult> execute(ProductSearchCommand command) {
        List<TagId> tagIds = resolveTagIds(command.tagNames());
        if (!command.tagNames().isEmpty() && tagIds.isEmpty()) {
            return List.of();
        }

        ProductFilterQuery filter = new ProductFilterQuery(
                command.name(),
                command.categoryIds(),
                tagIds,
                command.optionGroupIds(),
                command.statuses(),
                command.kinds(),
                command.branchId()
        );
        return productService.searchProducts(filter);
    }

    private List<TagId> resolveTagIds(List<String> tagNames) {
        if (tagNames.isEmpty()) return List.of();
        try {
            return tagRepository.findAllByNames(tagNames).stream().map(Tag::getId).toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}

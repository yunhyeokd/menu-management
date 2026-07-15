package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.catalog.application.ProductServiceCode;
import com.dozycoffee.catalog.application.dto.ProductFilterQuery;
import com.dozycoffee.catalog.application.dto.ProductSearchCommand;
import com.dozycoffee.catalog.application.dto.ProductSummaryResult;
import com.dozycoffee.catalog.application.repository.TagRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.domain.Tag;
import com.dozycoffee.catalog.domain.TagId;

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

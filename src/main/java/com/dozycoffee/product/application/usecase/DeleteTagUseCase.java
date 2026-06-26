package com.dozycoffee.product.application.usecase;

import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.application.service.tag.TagService;
import com.dozycoffee.product.domain.TagId;

public class DeleteTagUseCase {

    private final ProductTagService productTagService;
    private final TagService tagService;

    public DeleteTagUseCase(ProductTagService productTagService, TagService tagService) {
        this.productTagService = productTagService;
        this.tagService = tagService;
    }

    public void execute(TagId tagId) {
        productTagService.deleteAllByTagId(tagId);
        tagService.remove(tagId);
    }
}

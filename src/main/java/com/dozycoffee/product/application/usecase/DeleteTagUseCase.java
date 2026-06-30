package com.dozycoffee.product.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.application.service.tag.TagService;
import com.dozycoffee.product.domain.TagId;

@Service
@Transactional
public class DeleteTagUseCase {

    private final ProductTagService productTagService;
    private final TagService tagService;

    public DeleteTagUseCase(ProductTagService productTagService, TagService tagService) {
        this.productTagService = productTagService;
        this.tagService = tagService;
    }

    public void execute(TagId tagId) {
        tagService.assertExists(tagId);
        productTagService.deleteAllByTagId(tagId);
        tagService.remove(tagId);
    }
}

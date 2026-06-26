package com.dozycoffee.product.application.usecase;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.ConflictException;
import com.dozycoffee.core.application.exception.ResourceNotFoundException;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.service.option.OptionService;
import com.dozycoffee.product.application.service.option.ProductOptionGroupService;
import com.dozycoffee.product.application.service.tag.ProductTagService;
import com.dozycoffee.product.application.service.tag.TagService;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductUseCaseTest {

    private FakeProductRepository productRepository;
    private FakeProductQueryRepository productQueryRepository;
    private FakeCategoryRepository categoryRepository;
    private FakeBranchExistencePort branchExistencePort;
    private FakeProductTagRepository productTagRepository;
    private FakeTagRepository tagRepository;
    private FakeProductOptionGroupRepository productOptionGroupRepository;
    private FakeOptionGroupRepository optionGroupRepository;
    private FakeOptionItemRepository optionItemRepository;

    private RegisterCommonProductUseCase registerCommonProductUseCase;
    private RegisterBranchProductUseCase registerBranchProductUseCase;
    private UpdateProductProfileUseCase updateProductProfileUseCase;
    private DeleteProductUseCase deleteProductUseCase;
    private ReplaceProductOptionGroupsUseCase replaceProductOptionGroupsUseCase;
    private DeleteTagUseCase deleteTagUseCase;
    private DeleteOptionGroupUseCase deleteOptionGroupUseCase;

    private long nextProductId = 1L;
    private long nextTagId = 1L;
    private long nextOptionGroupId = 1L;
    private long nextOptionItemId = 1L;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        productQueryRepository = new FakeProductQueryRepository();
        categoryRepository = new FakeCategoryRepository();
        branchExistencePort = new FakeBranchExistencePort();
        productTagRepository = new FakeProductTagRepository();
        tagRepository = new FakeTagRepository();
        productOptionGroupRepository = new FakeProductOptionGroupRepository();
        optionGroupRepository = new FakeOptionGroupRepository();
        optionItemRepository = new FakeOptionItemRepository();

        nextProductId = 1L;
        nextTagId = 1L;
        nextOptionGroupId = 1L;
        nextOptionItemId = 1L;

        ProductService productService = new ProductService(
                productRepository, productQueryRepository, categoryRepository,
                branchExistencePort, () -> ProductId.of(nextProductId++)
        );
        TagService tagService = new TagService(tagRepository, () -> TagId.of(nextTagId++));
        ProductTagService productTagService = new ProductTagService(productTagRepository, tagService);
        ProductOptionGroupService productOptionGroupService = new ProductOptionGroupService(
                productOptionGroupRepository, optionGroupRepository
        );
        OptionService optionService = new OptionService(
                optionGroupRepository, optionItemRepository,
                () -> OptionGroupId.of(nextOptionGroupId++),
                () -> OptionItemId.of(nextOptionItemId++)
        );

        registerCommonProductUseCase = new RegisterCommonProductUseCase(productService, productTagService, productOptionGroupService);
        registerBranchProductUseCase = new RegisterBranchProductUseCase(productService, productTagService, productOptionGroupService);
        updateProductProfileUseCase = new UpdateProductProfileUseCase(productService, productTagService);
        deleteProductUseCase = new DeleteProductUseCase(productService, productTagService, productOptionGroupService);
        replaceProductOptionGroupsUseCase = new ReplaceProductOptionGroupsUseCase(productService, productOptionGroupService);
        deleteTagUseCase = new DeleteTagUseCase(productTagService, tagService);
        deleteOptionGroupUseCase = new DeleteOptionGroupUseCase(productOptionGroupService, optionService);
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Category defaultCategory() {
        return categoryRepository.put(Category.of(CategoryId.of(1L), "음료", Instant.now()));
    }

    private void defaultBranch() {
        branchExistencePort.register(BranchId.of(1L));
    }

    private OptionGroup savedOptionGroup(long id) {
        return optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(id), "옵션" + id, null, Instant.now()));
    }

    // ─── RegisterCommonProduct ────────────────────────────────────────────────

    @Test
    void 공통_상품을_태그와_옵션그룹과_함께_등록한다() {
        defaultCategory();
        OptionGroup og = savedOptionGroup(1L);

        ProductData result = registerCommonProductUseCase.execute(new CommonProductRegisterCommand(
                "아메리카노", null, null, CategoryId.of(1L), 3000, null, null,
                Set.of("신제품"), List.of(new OptionGroupLinkSpec(og.getId(), true, false))
        ));

        assertThat(result.name()).isEqualTo("아메리카노");
        assertThat(result.tags()).hasSize(1);
        assertThat(productTagRepository.all()).hasSize(1);
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(og.getId())).hasSize(1);
    }

    @Test
    void 공통_상품_등록시_옵션그룹이_없으면_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();

        assertThatThrownBy(() -> registerCommonProductUseCase.execute(new CommonProductRegisterCommand(
                "아메리카노", null, null, CategoryId.of(1L), 3000, null, null,
                Set.of(), List.of(new OptionGroupLinkSpec(OptionGroupId.of(999L), true, false))
        )))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── RegisterBranchProduct ────────────────────────────────────────────────

    @Test
    void 지점_전용_상품을_태그와_함께_등록한다() {
        defaultCategory();
        defaultBranch();

        ProductData result = registerBranchProductUseCase.execute(new BranchProductRegisterCommand(
                BranchId.of(1L), "지점전용라떼", null, null,
                CategoryId.of(1L), 4500, null, null,
                Set.of("프리미엄"), List.of()
        ));

        assertThat(result.name()).isEqualTo("지점전용라떼");
        assertThat(result.tags()).hasSize(1);
    }

    // ─── UpdateProductProfile ─────────────────────────────────────────────────

    @Test
    void 상품_프로필_수정시_태그도_교체된다() {
        defaultCategory();
        Tag oldTag = tagRepository.put(Tag.of(TagId.of(10L), "구태그", Instant.now()));
        Product product = productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        productTagRepository.add(ProductTag.of(product.getId(), oldTag.getId(), Instant.now()));

        ProductData result = updateProductProfileUseCase.execute(new ProductProfileUpdateCommand(
                ProductId.of(1L), "라떼", null, null,
                CategoryId.of(1L), 4000, null, null, Set.of("신태그")
        ));

        assertThat(result.tags()).hasSize(1);
        assertThat(result.tags().get(0).name()).isEqualTo("신태그");
        assertThat(productTagRepository.findAllByTagId(oldTag.getId())).isEmpty();
    }

    // ─── DeleteProduct ────────────────────────────────────────────────────────

    @Test
    void 상품_삭제시_태그와_옵션그룹_연결도_함께_제거된다() {
        OptionGroup og = savedOptionGroup(1L);
        Tag tag = tagRepository.put(Tag.of(TagId.of(10L), "신제품", Instant.now()));
        Product product = productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        productTagRepository.add(ProductTag.of(product.getId(), tag.getId(), Instant.now()));
        productOptionGroupRepository.put(ProductOptionGroup.of(product.getId(), og.getId(), true, false, Instant.now()));

        deleteProductUseCase.execute(product.getId());

        assertThat(productRepository.findById(product.getId())).isEmpty();
        assertThat(productTagRepository.all()).isEmpty();
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(og.getId())).isEmpty();
    }

    @Test
    void 상품_삭제시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> deleteProductUseCase.execute(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── ReplaceProductOptionGroups ───────────────────────────────────────────

    @Test
    void 상품_옵션그룹_구성을_교체한다() {
        OptionGroup oldOg = savedOptionGroup(1L);
        OptionGroup newOg = savedOptionGroup(2L);
        Product product = productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        productOptionGroupRepository.put(ProductOptionGroup.of(product.getId(), oldOg.getId(), true, false, Instant.now()));

        replaceProductOptionGroupsUseCase.execute(product.getId(), List.of(
                new OptionGroupLinkSpec(newOg.getId(), false, true)
        ));

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(oldOg.getId())).isEmpty();
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(newOg.getId())).hasSize(1);
    }

    @Test
    void 옵션그룹_교체시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> replaceProductOptionGroupsUseCase.execute(ProductId.of(999L), List.of()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── DeleteTag ────────────────────────────────────────────────────────────

    @Test
    void 태그_삭제시_연결된_상품_태그도_함께_제거된다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(10L), "신제품", Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(1L), tag.getId(), Instant.now()));

        deleteTagUseCase.execute(tag.getId());

        assertThat(tagRepository.findById(tag.getId())).isEmpty();
        assertThat(productTagRepository.findAllByTagId(tag.getId())).isEmpty();
    }

    @Test
    void 태그_삭제시_태그가_없으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> deleteTagUseCase.execute(TagId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── DeleteOptionGroup ────────────────────────────────────────────────────

    @Test
    void 연결된_상품_없으면_옵션그룹을_정상_삭제한다() {
        OptionGroup og = savedOptionGroup(1L);
        optionItemRepository.put(OptionItem.of(OptionItemId.of(1L), og.getId(), "S", null, 0, Instant.now()));

        deleteOptionGroupUseCase.execute(og.getId());

        assertThat(optionGroupRepository.contains(og.getId())).isFalse();
        assertThat(optionItemRepository.findAllByOptionGroupId(og.getId())).isEmpty();
    }

    @Test
    void 연결된_상품이_있으면_옵션그룹_삭제시_LINKED_PRODUCT_EXISTS_ERROR를_던진다() {
        OptionGroup og = savedOptionGroup(1L);
        productOptionGroupRepository.put(ProductOptionGroup.of(
                ProductId.of(1L), og.getId(), true, false, Instant.now()
        ));

        assertThatThrownBy(() -> deleteOptionGroupUseCase.execute(og.getId()))
                .isInstanceOf(ConflictException.class);
    }
}

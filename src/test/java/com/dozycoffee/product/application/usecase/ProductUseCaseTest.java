package com.dozycoffee.product.application.usecase;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.service.ServiceException;
import com.dozycoffee.core.exception.service.ServiceError;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
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

    private static final ProductId NON_EXISTENT_PRODUCT_ID = ProductId.of("00000000-0000-0000-0000-000000000999");
    private static final TagId NON_EXISTENT_TAG_ID = TagId.of("00000000-0000-0000-0000-000000000999");
    private static final OptionGroupId NON_EXISTENT_OPTION_GROUP_ID = OptionGroupId.of("00000000-0000-0000-0000-000000000999");

    private FakeProductRepository productRepository;
    private FakeProductQueryRepository productQueryRepository;
    private FakeCategoryRepository categoryRepository;
    private FakeBranchExistencePort branchExistencePort;
    private FakeProductTagRepository productTagRepository;
    private FakeTagRepository tagRepository;
    private FakeProductOptionGroupRepository productOptionGroupRepository;
    private FakeOptionGroupRepository optionGroupRepository;

    private RegisterProductUseCase registerProductUseCase;
    private UpdateProductProfileUseCase updateProductProfileUseCase;
    private DeleteProductUseCase deleteProductUseCase;
    private ReplaceProductOptionGroupsUseCase replaceProductOptionGroupsUseCase;
    private DeleteTagUseCase deleteTagUseCase;
    private DeleteOptionGroupUseCase deleteOptionGroupUseCase;
    private FindSellableProductsUseCase findSellableProductsUseCase;
    private SearchProductsUseCase searchProductsUseCase;

    private long nextProductId = 1L;
    private long nextTagId = 1L;
    private long nextOptionGroupId = 1L;

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

        nextProductId = 1L;
        nextTagId = 1L;
        nextOptionGroupId = 1L;

        ProductService productService = new ProductService(
                productRepository, productQueryRepository, categoryRepository,
                branchExistencePort, () -> ProductId.of(String.format("00000000-0000-0000-0000-%012d", nextProductId++))
        );
        TagService tagService = new TagService(tagRepository, () -> TagId.of(String.format("00000000-0000-0000-0000-%012d", nextTagId++)));
        ProductTagService productTagService = new ProductTagService(productTagRepository, tagService);
        ProductOptionGroupService productOptionGroupService = new ProductOptionGroupService(
                productOptionGroupRepository, optionGroupRepository
        );
        OptionService optionService = new OptionService(
                optionGroupRepository,
                () -> OptionGroupId.of(String.format("00000000-0000-0000-0000-%012d", nextOptionGroupId++))
        );

        registerProductUseCase = new RegisterProductUseCase(productService, productTagService, productOptionGroupService);
        updateProductProfileUseCase = new UpdateProductProfileUseCase(productService, productTagService);
        deleteProductUseCase = new DeleteProductUseCase(productService, productTagService, productOptionGroupService);
        replaceProductOptionGroupsUseCase = new ReplaceProductOptionGroupsUseCase(productService, productOptionGroupService);
        deleteTagUseCase = new DeleteTagUseCase(productTagService, tagService);
        deleteOptionGroupUseCase = new DeleteOptionGroupUseCase(productOptionGroupService, optionService);
        findSellableProductsUseCase = new FindSellableProductsUseCase(productService, productQueryRepository);
        searchProductsUseCase = new SearchProductsUseCase(productService, tagRepository);
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Category defaultCategory() {
        return categoryRepository.put(CategoryFixture.builder().name("음료").build());
    }

    private void defaultBranch() {
        branchExistencePort.register(BranchId.of("00000000-0000-0000-0000-000000000001"));
    }

    private OptionGroup savedOptionGroup(long id) {
        OptionGroupId optionGroupId = OptionGroupId.of(String.format("00000000-0000-0000-0000-%012d", id));
        return optionGroupRepository.put(OptionGroupFixture.builder()
                .id(optionGroupId)
                .name("옵션" + id)
                .build());
    }

    // ─── RegisterCommonProduct ────────────────────────────────────────────────

    @Test
    void 공통_상품을_태그와_옵션그룹과_함께_등록한다() {
        defaultCategory();
        OptionGroup og = savedOptionGroup(1L);

        ProductSnapshot result = registerProductUseCase.execute(new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null, CategoryFixture.Defaults.id, 3000, null, null,
                Set.of("신제품"), List.of(new OptionGroupLinkCommand(og.getId(), true, false))
        ));

        assertThat(result.name()).isEqualTo("아메리카노");
        assertThat(result.tags()).hasSize(1);
        assertThat(productTagRepository.all()).hasSize(1);
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(og.getId())).hasSize(1);
    }

    @Test
    void 지점_전용_상품을_태그와_함께_등록한다() {
        defaultCategory();
        defaultBranch();

        ProductSnapshot result = registerProductUseCase.execute(new ProductRegisterCommand(
                ProductKind.BRANCH_EXCLUSIVE, BranchId.of("00000000-0000-0000-0000-000000000001"),
                "지점전용라떼", null, null,
                CategoryFixture.Defaults.id, 4500, null, null,
                Set.of("프리미엄"), List.of()
        ));

        assertThat(result.name()).isEqualTo("지점전용라떼");
        assertThat(result.tags()).hasSize(1);
    }

    @Test
    void 상품_등록시_옵션그룹이_없으면_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();

        assertThatThrownBy(() -> registerProductUseCase.execute(new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null, CategoryFixture.Defaults.id, 3000, null, null,
                Set.of(), List.of(new OptionGroupLinkCommand(NON_EXISTENT_OPTION_GROUP_ID, true, false))
        )))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── UpdateProductProfile ─────────────────────────────────────────────────

    @Test
    void 상품_프로필_수정시_태그도_교체된다() {
        defaultCategory();
        ProductId productId = ProductFixture.Defaults.id;
        // 서비스가 새 태그에 발급하는 순번(...001부터)과 겹치지 않도록 별도 id 사용
        Tag oldTag = tagRepository.put(TagFixture.builder()
                .id(TagId.of("00000000-0000-0000-0000-000000000010"))
                .name("구태그")
                .build());
        Product product = productRepository.put(ProductFixture.builder()
                .id(productId).kind(ProductKind.COMMON).branchId(null).build());
        productTagRepository.add(ProductTag.of(product.getId(), oldTag.getId(), Instant.now()));

        ProductSnapshot result = updateProductProfileUseCase.execute(productId, new ProductProfileUpdateCommand(
                "라떼", null, null,
                CategoryFixture.Defaults.id, 4000, null, null, Set.of("신태그")
        ));

        assertThat(result.tags()).hasSize(1);
        assertThat(result.tags().get(0).name()).isEqualTo("신태그");
        assertThat(productTagRepository.findAllByTagId(oldTag.getId())).isEmpty();
    }

    // ─── DeleteProduct ────────────────────────────────────────────────────────

    @Test
    void 상품_삭제시_태그와_옵션그룹_연결도_함께_제거된다() {
        OptionGroup og = savedOptionGroup(1L);
        Tag tag = tagRepository.put(TagFixture.builder().name("신제품").build());
        Product product = productRepository.put(ProductFixture.builder()
                .id(ProductFixture.Defaults.id).kind(ProductKind.COMMON).branchId(null).build());
        productTagRepository.add(ProductTag.of(product.getId(), tag.getId(), Instant.now()));
        productOptionGroupRepository.put(ProductOptionGroup.of(product.getId(), og.getId(), true, false, Instant.now()));

        deleteProductUseCase.execute(product.getId());

        assertThat(productRepository.findById(product.getId())).isEmpty();
        assertThat(productTagRepository.all()).isEmpty();
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(og.getId())).isEmpty();
    }

    @Test
    void 상품_삭제시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> deleteProductUseCase.execute(NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── ReplaceProductOptionGroups ───────────────────────────────────────────

    @Test
    void 상품_옵션그룹_구성을_교체한다() {
        OptionGroup oldOg = savedOptionGroup(1L);
        OptionGroup newOg = savedOptionGroup(2L);
        Product product = productRepository.put(ProductFixture.builder()
                .id(ProductFixture.Defaults.id).kind(ProductKind.COMMON).branchId(null).build());
        productOptionGroupRepository.put(ProductOptionGroup.of(product.getId(), oldOg.getId(), true, false, Instant.now()));

        replaceProductOptionGroupsUseCase.execute(product.getId(), List.of(
                new OptionGroupLinkCommand(newOg.getId(), false, true)
        ));

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(oldOg.getId())).isEmpty();
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(newOg.getId())).hasSize(1);
    }

    @Test
    void 옵션그룹_교체시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> replaceProductOptionGroupsUseCase.execute(NON_EXISTENT_PRODUCT_ID, List.of()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── DeleteTag ────────────────────────────────────────────────────────────

    @Test
    void 태그_삭제시_연결된_상품_태그도_함께_제거된다() {
        Tag tag = tagRepository.put(TagFixture.builder().name("신제품").build());
        productTagRepository.add(ProductTag.of(ProductFixture.Defaults.id, tag.getId(), Instant.now()));

        deleteTagUseCase.execute(tag.getId());

        assertThat(tagRepository.findById(tag.getId())).isEmpty();
        assertThat(productTagRepository.findAllByTagId(tag.getId())).isEmpty();
    }

    @Test
    void 태그_삭제시_태그가_없으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> deleteTagUseCase.execute(NON_EXISTENT_TAG_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── DeleteOptionGroup ────────────────────────────────────────────────────

    @Test
    void 연결된_상품_없으면_옵션그룹을_정상_삭제한다() {
        OptionGroup og = savedOptionGroup(1L);

        deleteOptionGroupUseCase.execute(og.getId());

        assertThat(optionGroupRepository.contains(og.getId())).isFalse();
    }

    @Test
    void 연결된_상품이_있으면_옵션그룹_삭제시_LINKED_PRODUCT_EXISTS_ERROR를_던진다() {
        OptionGroup og = savedOptionGroup(1L);
        productOptionGroupRepository.put(ProductOptionGroup.of(
                ProductFixture.Defaults.id, og.getId(), true, false, Instant.now()
        ));

        assertThatThrownBy(() -> deleteOptionGroupUseCase.execute(og.getId()))
                .isInstanceOf(ConflictException.class);
    }

    // ─── FindSellableProducts ─────────────────────────────────────────────────

    @Test
    void 판매가능한_공통상품과_해당_지점의_전용상품을_함께_조회한다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        BranchId otherBranchId = BranchId.of("00000000-0000-0000-0000-000000000002");

        Product commonProduct = productRepository.put(ProductFixture.builder()
                .id(ProductFixture.Defaults.id)
                .status(ProductStatus.ACTIVE).kind(ProductKind.COMMON).branchId(null).build());
        Product exclusiveProduct = productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000002"))
                .status(ProductStatus.ACTIVE).kind(ProductKind.BRANCH_EXCLUSIVE).branchId(branchId).build());
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000003"))
                .status(ProductStatus.ACTIVE).kind(ProductKind.BRANCH_EXCLUSIVE).branchId(otherBranchId).build());
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000004"))
                .status(ProductStatus.INACTIVE).kind(ProductKind.COMMON).branchId(null).build());
        productQueryRepository.putTags(commonProduct.getId(),
                List.of(new TagData(TagFixture.Defaults.id, "신제품")));

        List<ProductSnapshot> result = findSellableProductsUseCase.execute(branchId);

        assertThat(result).extracting(ProductSnapshot::id)
                .containsExactlyInAnyOrder(commonProduct.getId(), exclusiveProduct.getId());
        assertThat(result.stream().filter(p -> p.id().equals(commonProduct.getId())).findFirst().orElseThrow().tags())
                .hasSize(1);
    }

    @Test
    void 판매가능한_상품이_없으면_빈_목록을_반환한다() {
        List<ProductSnapshot> result = findSellableProductsUseCase.execute(BranchId.of("00000000-0000-0000-0000-000000000001"));

        assertThat(result).isEmpty();
    }

    // ─── SearchProducts ───────────────────────────────────────────────────────

    @Test
    void 존재하지_않는_태그_이름으로_검색하면_빈_목록을_반환한다() {
        ProductSearchCommand command = new ProductSearchCommand(
                null, List.of(), List.of("존재하지않는태그"), List.of(), List.of(), List.of(), null
        );

        List<ProductSummaryResult> result = searchProductsUseCase.execute(command);

        assertThat(result).isEmpty();
    }

    @Test
    void 태그_조건_없이_검색하면_저장된_상품_목록을_반환한다() {
        productQueryRepository.add(new ProductSummaryResult(
                ProductFixture.Defaults.id, "아메리카노", null, null, 3000,
                ProductKind.COMMON, null, ProductStatus.ACTIVE, List.of()
        ));

        ProductSearchCommand command = new ProductSearchCommand(
                null, List.of(), List.of(), List.of(), List.of(), List.of(), null
        );

        List<ProductSummaryResult> result = searchProductsUseCase.execute(command);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("아메리카노");
    }

    @Test
    void 존재하는_태그_이름으로_검색하면_태그ID로_변환되어_조회에_위임된다() {
        tagRepository.put(TagFixture.builder().name("신제품").build());
        productQueryRepository.add(new ProductSummaryResult(
                ProductFixture.Defaults.id, "아메리카노", null, null, 3000,
                ProductKind.COMMON, null, ProductStatus.ACTIVE, List.of()
        ));

        ProductSearchCommand command = new ProductSearchCommand(
                null, List.of(), List.of("신제품"), List.of(), List.of(), List.of(), null
        );

        List<ProductSummaryResult> result = searchProductsUseCase.execute(command);

        assertThat(result).hasSize(1);
    }
}

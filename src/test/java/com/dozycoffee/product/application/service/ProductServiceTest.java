package com.dozycoffee.product.application.service;

import com.dozycoffee.branch.application.repository.FakeBranchAccountRepository;
import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.application.service.tag.TagService;
import com.dozycoffee.branch.domain.BranchAccount;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.domain.BranchStatus;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductServiceTest {

    private FakeProductRepository productRepository;
    private FakeProductQueryRepository productQueryRepository;
    private FakeCategoryRepository categoryRepository;
    private FakeTagRepository tagRepository;
    private FakeProductTagRepository productTagRepository;
    private FakeOptionGroupRepository optionGroupRepository;
    private FakeProductOptionGroupRepository productOptionGroupRepository;
    private FakeBranchAccountRepository branchAccountRepository;
    private ProductService productService;

    private long nextProductId = 1L;
    private long nextTagId = 1L;

    @BeforeEach
    public void setUp() {
        productRepository = new FakeProductRepository();
        productQueryRepository = new FakeProductQueryRepository();
        categoryRepository = new FakeCategoryRepository();
        tagRepository = new FakeTagRepository();
        productTagRepository = new FakeProductTagRepository();
        optionGroupRepository = new FakeOptionGroupRepository();
        productOptionGroupRepository = new FakeProductOptionGroupRepository();
        branchAccountRepository = new FakeBranchAccountRepository();
        nextProductId = 1L;
        nextTagId = 1L;

        TagService tagService = new TagService(tagRepository, productTagRepository, () -> TagId.of(nextTagId++));

        productService = new ProductService(
                productRepository,
                productQueryRepository,
                categoryRepository,
                tagRepository,
                productTagRepository,
                optionGroupRepository,
                productOptionGroupRepository,
                branchAccountRepository,
                () -> ProductId.of(nextProductId++),
                tagService
        );
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Category defaultCategory() {
        return categoryRepository.put(Category.of(CategoryId.of(1L), "음료", Instant.now()));
    }

    private BranchAccount defaultBranch() {
        return branchAccountRepository.put(BranchAccount.of(
                BranchId.of(1L),
                BranchCode.of("20260001"),
                "hash",
                BranchStatus.ACTIVE,
                Instant.now(),
                null
        ));
    }

    private OptionGroup defaultOptionGroup() {
        return optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
    }

    // ─── UC-PRD.1 공통 상품 생성 ────────────────────────────────────────────────

    @Test
    public void 공통_상품을_정상_생성한다() {
        defaultCategory();
        CommonProductRegisterCommand command = new CommonProductRegisterCommand(
                "아메리카노", null, null,
                CategoryId.of(1L), 3000, null, null,
                Set.of(), List.of()
        );

        ProductData result = productService.registerCommonProduct(command);

        assertThat(result.name()).isEqualTo("아메리카노");
        assertThat(result.id()).isEqualTo(ProductId.of(1L));
        assertThat(productRepository.findById(ProductId.of(1L))).isPresent();
    }

    @Test
    public void 공통_상품_생성시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        CommonProductRegisterCommand command = new CommonProductRegisterCommand(
                "아메리카노", null, null,
                CategoryId.of(999L), 3000, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.registerCommonProduct(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    @Test
    public void 공통_상품_생성시_태그가_없으면_자동_생성된다() {
        defaultCategory();
        CommonProductRegisterCommand command = new CommonProductRegisterCommand(
                "아메리카노", null, null,
                CategoryId.of(1L), 3000, null, null,
                Set.of("신제품"), List.of()
        );

        ProductData result = productService.registerCommonProduct(command);

        assertThat(result.tags()).hasSize(1);
        assertThat(result.tags().get(0).name()).isEqualTo("신제품");
    }

    @Test
    public void 공통_상품_생성시_옵션그룹을_함께_연결한다() {
        defaultCategory();
        defaultOptionGroup();
        List<OptionGroupLinkSpec> specs = List.of(new OptionGroupLinkSpec(OptionGroupId.of(1L), true, false));
        CommonProductRegisterCommand command = new CommonProductRegisterCommand(
                "아메리카노", null, null,
                CategoryId.of(1L), 3000, null, null,
                Set.of(), specs
        );

        productService.registerCommonProduct(command);

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(OptionGroupId.of(1L))).hasSize(1);
    }

    @Test
    public void 공통_상품_생성시_존재하지_않는_옵션그룹_연결_요청시_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();
        List<OptionGroupLinkSpec> specs = List.of(new OptionGroupLinkSpec(OptionGroupId.of(999L), true, false));
        CommonProductRegisterCommand command = new CommonProductRegisterCommand(
                "아메리카노", null, null,
                CategoryId.of(1L), 3000, null, null,
                Set.of(), specs
        );

        assertThatThrownBy(() -> productService.registerCommonProduct(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    // ─── UC-PRD.2.1 상품 필터 조회 ─────────────────────────────────────────────

    @Test
    public void 상품_필터_조회를_정상_수행한다() {
        productQueryRepository.add(new ProductDetailResult(
                ProductId.of(1L), "아메리카노", null, null,
                new CategoryData(CategoryId.of(1L), "음료"),
                3000, null, null,
                ProductKind.COMMON, null, ProductStatus.ACTIVE,
                List.of(), List.of(), Instant.now()
        ));

        List<ProductDetailResult> results = productService.searchProducts(ProductFilterQuery.empty());

        assertThat(results).hasSize(1);
    }

    @Test
    public void 상품_필터_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productQueryRepository.throwOnNextCall();

        assertThatThrownBy(() -> productService.searchProducts(ProductFilterQuery.empty()))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── UC-PRD.3/7 상품 수정 ──────────────────────────────────────────────────

    @Test
    public void 공통_상품_프로필을_정상_수정한다() {
        defaultCategory();
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                ProductId.of(1L), "라떼", null, null,
                CategoryId.of(1L), 4000, null, null, Set.of()
        );

        ProductData result = productService.updateProfile(command);

        assertThat(result.name()).isEqualTo("라떼");
        assertThat(result.price()).isEqualTo(4000);
    }

    @Test
    public void 상품_수정시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                ProductId.of(999L), "라떼", null, null,
                CategoryId.of(1L), 4000, null, null, Set.of()
        );

        assertThatThrownBy(() -> productService.updateProfile(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_수정시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                ProductId.of(1L), "라떼", null, null,
                CategoryId.of(999L), 4000, null, null, Set.of()
        );

        assertThatThrownBy(() -> productService.updateProfile(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    // ─── UC-PRD.4/8 상품 삭제 ──────────────────────────────────────────────────

    @Test
    public void 공통_상품을_정상_삭제한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());

        productService.deleteProduct(ProductId.of(1L));

        assertThat(productRepository.findById(ProductId.of(1L))).isEmpty();
    }

    @Test
    public void 상품_삭제시_옵션그룹_연결이_해제된다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        productOptionGroupRepository.put(ProductOptionGroup.of(
                ProductId.of(1L), OptionGroupId.of(1L), true, false, Instant.now()
        ));

        productService.deleteProduct(ProductId.of(1L));

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(OptionGroupId.of(1L))).isEmpty();
    }

    @Test
    public void 상품_삭제시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.deleteProduct(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    // ─── UC-PRD.5 지점 전용 상품 생성 ──────────────────────────────────────────

    @Test
    public void 지점_전용_상품을_정상_생성한다() {
        defaultCategory();
        defaultBranch();
        BranchProductRegisterCommand command = new BranchProductRegisterCommand(
                BranchId.of(1L), "지점전용라떼", null, null,
                CategoryId.of(1L), 4500, null, null,
                Set.of(), List.of()
        );

        ProductData result = productService.registerBranchProduct(command);

        assertThat(result.name()).isEqualTo("지점전용라떼");
        assertThat(productRepository.findById(ProductId.of(1L))).isPresent();
    }

    @Test
    public void 지점_전용_상품_생성시_지점이_없으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();
        BranchProductRegisterCommand command = new BranchProductRegisterCommand(
                BranchId.of(999L), "지점전용라떼", null, null,
                CategoryId.of(1L), 4500, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.registerBranchProduct(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    public void 지점_전용_상품_생성시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        defaultBranch();
        BranchProductRegisterCommand command = new BranchProductRegisterCommand(
                BranchId.of(1L), "지점전용라떼", null, null,
                CategoryId.of(999L), 4500, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.registerBranchProduct(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    // ─── UC-PRD.9 옵션 그룹 구성 변경 ──────────────────────────────────────────

    @Test
    public void 상품_옵션그룹_구성을_정상_교체한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        defaultOptionGroup();
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(2L), "온도", null, Instant.now()));
        productOptionGroupRepository.put(ProductOptionGroup.of(
                ProductId.of(1L), OptionGroupId.of(1L), true, false, Instant.now()
        ));

        List<OptionGroupLinkSpec> newSpecs = List.of(new OptionGroupLinkSpec(OptionGroupId.of(2L), false, true));
        productService.replaceOptionGroups(ProductId.of(1L), newSpecs);

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(OptionGroupId.of(1L))).isEmpty();
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(OptionGroupId.of(2L))).hasSize(1);
    }

    @Test
    public void 옵션그룹_구성_변경시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.replaceOptionGroups(ProductId.of(999L), List.of()))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 옵션그룹_구성_변경시_존재하지_않는_옵션그룹_포함시_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        List<OptionGroupLinkSpec> specs = List.of(new OptionGroupLinkSpec(OptionGroupId.of(999L), true, false));

        assertThatThrownBy(() -> productService.replaceOptionGroups(ProductId.of(1L), specs))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    // ─── UC-PRD.10 관리자 판매 상태 변경 ───────────────────────────────────────

    @Test
    public void 상품_상태를_활성으로_변경한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null)
                .status(ProductStatus.INACTIVE).build());

        productService.activate(ProductId.of(1L));

        assertThat(productRepository.findById(ProductId.of(1L)).get().getStatus())
                .isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    public void 상품_상태를_비활성으로_변경한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null)
                .status(ProductStatus.ACTIVE).build());

        productService.deactivate(ProductId.of(1L));

        assertThat(productRepository.findById(ProductId.of(1L)).get().getStatus())
                .isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    public void 상품_활성화시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.activate(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_비활성화시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.deactivate(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }
}

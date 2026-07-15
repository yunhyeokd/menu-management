package com.dozycoffee.catalog.application.usecase;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.dto.ProductOverrideResult;
import com.dozycoffee.catalog.application.repository.FakeBranchExistencePort;
import com.dozycoffee.catalog.application.repository.FakeCategoryRepository;
import com.dozycoffee.catalog.application.repository.FakeProductQueryRepository;
import com.dozycoffee.catalog.application.repository.FakeProductRepository;
import com.dozycoffee.catalog.application.repository.FakeProductSalesOverrideRepository;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.domain.Product;
import com.dozycoffee.catalog.domain.ProductFixture;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductKind;
import com.dozycoffee.catalog.domain.ProductStatus;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FindProductOverridesUseCaseTest {

    private static final BranchId BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000001");
    private static final BranchId OTHER_BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000002");
    private static final BranchId NON_EXISTENT_BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000999");

    private FakeProductRepository productRepository;
    private FakeProductSalesOverrideRepository productSalesOverrideRepository;
    private FakeBranchExistencePort branchExistencePort;
    private FindProductOverridesUseCase findProductOverridesUseCase;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        FakeProductQueryRepository productQueryRepository = new FakeProductQueryRepository();
        FakeCategoryRepository categoryRepository = new FakeCategoryRepository();
        branchExistencePort = new FakeBranchExistencePort();
        branchExistencePort.register(BRANCH_ID);
        productSalesOverrideRepository = new FakeProductSalesOverrideRepository();

        ProductService productService = new ProductService(
                productRepository, productQueryRepository, categoryRepository,
                branchExistencePort, () -> ProductId.of("00000000-0000-0000-0000-000000000123")
        );
        FindSellableProductsUseCase findSellableProductsUseCase =
                new FindSellableProductsUseCase(productService, productRepository, productQueryRepository);
        findProductOverridesUseCase = new FindProductOverridesUseCase(
                findSellableProductsUseCase, productSalesOverrideRepository, branchExistencePort
        );
    }

    @Test
    void 지점이_존재하지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> findProductOverridesUseCase.execute(NON_EXISTENT_BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 오버라이드가_없는_상품은_상태가_null로_조회된다() {
        Product common = productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000010"))
                .kind(ProductKind.COMMON).branchId(null).status(ProductStatus.ACTIVE).build());

        List<ProductOverrideResult> results = findProductOverridesUseCase.execute(BRANCH_ID);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(common.getId());
        assertThat(results.get(0).overrideStatus()).isNull();
    }

    @Test
    void 오버라이드가_있는_상품은_현재_상태가_반영된다() {
        Product common = productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000011"))
                .kind(ProductKind.COMMON).branchId(null).status(ProductStatus.ACTIVE).build());
        productSalesOverrideRepository.put(ProductSalesOverride.create(common.getId(), BRANCH_ID, ProductSalesOverrideStatus.HIDDEN));

        List<ProductOverrideResult> results = findProductOverridesUseCase.execute(BRANCH_ID);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).overrideStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 다른_지점_전용_상품은_포함되지_않는다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000012"))
                .kind(ProductKind.BRANCH_EXCLUSIVE).branchId(OTHER_BRANCH_ID).status(ProductStatus.ACTIVE).build());

        List<ProductOverrideResult> results = findProductOverridesUseCase.execute(BRANCH_ID);

        assertThat(results).isEmpty();
    }

    @Test
    void 해당_지점_전용_상품은_포함된다() {
        Product exclusive = productRepository.put(ProductFixture.builder()
                .id(ProductId.of("00000000-0000-0000-0000-000000000013"))
                .kind(ProductKind.BRANCH_EXCLUSIVE).branchId(BRANCH_ID).status(ProductStatus.ACTIVE).build());

        List<ProductOverrideResult> results = findProductOverridesUseCase.execute(BRANCH_ID);

        assertThat(results).extracting(ProductOverrideResult::id).containsExactly(exclusive.getId());
    }
}

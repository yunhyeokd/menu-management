package com.dozycoffee.catalog.application.usecase;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.dto.CategoryData;
import com.dozycoffee.catalog.application.dto.ProductDetailResult;
import com.dozycoffee.catalog.application.dto.SellableProductDetailResult;
import com.dozycoffee.catalog.application.dto.SellableProductResult;
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

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ResolveBranchCatalogUseCaseTest {

    private static final BranchId BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000001");
    private static final BranchId NON_EXISTENT_BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000999");

    private FakeProductRepository productRepository;
    private FakeProductQueryRepository productQueryRepository;
    private FakeProductSalesOverrideRepository productSalesOverrideRepository;
    private ResolveBranchCatalogUseCase resolveBranchCatalogUseCase;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        productQueryRepository = new FakeProductQueryRepository();
        FakeCategoryRepository categoryRepository = new FakeCategoryRepository();
        FakeBranchExistencePort branchExistencePort = new FakeBranchExistencePort();
        branchExistencePort.register(BRANCH_ID);
        productSalesOverrideRepository = new FakeProductSalesOverrideRepository();

        ProductService productService = new ProductService(
                productRepository, productQueryRepository, categoryRepository,
                branchExistencePort, () -> ProductId.of("00000000-0000-0000-0000-000000000123")
        );
        FindSellableProductsUseCase findSellableProductsUseCase =
                new FindSellableProductsUseCase(productService, productRepository, productQueryRepository);
        resolveBranchCatalogUseCase = new ResolveBranchCatalogUseCase(
                findSellableProductsUseCase, productService, productSalesOverrideRepository, branchExistencePort
        );
    }

    private Product commonProduct(ProductId id) {
        return productRepository.put(ProductFixture.builder()
                .id(id).kind(ProductKind.COMMON).branchId(null).status(ProductStatus.ACTIVE).build());
    }

    private void registerDetail(Product product) {
        productQueryRepository.add(new ProductDetailResult(
                product.getId(), product.getName(), product.getDescription(), product.getImageUrl(),
                CategoryData.from(product.getCategoryId()), product.getPrice(), product.getKcal(),
                product.getAllergenInfo(), product.getKind(), product.getBranchId(), product.getStatus(),
                List.of(), List.of(), Instant.now()
        ));
    }

    @Test
    void 지점이_존재하지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> resolveBranchCatalogUseCase.execute(NON_EXISTENT_BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 오버라이드가_없는_상품은_그대로_노출된다() {
        Product product = commonProduct(ProductId.of("00000000-0000-0000-0000-000000000020"));

        List<SellableProductResult> results = resolveBranchCatalogUseCase.execute(BRANCH_ID);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(product.getId());
        assertThat(results.get(0).soldOut()).isFalse();
    }

    @Test
    void HIDDEN_오버라이드된_상품은_목록에서_제외된다() {
        Product product = commonProduct(ProductId.of("00000000-0000-0000-0000-000000000021"));
        productSalesOverrideRepository.put(ProductSalesOverride.create(product.getId(), BRANCH_ID, ProductSalesOverrideStatus.HIDDEN));

        List<SellableProductResult> results = resolveBranchCatalogUseCase.execute(BRANCH_ID);

        assertThat(results).isEmpty();
    }

    @Test
    void SOLD_OUT_오버라이드된_상품은_품절_표시로_포함된다() {
        Product product = commonProduct(ProductId.of("00000000-0000-0000-0000-000000000022"));
        productSalesOverrideRepository.put(ProductSalesOverride.create(product.getId(), BRANCH_ID, ProductSalesOverrideStatus.SOLD_OUT));

        List<SellableProductResult> results = resolveBranchCatalogUseCase.execute(BRANCH_ID);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).soldOut()).isTrue();
    }

    @Test
    void 단건_조회는_상세_정보를_반환한다() {
        Product product = commonProduct(ProductId.of("00000000-0000-0000-0000-000000000023"));
        registerDetail(product);

        SellableProductDetailResult result = resolveBranchCatalogUseCase.execute(BRANCH_ID, product.getId());

        assertThat(result.id()).isEqualTo(product.getId());
        assertThat(result.soldOut()).isFalse();
    }

    @Test
    void 단건_조회시_품절이면_soldOut_플래그가_true다() {
        Product product = commonProduct(ProductId.of("00000000-0000-0000-0000-000000000024"));
        registerDetail(product);
        productSalesOverrideRepository.put(ProductSalesOverride.create(product.getId(), BRANCH_ID, ProductSalesOverrideStatus.SOLD_OUT));

        SellableProductDetailResult result = resolveBranchCatalogUseCase.execute(BRANCH_ID, product.getId());

        assertThat(result.soldOut()).isTrue();
    }

    @Test
    void 단건_조회시_HIDDEN이면_404를_던진다() {
        Product product = commonProduct(ProductId.of("00000000-0000-0000-0000-000000000025"));
        registerDetail(product);
        productSalesOverrideRepository.put(ProductSalesOverride.create(product.getId(), BRANCH_ID, ProductSalesOverrideStatus.HIDDEN));

        assertThatThrownBy(() -> resolveBranchCatalogUseCase.execute(BRANCH_ID, product.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 단건_조회시_판매_대상이_아니면_404를_던진다() {
        assertThatThrownBy(() -> resolveBranchCatalogUseCase.execute(BRANCH_ID, ProductId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

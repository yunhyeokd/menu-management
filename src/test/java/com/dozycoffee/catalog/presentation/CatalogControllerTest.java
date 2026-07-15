package com.dozycoffee.catalog.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.catalog.application.repository.CategoryRepository;
import com.dozycoffee.catalog.application.repository.ProductRepository;
import com.dozycoffee.catalog.domain.Category;
import com.dozycoffee.catalog.domain.CategoryFixture;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.Product;
import com.dozycoffee.catalog.domain.ProductFixture;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductKind;
import com.dozycoffee.catalog.domain.ProductStatus;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.support.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class CatalogControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IdentifierGenerator<ProductId> productIdGenerator;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IdentifierGenerator<CategoryId> categoryIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    @Test
    void 인증없이_조회하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/catalog"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SYSTEM_권한으로_조회하면_활성화된_공통_상품이_목록에_포함된다() throws Exception {
        String name = uniqueName();
        seedCommonProduct(name, ProductStatus.ACTIVE);

        mockMvc.perform(get("/catalog")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").exists());
    }

    @Test
    void 비활성화된_상품은_목록에_포함되지_않는다() throws Exception {
        String name = uniqueName();
        seedCommonProduct(name, ProductStatus.INACTIVE);

        mockMvc.perform(get("/catalog")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").doesNotExist());
    }

    @Test
    void 존재하지_않는_상품을_조회하면_404를_반환한다() throws Exception {
        ProductId unknownId = productIdGenerator.generate();

        mockMvc.perform(get("/catalog/{productId}", unknownId.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void 활성화된_공통_상품을_단건_조회하면_상세정보가_반환된다() throws Exception {
        String name = uniqueName();
        ProductId productId = seedCommonProduct(name, ProductStatus.ACTIVE);

        mockMvc.perform(get("/catalog/{productId}", productId.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    private ProductId seedCommonProduct(String name, ProductStatus status) {
        CategoryId categoryId = seedCategory();
        ProductId id = productIdGenerator.generate();
        Product product = ProductFixture.builder()
                .id(id)
                .categoryId(categoryId)
                .name(name)
                .branchId(null)
                .kind(ProductKind.COMMON)
                .status(status)
                .build();
        productRepository.save(product);
        return id;
    }

    private CategoryId seedCategory() {
        CategoryId id = categoryIdGenerator.generate();
        Category category = CategoryFixture.builder().id(id).name(uniqueCategoryName()).build();
        categoryRepository.save(category);
        return id;
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueName() {
        return "상품" + Math.floorMod(System.nanoTime(), 1_000_000);
    }

    private String uniqueCategoryName() {
        return "카테고리" + Math.floorMod(System.nanoTime(), 1_000_000);
    }
}

package com.dozycoffee.catalog.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.branch.application.BranchRepository;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchFixture;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.repository.CategoryRepository;
import com.dozycoffee.catalog.domain.Category;
import com.dozycoffee.catalog.domain.CategoryFixture;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.presentation.dto.ProductRegisterRequest;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.support.MockMvcIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class BranchCatalogControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private IdentifierGenerator<BranchId> branchIdGenerator;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IdentifierGenerator<CategoryId> categoryIdGenerator;

    @Autowired
    private IdentifierGenerator<ProductId> productIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 인증없이_카탈로그를_조회해도_200을_반환한다() throws Exception {
        BranchId branchId = seedBranch();

        mockMvc.perform(get("/branches/{branchId}/catalog", branchId.getValue()))
                .andExpect(status().isOk());
    }

    @Test
    void 존재하지_않는_지점의_카탈로그를_조회하면_404를_반환한다() throws Exception {
        BranchId unknownBranchId = branchIdGenerator.generate();

        mockMvc.perform(get("/branches/{branchId}/catalog", unknownBranchId.getValue()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void 활성화된_지점전용_상품이_카탈로그에_포함된다() throws Exception {
        BranchId branchId = seedBranch();
        String name = uniqueName();
        registerAndActivateProduct(branchId, name);

        mockMvc.perform(get("/branches/{branchId}/catalog", branchId.getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").exists());
    }

    @Test
    void 존재하지_않는_상품을_단건_조회하면_404를_반환한다() throws Exception {
        BranchId branchId = seedBranch();
        ProductId unknownProductId = productIdGenerator.generate();

        mockMvc.perform(get("/branches/{branchId}/catalog/{productId}", branchId.getValue(), unknownProductId.getValue()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void 인증없이_overrides를_조회하면_401을_반환한다() throws Exception {
        BranchId branchId = seedBranch();

        mockMvc.perform(get("/branches/{branchId}/catalog/overrides", branchId.getValue()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN_권한으로_overrides를_조회하면_403을_반환한다() throws Exception {
        BranchId branchId = seedBranch();

        mockMvc.perform(get("/branches/{branchId}/catalog/overrides", branchId.getValue())
                        .header("Authorization", "Bearer " + issueSession("admin-1", "ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void SYSTEM_권한으로_hide처리하면_카탈로그_목록에서_사라진다() throws Exception {
        BranchId branchId = seedBranch();
        String name = uniqueName();
        String productId = registerAndActivateProduct(branchId, name);
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(patch("/branches/{branchId}/catalog/overrides/{productId}/hide", branchId.getValue(), productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/branches/{branchId}/catalog", branchId.getValue()))
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").doesNotExist());
    }

    @Test
    void SYSTEM_권한으로_soldout처리하면_soldOut이_true로_표시된다() throws Exception {
        BranchId branchId = seedBranch();
        String name = uniqueName();
        String productId = registerAndActivateProduct(branchId, name);
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(patch("/branches/{branchId}/catalog/overrides/{productId}/sold-out", branchId.getValue(), productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/branches/{branchId}/catalog", branchId.getValue()))
                .andExpect(jsonPath("$[?(@.name == '" + name + "' && @.soldOut == true)]").exists());
    }

    @Test
    void SYSTEM_권한으로_restore_sale하면_soldOut_표시가_해제된다() throws Exception {
        BranchId branchId = seedBranch();
        String name = uniqueName();
        String productId = registerAndActivateProduct(branchId, name);
        String sessionId = issueSession("system-1", "SYSTEM");
        mockMvc.perform(patch("/branches/{branchId}/catalog/overrides/{productId}/sold-out", branchId.getValue(), productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/branches/{branchId}/catalog/overrides/{productId}/restore-sale", branchId.getValue(), productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/branches/{branchId}/catalog", branchId.getValue()))
                .andExpect(jsonPath("$[?(@.name == '" + name + "' && @.soldOut == false)]").exists());
    }

    private String registerAndActivateProduct(BranchId branchId, String name) throws Exception {
        CategoryId categoryId = seedCategory();
        String sessionId = issueSession("system-1", "SYSTEM");
        ProductRegisterRequest request = new ProductRegisterRequest(
                "BRANCH_EXCLUSIVE", branchId.getValue(), name, "설명", "https://www.dozycoffee.com",
                categoryId.getValue(), 1000, 100, "CASHEW", List.of(), List.of()
        );

        MvcResult result = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        String productId = body.get("productId").asText();

        mockMvc.perform(patch("/products/{productId}/activate", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        return productId;
    }

    private BranchId seedBranch() {
        BranchId id = branchIdGenerator.generate();
        Branch branch = BranchFixture.builder()
                .id(id)
                .code(uniqueBranchCode())
                .name(uniqueBranchName())
                .build();
        branchRepository.save(branch);
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

    private String uniqueBranchName() {
        return "테스트지점" + Math.floorMod(System.nanoTime(), 1_000_000);
    }

    private String uniqueBranchCode() {
        return "29" + String.format("%06d", Math.floorMod(System.nanoTime(), 1_000_000));
    }
}

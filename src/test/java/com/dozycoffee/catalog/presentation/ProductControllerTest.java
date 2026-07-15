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
import com.dozycoffee.catalog.presentation.dto.ProductModifyRequest;
import com.dozycoffee.catalog.presentation.dto.ProductOptionsReplaceRequest;
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
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ProductControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IdentifierGenerator<CategoryId> categoryIdGenerator;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private IdentifierGenerator<BranchId> branchIdGenerator;

    @Autowired
    private IdentifierGenerator<ProductId> productIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void BRANCH_권한으로_생성하면_403을_반환한다() throws Exception {
        ProductRegisterRequest request = registerRequest(seedCategory().getValue(), seedBranch().getValue(), uniqueName());

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + issueSession("branch-1", "BRANCH"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 필드_검증_실패시_400을_반환한다() throws Exception {
        ProductRegisterRequest request = registerRequest(seedCategory().getValue(), seedBranch().getValue(), "");

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceCode").value("COMMON"));
    }

    @Test
    void SYSTEM_권한으로_생성하면_INACTIVE_상태의_상품이_생성된다() throws Exception {
        String name = uniqueName();
        ProductRegisterRequest request = registerRequest(seedCategory().getValue(), seedBranch().getValue(), name);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void 등록한_상품이_목록_조회에_포함된다() throws Exception {
        String name = uniqueName();
        String productId = registerProduct(name);

        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productId == '" + productId + "')]").exists());
    }

    @Test
    void 존재하지_않는_상품을_조회하면_404를_반환한다() throws Exception {
        ProductId unknownId = productIdGenerator.generate();

        mockMvc.perform(get("/products/{productId}", unknownId.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void 등록한_상품을_단건_조회하면_상세정보가_반환된다() throws Exception {
        String name = uniqueName();
        String productId = registerProduct(name);

        mockMvc.perform(get("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void SYSTEM_권한으로_프로필을_수정하면_변경된_이름이_반영된다() throws Exception {
        String productId = registerProduct(uniqueName());
        String newName = uniqueName();
        CategoryId categoryId = seedCategory();
        ProductModifyRequest request = new ProductModifyRequest(
                newName, "수정된 설명", "https://www.dozycoffee.com", categoryId.getValue(),
                2000, 200, "CASHEW", Set.of()
        );

        mockMvc.perform(patch("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    void SYSTEM_권한으로_활성화하면_상태가_ACTIVE로_바뀐다() throws Exception {
        String productId = registerProduct(uniqueName());
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(patch("/products/{productId}/activate", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void SYSTEM_권한으로_비활성화하면_상태가_INACTIVE로_바뀐다() throws Exception {
        String productId = registerProduct(uniqueName());
        String sessionId = issueSession("system-1", "SYSTEM");
        mockMvc.perform(patch("/products/{productId}/activate", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/products/{productId}/deactivate", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void SYSTEM_권한으로_옵션을_교체하면_403을_반환한다() throws Exception {
        String productId = registerProduct(uniqueName());
        ProductOptionsReplaceRequest request = new ProductOptionsReplaceRequest(List.of());

        mockMvc.perform(put("/products/{productId}/options", productId)
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void SYSTEM_권한으로_삭제하면_이후_조회시_404를_반환한다() throws Exception {
        String productId = registerProduct(uniqueName());
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(delete("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNotFound());
    }

    private String registerProduct(String name) throws Exception {
        ProductRegisterRequest request = registerRequest(seedCategory().getValue(), seedBranch().getValue(), name);

        MvcResult result = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("productId").asText();
    }

    private ProductRegisterRequest registerRequest(String categoryId, String branchId, String name) {
        return new ProductRegisterRequest(
                "BRANCH_EXCLUSIVE", branchId, name, "설명", "https://www.dozycoffee.com",
                categoryId, 1000, 100, "CASHEW", List.of(), List.of()
        );
    }

    private CategoryId seedCategory() {
        CategoryId id = categoryIdGenerator.generate();
        Category category = CategoryFixture.builder().id(id).name(uniqueCategoryName()).build();
        categoryRepository.save(category);
        return id;
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

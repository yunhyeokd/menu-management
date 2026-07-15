package com.dozycoffee.catalog.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.catalog.application.repository.CategoryRepository;
import com.dozycoffee.catalog.domain.Category;
import com.dozycoffee.catalog.domain.CategoryFixture;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.presentation.dto.CategoryCreateRequest;
import com.dozycoffee.catalog.presentation.dto.CategoryUpdateRequest;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.support.MockMvcIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class CategoryControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IdentifierGenerator<CategoryId> categoryIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 인증없이_목록을_조회하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SYSTEM_권한으로_조회하면_등록된_카테고리가_목록에_포함된다() throws Exception {
        String name = uniqueName();
        Category category = CategoryFixture.builder()
                .id(categoryIdGenerator.generate())
                .name(name)
                .build();
        categoryRepository.save(category);

        mockMvc.perform(get("/categories")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").exists());
    }

    @Test
    void 존재하지_않는_카테고리를_조회하면_404를_반환한다() throws Exception {
        CategoryId unknownId = categoryIdGenerator.generate();

        mockMvc.perform(get("/categories/{categoryId}", unknownId.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void BRANCH_권한으로_생성하면_403을_반환한다() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest(uniqueName());

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + issueSession("branch-1", "BRANCH"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 필드_검증_실패시_400을_반환한다() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("");

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceCode").value("COMMON"));
    }

    @Test
    void SYSTEM_권한으로_생성하면_카테고리가_생성된다() throws Exception {
        String name = uniqueName();
        CategoryCreateRequest request = new CategoryCreateRequest(name);

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void 이미_존재하는_이름으로_생성하면_409를_반환한다() throws Exception {
        String name = uniqueName();
        Category category = CategoryFixture.builder()
                .id(categoryIdGenerator.generate())
                .name(name)
                .build();
        categoryRepository.save(category);

        CategoryCreateRequest request = new CategoryCreateRequest(name);

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void SYSTEM_권한으로_이름을_수정하면_변경된_이름이_반환된다() throws Exception {
        CategoryId id = categoryIdGenerator.generate();
        Category category = CategoryFixture.builder().id(id).name(uniqueName()).build();
        categoryRepository.save(category);
        String newName = uniqueName();
        CategoryUpdateRequest request = new CategoryUpdateRequest(newName);

        mockMvc.perform(patch("/categories/{categoryId}", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    void SYSTEM_권한으로_삭제하면_이후_조회시_404를_반환한다() throws Exception {
        CategoryId id = categoryIdGenerator.generate();
        Category category = CategoryFixture.builder().id(id).name(uniqueName()).build();
        categoryRepository.save(category);
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(delete("/categories/{categoryId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/categories/{categoryId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNotFound());
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueName() {
        return "카테고리" + Math.floorMod(System.nanoTime(), 1_000_000);
    }
}

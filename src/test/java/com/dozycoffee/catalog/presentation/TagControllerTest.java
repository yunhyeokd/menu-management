package com.dozycoffee.catalog.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.catalog.application.repository.TagRepository;
import com.dozycoffee.catalog.domain.Tag;
import com.dozycoffee.catalog.domain.TagFixture;
import com.dozycoffee.catalog.domain.TagId;
import com.dozycoffee.catalog.presentation.dto.TagCreateRequest;
import com.dozycoffee.catalog.presentation.dto.TagRenameRequest;
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
class TagControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private IdentifierGenerator<TagId> tagIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 인증없이_목록을_조회하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/tags"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SYSTEM_권한으로_조회하면_등록된_태그가_목록에_포함된다() throws Exception {
        String name = uniqueName();
        Tag tag = TagFixture.builder().id(tagIdGenerator.generate()).name(name).build();
        tagRepository.save(tag);

        mockMvc.perform(get("/tags")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").exists());
    }

    @Test
    void BRANCH_권한으로_생성하면_403을_반환한다() throws Exception {
        TagCreateRequest request = new TagCreateRequest(uniqueName());

        mockMvc.perform(post("/tags")
                        .header("Authorization", "Bearer " + issueSession("branch-1", "BRANCH"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 필드_검증_실패시_400을_반환한다() throws Exception {
        TagCreateRequest request = new TagCreateRequest("");

        mockMvc.perform(post("/tags")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceCode").value("COMMON"));
    }

    @Test
    void ADMIN_권한으로_생성하면_태그가_생성된다() throws Exception {
        String name = uniqueName();
        TagCreateRequest request = new TagCreateRequest(name);

        mockMvc.perform(post("/tags")
                        .header("Authorization", "Bearer " + issueSession("admin-1", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void 이미_존재하는_이름으로_생성하면_409를_반환한다() throws Exception {
        String name = uniqueName();
        Tag tag = TagFixture.builder().id(tagIdGenerator.generate()).name(name).build();
        tagRepository.save(tag);

        TagCreateRequest request = new TagCreateRequest(name);

        mockMvc.perform(post("/tags")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.serviceCode").value("PRD"));
    }

    @Test
    void SYSTEM_권한으로_이름을_변경하면_204를_반환한다() throws Exception {
        TagId id = tagIdGenerator.generate();
        Tag tag = TagFixture.builder().id(id).name(uniqueName()).build();
        tagRepository.save(tag);
        TagRenameRequest request = new TagRenameRequest(uniqueName());

        mockMvc.perform(patch("/tags/{tagId}", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void SYSTEM_권한으로_삭제하면_403을_반환한다() throws Exception {
        TagId id = tagIdGenerator.generate();
        Tag tag = TagFixture.builder().id(id).name(uniqueName()).build();
        tagRepository.save(tag);

        mockMvc.perform(delete("/tags/{tagId}", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isForbidden());
    }

    @Test
    void ADMIN_권한으로_삭제하면_204를_반환하고_목록에서_사라진다() throws Exception {
        String name = uniqueName();
        TagId id = tagIdGenerator.generate();
        Tag tag = TagFixture.builder().id(id).name(name).build();
        tagRepository.save(tag);
        String sessionId = issueSession("admin-1", "ADMIN");

        mockMvc.perform(delete("/tags/{tagId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tags").param("name", name)
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueName() {
        return "태그" + Math.floorMod(System.nanoTime(), 1_000_000);
    }
}

package com.dozycoffee.catalog.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.catalog.application.repository.OptionGroupRepository;
import com.dozycoffee.catalog.domain.OptionGroup;
import com.dozycoffee.catalog.domain.OptionGroupFixture;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.OptionItemFixture;
import com.dozycoffee.catalog.presentation.dto.OptionGroupCreateRequest;
import com.dozycoffee.catalog.presentation.dto.OptionGroupItemsUpdateRequest;
import com.dozycoffee.catalog.presentation.dto.OptionGroupProfileUpdateRequest;
import com.dozycoffee.catalog.presentation.dto.OptionItemRequest;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.support.MockMvcIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class OptionControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private OptionGroupRepository optionGroupRepository;

    @Autowired
    private IdentifierGenerator<OptionGroupId> optionGroupIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 인증없이_목록을_조회하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/options"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SYSTEM_권한으로_조회하면_등록된_옵션그룹이_목록에_포함된다() throws Exception {
        String name = uniqueName();
        OptionGroup optionGroup = OptionGroupFixture.builder()
                .id(optionGroupIdGenerator.generate())
                .name(name)
                .build();
        optionGroupRepository.save(optionGroup);

        mockMvc.perform(get("/options")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").exists());
    }

    @Test
    void BRANCH_권한으로_생성하면_403을_반환한다() throws Exception {
        OptionGroupCreateRequest request = new OptionGroupCreateRequest(
                uniqueName(), "설명", List.of(new OptionItemRequest("샷 추가", "설명", 500))
        );

        mockMvc.perform(post("/options")
                        .header("Authorization", "Bearer " + issueSession("branch-1", "BRANCH"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 아이템이_비어있으면_400을_반환한다() throws Exception {
        OptionGroupCreateRequest request = new OptionGroupCreateRequest(uniqueName(), "설명", List.of());

        mockMvc.perform(post("/options")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceCode").value("COMMON"));
    }

    @Test
    void SYSTEM_권한으로_생성하면_옵션그룹과_아이템이_생성된다() throws Exception {
        String name = uniqueName();
        OptionGroupCreateRequest request = new OptionGroupCreateRequest(
                name, "설명", List.of(new OptionItemRequest("샷 추가", "설명", 500))
        );

        mockMvc.perform(post("/options")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.items[0].name").value("샷 추가"));
    }

    @Test
    void SYSTEM_권한으로_프로필을_수정하면_204를_반환한다() throws Exception {
        OptionGroupId id = optionGroupIdGenerator.generate();
        OptionGroup optionGroup = OptionGroupFixture.builder().id(id).name(uniqueName()).build();
        optionGroupRepository.save(optionGroup);
        String newName = uniqueName();
        OptionGroupProfileUpdateRequest request = new OptionGroupProfileUpdateRequest(newName, "새로운 설명");

        mockMvc.perform(patch("/options/{optionGroupId}/profile", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/options")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(jsonPath("$[?(@.name == '" + newName + "')]").exists());
    }

    @Test
    void SYSTEM_권한으로_아이템을_교체하면_204를_반환한다() throws Exception {
        OptionGroupId id = optionGroupIdGenerator.generate();
        OptionGroup optionGroup = OptionGroupFixture.builder()
                .id(id)
                .name(uniqueName())
                .items(List.of(OptionItemFixture.builder().build()))
                .build();
        optionGroupRepository.save(optionGroup);
        OptionGroupItemsUpdateRequest request = new OptionGroupItemsUpdateRequest(
                List.of(new OptionItemRequest("교체된 아이템", "설명", 1000))
        );

        mockMvc.perform(patch("/options/{optionGroupId}/items", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void SYSTEM_권한으로_삭제하면_204를_반환하고_목록에서_사라진다() throws Exception {
        OptionGroupId id = optionGroupIdGenerator.generate();
        String name = uniqueName();
        OptionGroup optionGroup = OptionGroupFixture.builder().id(id).name(name).build();
        optionGroupRepository.save(optionGroup);
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(delete("/options/{optionGroupId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/options")
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").doesNotExist());
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueName() {
        return "옵션그룹" + Math.floorMod(System.nanoTime(), 1_000_000);
    }
}

package com.dozycoffee.auth.presentation;

import com.dozycoffee.admin.application.AdminRepository;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminFixture;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.auth.presentation.dto.AuthLoginRequest;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.support.MockMvcIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AuthControllerTest extends MockMvcIntegrationTest {

    private static final String RAW_PASSWORD = "password1!";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private IdentifierGenerator<AdminId> adminIdGenerator;

    @Autowired
    private PasswordHasher passwordHasher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 필드_검증_실패시_400을_반환한다() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(new AuthLoginRequest("", "", ""));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceCode").value("COMMON"));
    }

    @Test
    void 존재하지_않는_계정으로_로그인하면_401을_반환한다() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("ADMIN", "no_such_admin", RAW_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.serviceCode").value("AUTH"));
    }

    @Test
    void 활성화된_관리자_계정으로_로그인하면_세션을_발급한다() throws Exception {
        String username = registerActiveAdmin();
        AuthLoginRequest request = new AuthLoginRequest("ADMIN", username, RAW_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.sessionId").isNotEmpty());
    }

    @Test
    void 세션_없이_로그아웃하면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그아웃하면_세션이_무효화되어_이후_요청은_401을_반환한다() throws Exception {
        String username = registerActiveAdmin();
        AuthLoginRequest loginRequest = new AuthLoginRequest("ADMIN", username, RAW_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String sessionId = body.get("sessionId").asText();

        mockMvc.perform(post("/auth/logout").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admins/me").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isUnauthorized());
    }

    private String registerActiveAdmin() {
        AdminId id = adminIdGenerator.generate();
        String username = "mockmvc_" + id.getValue().replace("-", "").substring(0, 8);
        Admin admin = AdminFixture.builder()
                .id(id)
                .username(username)
                .password(passwordHasher.hash(RAW_PASSWORD))
                .build();
        admin.approve();
        adminRepository.save(admin);
        return username;
    }
}

package com.dozycoffee.support;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MockMvcIntegrationSmokeTest extends MockMvcIntegrationTest {

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 인증_필터와_인가_인터셉터를_거치지_않으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/admins"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 요구되는_role과_불일치하면_403을_반환한다() throws Exception {
        String sessionId = issueSession("branch-1", "BRANCH");

        mockMvc.perform(get("/admins").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isForbidden());
    }

    @Test
    void 요구되는_role과_일치하면_실제_컨트롤러까지_도달한다() throws Exception {
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(get("/admins").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk());
    }

    @Test
    void 검증_실패시_GlobalExceptionHandler_포맷으로_400을_반환한다() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(new LoginRequest("", "", ""));

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("serviceCode").asText()).isEqualTo("COMMON");
        assertThat(body.get("fieldErrors")).isNotEmpty();
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private record LoginRequest(String role, String id, String credential) {
    }
}

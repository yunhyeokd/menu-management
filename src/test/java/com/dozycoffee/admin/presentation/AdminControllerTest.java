package com.dozycoffee.admin.presentation;

import com.dozycoffee.admin.application.AdminRepository;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminFixture;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.presentation.dto.AdminRegisterRequest;
import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.support.MockMvcIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AdminControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private IdentifierGenerator<AdminId> adminIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void SYSTEM_권한으로_조회하면_등록된_관리자가_목록에_포함된다() throws Exception {
        AdminId id = adminIdGenerator.generate();
        String username = "mockmvc_" + uniqueSuffix(id);
        Admin admin = AdminFixture.builder()
                .id(id)
                .username(username)
                .build();
        adminRepository.save(admin);

        mockMvc.perform(get("/admins")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == '" + username + "')]").exists());
    }

    @Test
    void 존재하지_않는_관리자를_조회하면_404와_ADM_서비스코드를_반환한다() throws Exception {
        AdminId unknownId = adminIdGenerator.generate();

        mockMvc.perform(get("/admins/{adminId}", unknownId.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("ADM"));
    }

    @Test
    void ADMIN_권한으로_등록하면_PENDING_상태의_관리자가_생성된다() throws Exception {
        String suffix = uniqueSuffix(adminIdGenerator.generate());
        AdminRegisterRequest request = new AdminRegisterRequest(
                "mockmvc_" + suffix, "password1!", "EMP" + suffix.toUpperCase(),
                "테스트", "+821000000000", "mockmvc_" + suffix + "@dozy.com"
        );

        mockMvc.perform(post("/admins")
                        .header("Authorization", "Bearer " + issueSession("admin-1", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mockmvc_" + suffix))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void 이미_존재하는_username으로_등록하면_409를_반환한다() throws Exception {
        AdminId id = adminIdGenerator.generate();
        String username = "mockmvc_" + uniqueSuffix(id);
        Admin admin = AdminFixture.builder()
                .id(id)
                .username(username)
                .build();
        adminRepository.save(admin);

        String suffix = uniqueSuffix(adminIdGenerator.generate());
        AdminRegisterRequest request = new AdminRegisterRequest(
                username, "password1!", "EMP" + suffix.toUpperCase(),
                "테스트2", "+821000000001", "mockmvc_" + suffix + "@dozy.com"
        );

        mockMvc.perform(post("/admins")
                        .header("Authorization", "Bearer " + issueSession("admin-1", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.serviceCode").value("ADM"));
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueSuffix(AdminId id) {
        return id.getValue().replace("-", "").substring(0, 8);
    }
}

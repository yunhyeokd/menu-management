package com.dozycoffee.admin.presentation;

import com.dozycoffee.admin.application.AdminRepository;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminFixture;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.presentation.dto.AdminPasswordChangeRequest;
import com.dozycoffee.admin.presentation.dto.AdminProfileUpdateRequest;
import com.dozycoffee.admin.presentation.dto.AdminRegisterRequest;
import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.security.PasswordHasher;
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
class AdminControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private IdentifierGenerator<AdminId> adminIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    @Autowired
    private PasswordHasher passwordHasher;

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

    @Test
    void SYSTEM_권한으로_단건_조회하면_관리자_정보가_반환된다() throws Exception {
        AdminId id = adminIdGenerator.generate();
        String username = "mockmvc_" + uniqueSuffix(id);
        adminRepository.save(AdminFixture.builder().id(id).username(username).build());

        mockMvc.perform(get("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void 본인_세션으로_me를_조회하면_본인_정보가_반환된다() throws Exception {
        AdminId id = adminIdGenerator.generate();
        String username = "mockmvc_" + uniqueSuffix(id);
        adminRepository.save(AdminFixture.builder().id(id).username(username).build());

        mockMvc.perform(get("/admins/me")
                        .header("Authorization", "Bearer " + issueSession(id.getValue(), "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminId").value(id.getValue()));
    }

    @Test
    void SYSTEM_권한으로_승인하면_ACTIVE_상태로_바뀐다() throws Exception {
        AdminId id = seedAdmin();
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(patch("/admins/{adminId}/approve", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void SYSTEM_권한으로_거절하면_INACTIVE_상태로_바뀐다() throws Exception {
        AdminId id = seedAdmin();
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(patch("/admins/{adminId}/reject", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void PENDING이_아닌_관리자를_승인하면_409를_반환한다() throws Exception {
        AdminId id = seedAdmin();
        String sessionId = issueSession("system-1", "SYSTEM");
        mockMvc.perform(patch("/admins/{adminId}/approve", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/admins/{adminId}/approve", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.serviceCode").value("ADM"));
    }

    @Test
    void SYSTEM_권한으로_다른_관리자의_프로필을_수정할_수_있다() throws Exception {
        AdminId id = seedAdmin();
        AdminProfileUpdateRequest request = new AdminProfileUpdateRequest("변경이름", "+821099998888", "changed@dozy.com");

        mockMvc.perform(patch("/admins/{adminId}/profile", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("변경이름"));
    }

    @Test
    void ADMIN_권한으로_다른_관리자의_프로필을_수정하면_403을_반환한다() throws Exception {
        AdminId otherId = seedAdmin();
        AdminProfileUpdateRequest request = new AdminProfileUpdateRequest("변경이름", "+821099998888", "changed@dozy.com");

        mockMvc.perform(patch("/admins/{adminId}/profile", otherId.getValue())
                        .header("Authorization", "Bearer " + issueSession("admin-1", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void ADMIN_권한으로_본인의_프로필은_수정할_수_있다() throws Exception {
        AdminId id = seedAdmin();
        AdminProfileUpdateRequest request = new AdminProfileUpdateRequest("변경이름", "+821099998888", "changed@dozy.com");

        mockMvc.perform(patch("/admins/{adminId}/profile", id.getValue())
                        .header("Authorization", "Bearer " + issueSession(id.getValue(), "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("변경이름"));
    }

    @Test
    void 현재_비밀번호가_틀리면_비밀번호_변경이_401을_반환한다() throws Exception {
        AdminId id = adminIdGenerator.generate();
        adminRepository.save(AdminFixture.builder()
                .id(id)
                .username("mockmvc_" + uniqueSuffix(id))
                .password(passwordHasher.hash("correct-password1!"))
                .build());
        AdminPasswordChangeRequest request = new AdminPasswordChangeRequest("wrong-password1!", "new-password1!");

        mockMvc.perform(patch("/admins/{adminId}/password", id.getValue())
                        .header("Authorization", "Bearer " + issueSession(id.getValue(), "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 현재_비밀번호가_맞으면_비밀번호_변경에_성공한다() throws Exception {
        AdminId id = adminIdGenerator.generate();
        adminRepository.save(AdminFixture.builder()
                .id(id)
                .username("mockmvc_" + uniqueSuffix(id))
                .password(passwordHasher.hash("correct-password1!"))
                .build());
        AdminPasswordChangeRequest request = new AdminPasswordChangeRequest("correct-password1!", "new-password1!");

        mockMvc.perform(patch("/admins/{adminId}/password", id.getValue())
                        .header("Authorization", "Bearer " + issueSession(id.getValue(), "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void SYSTEM_권한으로_soft_delete하면_상태가_INACTIVE로_바뀐다() throws Exception {
        AdminId id = seedAdmin();
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(delete("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void soft_delete되지_않은_관리자를_hard_delete하면_409를_반환한다() throws Exception {
        AdminId id = seedAdmin();

        mockMvc.perform(delete("/admins/{adminId}/hard", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.serviceCode").value("ADM"));
    }

    @Test
    void soft_delete된_관리자를_hard_delete하면_204를_반환하고_완전히_삭제된다() throws Exception {
        AdminId id = seedAdmin();
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(delete("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/admins/{adminId}/hard", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admins/{adminId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNotFound());
    }

    private AdminId seedAdmin() {
        AdminId id = adminIdGenerator.generate();
        adminRepository.save(AdminFixture.builder().id(id).username("mockmvc_" + uniqueSuffix(id)).build());
        return id;
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueSuffix(AdminId id) {
        return id.getValue().replace("-", "").substring(0, 8);
    }
}

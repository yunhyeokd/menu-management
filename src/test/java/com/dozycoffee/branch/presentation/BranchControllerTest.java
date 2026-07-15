package com.dozycoffee.branch.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.branch.application.BranchRepository;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchFixture;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.presentation.dto.BranchCreateRequest;
import com.dozycoffee.branch.presentation.dto.BranchProfileUpdateRequest;
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
class BranchControllerTest extends MockMvcIntegrationTest {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private IdentifierGenerator<BranchId> branchIdGenerator;

    @Autowired
    private AuthSessionManager authSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void SYSTEM_권한으로_생성하면_지점과_authKey가_발급된다() throws Exception {
        String name = "테스트지점" + uniqueSuffix();
        BranchCreateRequest request = new BranchCreateRequest(name, "서울시 강남구 테헤란로 1");

        mockMvc.perform(post("/branches")
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.authKey").isNotEmpty());
    }

    @Test
    void BRANCH_권한으로_생성하면_403을_반환한다() throws Exception {
        BranchCreateRequest request = new BranchCreateRequest("아무지점" + uniqueSuffix(), "주소");

        mockMvc.perform(post("/branches")
                        .header("Authorization", "Bearer " + issueSession("branch-1", "BRANCH"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 인증없이_목록을_조회해도_등록된_지점이_포함된다() throws Exception {
        String name = "테스트지점" + uniqueSuffix();
        Branch branch = BranchFixture.builder()
                .id(branchIdGenerator.generate())
                .code(uniqueBranchCode())
                .name(name)
                .build();
        branchRepository.save(branch);

        mockMvc.perform(get("/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").exists());
    }

    @Test
    void 인증없이_존재하지_않는_지점을_조회하면_404를_반환한다() throws Exception {
        BranchId unknownId = branchIdGenerator.generate();

        mockMvc.perform(get("/branches/{branchId}", unknownId.getValue()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.serviceCode").value("BRN"));
    }

    @Test
    void SYSTEM_권한으로_soft_delete하면_상태가_INACTIVE로_바뀐다() throws Exception {
        BranchId id = branchIdGenerator.generate();
        Branch branch = BranchFixture.builder()
                .id(id)
                .code(uniqueBranchCode())
                .name("테스트지점" + uniqueSuffix())
                .build();
        branchRepository.save(branch);

        mockMvc.perform(delete("/branches/{branchId}", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/branches/{branchId}", id.getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void soft_delete되지_않은_지점을_hard_delete하면_409를_반환한다() throws Exception {
        BranchId id = branchIdGenerator.generate();
        Branch branch = BranchFixture.builder()
                .id(id)
                .code(uniqueBranchCode())
                .name("테스트지점" + uniqueSuffix())
                .build();
        branchRepository.save(branch);

        mockMvc.perform(delete("/branches/{branchId}/hard", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isConflict());
    }

    @Test
    void soft_delete된_지점을_hard_delete하면_204를_반환하고_완전히_삭제된다() throws Exception {
        BranchId id = branchIdGenerator.generate();
        Branch branch = BranchFixture.builder()
                .id(id)
                .code(uniqueBranchCode())
                .name("테스트지점" + uniqueSuffix())
                .build();
        branchRepository.save(branch);
        String sessionId = issueSession("system-1", "SYSTEM");

        mockMvc.perform(delete("/branches/{branchId}", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/branches/{branchId}/hard", id.getValue())
                        .header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/branches/{branchId}", id.getValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void SYSTEM_권한으로_프로필을_수정하면_이름과_주소가_바뀐다() throws Exception {
        BranchId id = branchIdGenerator.generate();
        Branch branch = BranchFixture.builder()
                .id(id)
                .code(uniqueBranchCode())
                .name("테스트지점" + uniqueSuffix())
                .build();
        branchRepository.save(branch);
        String newName = "변경지점" + uniqueSuffix();
        BranchProfileUpdateRequest request = new BranchProfileUpdateRequest(newName, "서울시 강남구 테헤란로 2");

        mockMvc.perform(patch("/branches/{branchId}/profile", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/branches/{branchId}", id.getValue()))
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    void SYSTEM_권한으로_authKey를_재발급하면_새로운_키가_반환된다() throws Exception {
        BranchId id = branchIdGenerator.generate();
        Branch branch = BranchFixture.builder()
                .id(id)
                .code(uniqueBranchCode())
                .name("테스트지점" + uniqueSuffix())
                .build();
        branchRepository.save(branch);

        mockMvc.perform(patch("/branches/{branchId}/reset-authkey", id.getValue())
                        .header("Authorization", "Bearer " + issueSession("system-1", "SYSTEM")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authKey").isNotEmpty());
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    private String uniqueSuffix() {
        return String.valueOf(Math.floorMod(System.nanoTime(), 100000));
    }

    private String uniqueBranchCode() {
        return "29" + String.format("%06d", Math.floorMod(System.nanoTime(), 1_000_000));
    }
}

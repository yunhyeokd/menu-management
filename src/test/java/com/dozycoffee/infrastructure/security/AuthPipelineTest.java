package com.dozycoffee.infrastructure.security;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.application.AuthorizationService;
import com.dozycoffee.auth.domain.SessionPrincipal;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.core.session.SessionId;
import com.dozycoffee.infrastructure.persistance.repository_impl.InMemoryAuthSessionRepository;
import com.dozycoffee.infrastructure.web.GlobalExceptionHandler;
import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import com.dozycoffee.infrastructure.web.interceptor.RoleAuthorizationInterceptor;
import com.dozycoffee.infrastructure.web.resolver.AuthPrincipal;
import com.dozycoffee.infrastructure.web.resolver.PrincipalArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthPipelineTest {

    private AuthSessionManager authSessionManager;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InMemoryAuthSessionRepository repository = new InMemoryAuthSessionRepository();
        AtomicInteger counter = new AtomicInteger();
        authSessionManager = new AuthSessionManager(repository, () -> SessionId.of("session-" + counter.incrementAndGet()));

        SessionPrincipalFilter filter = new SessionPrincipalFilter(authSessionManager);
        PrincipalArgumentResolver principalArgumentResolver = new PrincipalArgumentResolver();
        RoleAuthorizationInterceptor roleAuthorizationInterceptor = new RoleAuthorizationInterceptor(new AuthorizationService());

        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilter(filter)
                .setCustomArgumentResolvers(principalArgumentResolver)
                .addInterceptors(roleAuthorizationInterceptor)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private String issueSession(String subject, String role) {
        Session<Principal> session = authSessionManager.create(SessionPrincipal.of(subject, role), 3600L);
        return session.getSessionId().getValue();
    }

    @Test
    void 세션이_없어도_공개_엔드포인트는_통과한다() throws Exception {
        mockMvc.perform(get("/open")).andExpect(status().isOk());
    }

    @Test
    void AuthPrincipal_엔드포인트는_세션_없으면_401() throws Exception {
        mockMvc.perform(get("/auth-only")).andExpect(status().isUnauthorized());
    }

    @Test
    void AuthPrincipal_엔드포인트는_유효한_세션이면_200() throws Exception {
        String sessionId = issueSession("admin-1", "ADMIN");
        mockMvc.perform(get("/auth-only").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk());
    }

    @Test
    void RequireRole_엔드포인트는_세션_없으면_401() throws Exception {
        mockMvc.perform(get("/admin-only")).andExpect(status().isUnauthorized());
    }

    @Test
    void RequireRole_엔드포인트는_role_불일치시_403() throws Exception {
        String sessionId = issueSession("branch-1", "BRANCH");
        mockMvc.perform(get("/admin-only").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isForbidden());
    }

    @Test
    void RequireRole_엔드포인트는_role_일치시_200() throws Exception {
        String sessionId = issueSession("admin-1", "ADMIN");
        mockMvc.perform(get("/admin-only").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk());
    }

    @RestController
    static class TestController {

        @GetMapping("/open")
        public void open() {
        }

        @GetMapping("/auth-only")
        public void authOnly(@AuthPrincipal Principal principal) {
        }

        @GetMapping("/admin-only")
        @RequireRole({"ADMIN"})
        public void adminOnly() {
        }
    }
}

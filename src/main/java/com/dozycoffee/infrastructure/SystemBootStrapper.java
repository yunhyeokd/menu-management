package com.dozycoffee.infrastructure;

import com.dozycoffee.admin.application.AdminService;
import com.dozycoffee.admin.application.dto.SystemAdminRegisterCommand;
import com.dozycoffee.core.exception.service.ConflictException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemBootStrapper {

    private static final Logger log = LoggerFactory.getLogger(SystemBootStrapper.class);

    private final AdminService adminService;
    private final String username;
    private final String password;

    public SystemBootStrapper(
            AdminService adminService,
            @Value("${system.admin.username}") String username,
            @Value("${system.admin.password}") String password
    ) {
        this.adminService = adminService;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void bootstrap() {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            log.info("SYSTEM_ADMIN_USERNAME/PASSWORD 미설정, 시스템 관리자 부트스트랩을 스킵합니다");
            return;
        }

        try {
            adminService.registerSystem(new SystemAdminRegisterCommand(username, password));
            log.info("시스템 관리자 계정을 생성했습니다");
        } catch (ConflictException e) {
            log.info("시스템 관리자 계정이 이미 존재해 부트스트랩을 스킵합니다");
        } catch (Exception e) {
            log.error("시스템 관리자 부트스트랩에 실패했습니다", e);
        }
    }
}

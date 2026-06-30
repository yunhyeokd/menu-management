package com.dozycoffee.infrastructure;

import com.dozycoffee.admin.application.AdminUsernameAuthenticator;
import com.dozycoffee.auth.application.AuthErrors;
import com.dozycoffee.auth.application.AuthServiceCode;
import com.dozycoffee.auth.application.AuthenticationResolver;
import com.dozycoffee.auth.application.AuthenticationResult;
import com.dozycoffee.auth.application.AuthenticationService;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.branch.application.BranchAuthenticator;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.core.application.exception.AuthenticationException;
import com.dozycoffee.core.domain.Identifier;
import com.dozycoffee.infrastructure.persistance.DatabaseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Objects;

@Configuration
@Import(DatabaseConfig.class)
@ComponentScan(
        basePackages = "com.dozycoffee",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)
)
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer yamlConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yaml"));
        configurer.setProperties(Objects.requireNonNull(yaml.getObject()));
        return configurer;
    }

    @Value("${application.name}")
    private String name;

    @Bean
    public AuthenticationService authenticationService(
            AdminUsernameAuthenticator adminAuthenticator,
            BranchAuthenticator branchAuthenticator
    ) {
        AuthenticationResolver adminResolver = (id, credential) -> {
            Principal principal = adminAuthenticator.authenticate((Identifier<String>) () -> id, credential)
                    .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED));
            return new AuthenticationResult(principal, 3600L);
        };
        AuthenticationResolver branchResolver = (id, credential) -> {
            Principal principal = branchAuthenticator.authenticate(BranchCode.of(id), credential)
                    .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED));
            return new AuthenticationResult(principal, 86400L);
        };

        return new AuthenticationService(Map.of(
                "ADMIN", adminResolver,
                "BRANCH", branchResolver
        ));
    }
}

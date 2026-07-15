package com.dozycoffee.infrastructure.web;

import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.auth.application.AuthorizationService;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.session.SessionId;
import com.dozycoffee.infrastructure.web.interceptor.RoleAuthorizationInterceptor;
import com.dozycoffee.infrastructure.web.resolver.PrincipalArgumentResolver;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.TagId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ServletConfig {

    // 헤더(Authorization 세션 토큰)/페이로드(로그인 비밀번호 등)는 로그에 남기지 않는다.
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(false);
        filter.setIncludePayload(false);
        return filter;
    }

    @Bean
    public PrincipalArgumentResolver principalArgumentResolver() {
        return new PrincipalArgumentResolver();
    }

    @Bean
    public RoleAuthorizationInterceptor roleAuthorizationInterceptor(AuthorizationService authorizationService) {
        return new RoleAuthorizationInterceptor(authorizationService);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(
            PrincipalArgumentResolver principalArgumentResolver,
            RoleAuthorizationInterceptor roleAuthorizationInterceptor
    ) {
        return new WebMvcConfigurer() {
            @Override
            public void addFormatters(FormatterRegistry registry) {
                registry.addConverter(new IdConverterFactory<AdminId>(AdminId::of) {});
                registry.addConverter(new IdConverterFactory<BranchId>(BranchId::of) {});
                registry.addConverter(new IdConverterFactory<CategoryId>(CategoryId::of) {});
                registry.addConverter(new IdConverterFactory<OptionGroupId>(OptionGroupId::of) {});
                registry.addConverter(new IdConverterFactory<ProductId>(ProductId::of) {});
                registry.addConverter(new IdConverterFactory<TagId>(TagId::of) {});
                registry.addConverter(new IdConverterFactory<SessionId>(SessionId::of) {});
            }

            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(principalArgumentResolver);
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(roleAuthorizationInterceptor);
            }
        };
    }

}

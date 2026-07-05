package com.dozycoffee.infrastructure.web;

import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.session.SessionId;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.TagId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = "com.dozycoffee",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = ControllerAdvice.class)
        }
)
public class ServletConfig {

    @Bean
    public WebMvcConfigurer idConverterConfigurer() {
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
        };
    }

}

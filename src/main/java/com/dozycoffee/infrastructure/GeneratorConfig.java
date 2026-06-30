package com.dozycoffee.infrastructure;

import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.domain.SessionId;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.TagId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneratorConfig {

    @Bean
    public IdentifierGenerator<AdminId> adminIdGenerator() {
        return new UuidV7Generator<>(AdminId::of);
    }

    @Bean
    public IdentifierGenerator<BranchId> branchIdGenerator() {
        return new UuidV7Generator<>(BranchId::of);
    }

    @Bean
    public IdentifierGenerator<ProductId> productIdGenerator() {
        return new UuidV7Generator<>(ProductId::of);
    }

    @Bean
    public IdentifierGenerator<CategoryId> categoryIdGenerator() {
        return new UuidV7Generator<>(CategoryId::of);
    }

    @Bean
    public IdentifierGenerator<TagId> tagIdGenerator() {
        return new UuidV7Generator<>(TagId::of);
    }

    @Bean
    public IdentifierGenerator<OptionGroupId> optionGroupIdGenerator() {
        return new UuidV7Generator<>(OptionGroupId::of);
    }

    @Bean
    public IdentifierGenerator<SessionId> sessionIdGenerator() {
        return new UuidV7Generator<>(SessionId::of);
    }
}

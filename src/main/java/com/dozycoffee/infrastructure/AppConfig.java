package com.dozycoffee.infrastructure;

import com.dozycoffee.infrastructure.persistance.DatabaseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

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

}

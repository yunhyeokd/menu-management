package com.dozycoffee.infrastructure.persistance;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

// DataSource(Hikari)는 spring.datasource.*로, 스키마 마이그레이션은 spring-boot-starter-flyway로
// Boot가 자동 구성한다. MyBatis는 아직 Boot 4.x 대응 스타터가 없어 SqlSessionFactory만 수동 배선한다.
@Configuration
@MapperScan("com.dozycoffee.infrastructure.persistance.mapper")
public class DatabaseConfig {

    @Value("${mybatis.mapper-locations}") private String mapperLocations;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
        return sqlSessionFactoryBean;
    }

}

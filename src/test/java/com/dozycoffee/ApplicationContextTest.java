package com.dozycoffee;

import com.dozycoffee.infrastructure.RootConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(RootConfig.class)
@WebAppConfiguration
class ApplicationContextTest {

    @Autowired
    DataSource dataSource;

    @Test
    void 컨텍스트가_로드되고_DB에_연결된다() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn.isValid(1)).isTrue();
        }
    }
}

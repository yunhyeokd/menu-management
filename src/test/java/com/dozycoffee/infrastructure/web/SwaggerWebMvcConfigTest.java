package com.dozycoffee.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SwaggerWebMvcConfigTest {

    @Configuration
    @EnableWebMvc
    @Import(SwaggerWebMvcConfig.class)
    static class TestConfig {
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    private void setUpContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(TestConfig.class);
        context.refresh();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void api_docs가_커스텀_제목과_bearer_보안스킴을_포함해서_생성된다() throws Exception {
        setUpContext();

        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("info").get("title").asText()).isEqualTo("Dozy Coffee Menu System API");
        assertThat(body.get("components").get("securitySchemes").get("bearerAuth").get("scheme").asText())
                .isEqualTo("bearer");
    }

    @Test
    void swagger_ui_정적_리소스가_서빙된다() throws Exception {
        setUpContext();

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}

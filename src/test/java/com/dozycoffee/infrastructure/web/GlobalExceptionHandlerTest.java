package com.dozycoffee.infrastructure.web;

import com.dozycoffee.admin.application.AdminErrors;
import com.dozycoffee.admin.application.AdminServiceCode;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void 필드_검증_실패시_400과_COMMON_에러코드를_반환한다() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(new TestRequest(""));

        MvcResult result = mockMvc.perform(post("/test-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("serviceCode").asText()).isEqualTo("COMMON");
        assertThat(body.get("errorCode").asInt()).isEqualTo(1);
        assertThat(body.get("fieldErrors").get(0).get("field").asText()).isEqualTo("name");
    }

    @Test
    void 도메인_서비스_예외는_해당_serviceCode와_상태코드로_변환된다() throws Exception {
        MvcResult result = mockMvc.perform(post("/test-not-found"))
                .andExpect(status().isNotFound())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("serviceCode").asText()).isEqualTo("ADM");
        assertThat(body.get("errorCode").asInt()).isEqualTo(3);
        assertThat(body.get("message").asText()).isEqualTo("Admin Not Found");
    }

    @Test
    void 예상하지_못한_예외는_500과_공용_메세지로_감싸진다() throws Exception {
        MvcResult result = mockMvc.perform(post("/test-unexpected"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("serviceCode").asText()).isEqualTo("COMMON");
        assertThat(body.get("errorCode").asInt()).isEqualTo(2);
        assertThat(body.get("message").asText()).isEqualTo("Internal Server Error");
    }

    @RestController
    @Validated
    static class TestController {

        @PostMapping("/test-validation")
        public void validation(@RequestBody @jakarta.validation.Valid TestRequest request) {
        }

        @PostMapping("/test-not-found")
        public void notFound() {
            throw new ResourceNotFoundException(AdminServiceCode.ADM, AdminErrors.ADMIN_NOT_FOUND);
        }

        @PostMapping("/test-unexpected")
        public void unexpected() {
            throw new IllegalStateException("boom");
        }
    }

    record TestRequest(@NotBlank String name) {
    }
}

package yjh.ontongsal.springbootexample.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import yjh.ontongsal.common.domain.exception.CommonResultCode;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebControllerTest {

    @LocalServerPort
    private int serverPort;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        restTestClient = RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + serverPort)
                .build();
    }

    @DisplayName("Web API 응답 포맷 검증")
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("responseTestCases")
    void responseFormatTest(ApiResponseTestCase testCase) {
        restTestClient
                .get()
                .uri(testCase.uri())
                .exchange()
                .expectStatus().isEqualTo(testCase.expectedStatus())
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.result_code").isEqualTo(testCase.expectedResultCode())
                .jsonPath("$.result_message").isEqualTo(testCase.expectedResultMessage())
                .jsonPath("$.data").doesNotExist()
                .jsonPath("$.trace_id").exists();
    }

    record ApiResponseTestCase(
            String uri,
            int expectedStatus,
            int expectedResultCode,
            String expectedResultMessage
    ) {
    }

    static Stream<ApiResponseTestCase> responseTestCases() {
        return Stream.of(
                new ApiResponseTestCase(
                        "/web/success",
                        200,
                        1,
                        "SUCCESS"
                ),
                new ApiResponseTestCase(
                        "/web/business-exception",
                        200,
                        CommonResultCode.INTERNAL_SERVER_ERROR.getCode(),
                        CommonResultCode.INTERNAL_SERVER_ERROR.getMessage()
                ),
                new ApiResponseTestCase(
                        "/web/authentication-exception",
                        401,
                        CommonResultCode.UNAUTHORIZED.getCode(),
                        CommonResultCode.UNAUTHORIZED.getMessage()
                )
        );
    }
}

package yjh.ontongsal.springbootexample.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import yjh.ontongsal.common.domain.exception.CommonResultCode;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@ActiveProfiles("local")
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

    @DisplayName("응답 바디 포맷 검증")
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

    @DisplayName("요청/응답 정보에 @Masked 필드는 로깅시 마스킹 처리된다.")
    @Test
    void securityMaskedTest() {
        String requestBody = """
                {
                    "email": "hongildone@gmail.com",
                    "password": "password123",
                    "name": "홍길동"
                }
                """;

        restTestClient
                .post()
                .uri("/web/security-masked")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.result_code").isEqualTo(1)
                .jsonPath("$.result_message").isEqualTo("SUCCESS")
                .jsonPath("$.data").exists()
                .jsonPath("$.data.phoneNumber").isEqualTo("111222333")
                .jsonPath("$.data.email").isEqualTo("hongildone@gmail.com")
                .jsonPath("$.data.name").isEqualTo("name")
                .jsonPath("$.data.createdAt").exists()
                .jsonPath("$.trace_id").exists();
    }

    @DisplayName("Non-text 요청 body 로깅 검증")
    @Test
    void nonTextTest() {
        byte[] requestBody = "hello world".getBytes(StandardCharsets.UTF_8);

        restTestClient
                .post()
                .uri("/web/non-text")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.result_code").isEqualTo(1)
                .jsonPath("$.result_message").isEqualTo("SUCCESS")
                .jsonPath("$.data").isEqualTo("SUCCESS")
                .jsonPath("$.trace_id").exists();
    }

    @DisplayName("1000자를 초과하는 요청 body 로깅 검증")
    @Test
    void largeBodyTest() {
        String largeName = "a".repeat(1500);

        String requestBody = """
                {
                    "email": "hongildone@gmail.com",
                    "password": "password123",
                    "name": "%s"
                }
                """.formatted(largeName);

        restTestClient
                .post()
                .uri("/web/large-body")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.result_code").isEqualTo(1)
                .jsonPath("$.result_message").isEqualTo("SUCCESS")
                .jsonPath("$.data").isEqualTo("SUCCESS")
                .jsonPath("$.trace_id").exists();
    }
}

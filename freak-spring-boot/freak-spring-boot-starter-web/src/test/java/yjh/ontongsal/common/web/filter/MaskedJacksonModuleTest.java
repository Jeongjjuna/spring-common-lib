package yjh.ontongsal.common.web.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaskedJacksonModuleTest {

    private final JsonMapper sut = JsonMapper.builder()
            .addModule(new MaskedJacksonModule())
            .build();

    record MaskedResponse(@Masked int phoneNumber, @Masked String email, String name) {
    }

    record NormalResponse(String email, String name) {
    }

    record MaskedEmailResponse(@Masked String email) {
    }

    record NormalEmailResponse(String email) {
    }

    record NestedResponse(String name, MaskedDetail detail) {
    }

    record MaskedDetail(@Masked String email, String name) {
    }

    record ListResponse(String name, List<MaskedDetail> details) {
    }

    @DisplayName("@Masked 필드는 마스킹된다")
    @Test
    void maskedFieldIsMasked() {
        var response = new MaskedResponse(
                111222333,
                "hongildone@gmail.com",
                "hongildone"
        );

        String json = sut.writeValueAsString(response);
        System.out.println(json);

        assertThat(json)
                .contains("\"phoneNumber\":\"***\"")
                .contains("\"email\":\"***\"")
                .contains("\"name\":\"hongildone\"");
    }

    @DisplayName("@Masked가 없는 필드는 마스킹되지 않는다")
    @Test
    void fieldWithoutMaskedAnnotationIsNotMasked() {
        var response = new NormalResponse(
                "hongildone@gmail.com",
                "hongildone"
        );

        String json = sut.writeValueAsString(response);
        System.out.println(json);

        assertThat(json)
                .contains("\"email\":\"hongildone@gmail.com\"")
                .contains("\"name\":\"hongildone\"")
                .doesNotContain("***");
    }

    @DisplayName("동일한 필드명이라도 @Masked가 붙은 필드만 마스킹된다")
    @Test
    void sameFieldNameIsMaskedOnlyWhenMaskedAnnotationExists() {
        var maskedResponse = new MaskedEmailResponse(
                "hongildone@gmail.com"
        );

        var normalResponse = new NormalEmailResponse(
                "hongildone@gmail.com"
        );

        String maskedJson = sut.writeValueAsString(maskedResponse);
        System.out.println(maskedJson);
        String normalJson = sut.writeValueAsString(normalResponse);
        System.out.println(normalJson);

        assertThat(maskedJson)
                .contains("\"email\":\"***\"");

        assertThat(normalJson)
                .contains("\"email\":\"hongildone@gmail.com\"")
                .doesNotContain("***");
    }

    @Test
    @DisplayName("하위 객체의 @Masked 필드도 마스킹된다")
    void nestedObjectFieldIsMasked() {
        // given
        var response = new NestedResponse("hongildone", new MaskedDetail("hongildone@gmail.com", "홍길동"));
        // when
        String json = sut.writeValueAsString(response);
        System.out.println(json);
        // then
        assertThat(json)
                .contains("\"name\":\"hongildone\"")
                .contains("\"email\":\"***\"")
                .contains("\"name\":\"홍길동\"");
    }

    @Test
    @DisplayName("객체 리스트의 @Masked 필드도 각각 마스킹된다")
    void listObjectFieldsAreMasked() {
        // given
        var response = new ListResponse(
                "users",
                List.of(
                        new MaskedDetail("hongildone@gmail.com", "홍길동"),
                        new MaskedDetail("kim@gmail.com", "김철수")
                )
        );
        // when
        String json = sut.writeValueAsString(response);
        System.out.println(json);

        // then
        assertThat(json)
                .contains("\"email\":\"***\"")
                .contains("\"name\":\"홍길동\"")
                .contains("\"name\":\"김철수\"")
                .doesNotContain("hongildone@gmail.com")
                .doesNotContain("kim@gmail.com");
    }
}
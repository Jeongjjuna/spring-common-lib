package yjh.ontongsal.springbootexample.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import yjh.ontongsal.common.domain.exception.AuthenticationException;
import yjh.ontongsal.common.domain.exception.BusinessException;
import yjh.ontongsal.common.domain.exception.CommonResultCode;
import yjh.ontongsal.common.web.api.ApiController;
import yjh.ontongsal.common.web.api.ApiResponse;
import yjh.ontongsal.common.web.filter.Masked;

import java.time.LocalDateTime;

@Tag(name = "[응답 포맷 예외처리 API]", description = "응답 포맷과 예외처리 테스트를 위한 API")
@RequestMapping("/web")
@RestController
public class WebController implements ApiController {

    @GetMapping("/success")
    public ApiResponse<Void> test0() {
        return success();
    }

    @GetMapping("/business-exception")
    public ApiResponse<Void> test2() {
        throw new BusinessException(CommonResultCode.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/authentication-exception")
    public ApiResponse<Void> test3() {
        throw new AuthenticationException();
    }

    @PostMapping("/security-masked")
    public ApiResponse<MaskResponse> test4(
            @RequestBody MaskRequest request
    ) {
        System.out.println("[WebController] request : " + request);
        return success(new MaskResponse(111222333, "hongildone@gmail.com", "name", LocalDateTime.now()));
    }

    public record MaskRequest(
            @Masked String email,
            @Masked String password,
            String name
    ) {
    }

    public record MaskResponse(
            @Masked int phoneNumber,
            @Masked String email,
            String name,
            LocalDateTime createdAt
    ) {
    }

    @PostMapping(
            value = "/non-text",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ApiResponse<String> nonText(
            @RequestBody byte[] request
    ) {
        System.out.println("[WebController] request : " + new String(request));
        return success("SUCCESS");
    }

    @PostMapping("/large-body")
    public ApiResponse<String> largeBody(
            @RequestBody MaskRequest request
    ) {
        System.out.println("[WebController] request : " + request);
        return success("SUCCESS");
    }
}

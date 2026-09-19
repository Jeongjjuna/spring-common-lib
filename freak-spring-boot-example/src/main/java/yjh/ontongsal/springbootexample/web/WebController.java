package yjh.ontongsal.springbootexample.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yjh.ontongsal.common.domain.exception.AuthenticationException;
import yjh.ontongsal.common.domain.exception.BusinessException;
import yjh.ontongsal.common.domain.exception.CommonResultCode;
import yjh.ontongsal.common.web.api.ApiController;
import yjh.ontongsal.common.web.api.ApiResponse;

@RestController
@RequestMapping("/web")
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
}

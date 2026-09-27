package yjh.ontongsal.springbootexample.httpclient;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import yjh.ontongsal.common.http.HttpClient;


@HttpClient("auth-api")
public interface AuthClient {

    @GetExchange("/http-client-test/users")
    UserResponse getUser(
            @RequestHeader("Authorization") String authorization
    );

    record UserResponse(
            Long userId,
            String username,
            String status
    ) {
    }
}
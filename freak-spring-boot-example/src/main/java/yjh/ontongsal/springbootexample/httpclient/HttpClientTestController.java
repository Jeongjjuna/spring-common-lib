package yjh.ontongsal.springbootexample.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import yjh.ontongsal.common.web.api.ApiController;

import java.util.Map;

@RestController
@RequestMapping("/http-client-test")
public class HttpClientTestController implements ApiController {

    private final Logger log = LoggerFactory.getLogger(HttpClientTestController.class);
    private final AuthClient authClient;
    private final RewardClient rewardClient;

    public HttpClientTestController(AuthClient authClient, RewardClient rewardClient) {
        this.authClient = authClient;
        this.rewardClient = rewardClient;
    }

    /**
     * client
     */
    @PostMapping("/call")
    public Map<String, Object> test() {
        var userResponseBody = authClient.getUser("Bearer test-token");
//        System.out.println(userResponseBody);

        var rewardRequestBody = new RewardClient.RewardGrantRequest(userResponseBody.userId());
        var rewardResponseBody = rewardClient.reward(rewardRequestBody);
//        System.out.println(rewardRequestBody);

        return Map.of(
                "user", userResponseBody,
                "reward", rewardResponseBody
        );
    }

    /**
     * server
     */
    @GetMapping("/users")
    public Map<String, Object> getUser(
            @RequestHeader("Authorization") String authorization
    ) throws InterruptedException {
        log.info("[Controller users]");
        Thread.sleep(5000);
        log.info("[Controller uesrs end");
        return Map.of(
                "userId", 1L,
                "username", "test-user",
                "status", "ACTIVE"
        );
    }

    @PostMapping("rewards")
    public Map<String, Object> reward(
            @RequestBody Map<String, Object> request
    ) {
        log.info("[Controller rewards]");
        return Map.of(
                "rewardId", 100L,
                "userId", request.get("userId"),
                "status", "GRANTED"
        );
    }
}

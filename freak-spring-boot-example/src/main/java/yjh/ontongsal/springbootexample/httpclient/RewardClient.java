package yjh.ontongsal.springbootexample.httpclient;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import yjh.ontongsal.common.http.HttpClient;

@HttpClient("reward-api")
public interface RewardClient {

    @PostExchange("/http-client-test/rewards")
    RewardGrantResponse reward(@RequestBody RewardGrantRequest request);

    record RewardGrantRequest(Long userId) {
    }

    record RewardGrantResponse(Long rewardId, Long userId, String status) {
    }
}

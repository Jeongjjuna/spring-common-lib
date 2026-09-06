package yjh.ontongsal.springapi;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/web/filter")
    public String test() {
        IO.println(MDC.get("traceId"));
        return "ok";
    }
}

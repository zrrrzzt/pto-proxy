package no.nav.pto_proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static no.nav.pto_proxy.filter.PreRequestZuulFilter.API_GW_KEY_HEADER;

@Slf4j
@RestController
@RequestMapping("/sink")
public class ProxySinkController {

    @GetMapping("/test-app/test")
    public String test(@RequestHeader(API_GW_KEY_HEADER) String apiGwKey) {
        log.info("Received key: " + apiGwKey);
        return "test";
    }

    @PostMapping("/test-app/greet")
    public GreetResponse greet(@RequestBody GreetRequest greetRequest) {
        return new GreetResponse("Hello " + greetRequest.name);
    }

    @Data
    public static class GreetRequest {
        String name;
    }

    @Data
    @AllArgsConstructor
    public static class GreetResponse {
        String greeting;
    }

}

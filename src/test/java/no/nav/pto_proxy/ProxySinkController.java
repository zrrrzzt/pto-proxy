package no.nav.pto_proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sink")
public class ProxySinkController {

    @GetMapping("/test-app/test")
    public String test() {
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

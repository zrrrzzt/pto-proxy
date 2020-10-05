package no.nav.pto_proxy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sink")
public class ProxySinkController {

    @GetMapping("/test-app/test")
    public String test() {
        return "test";
    }

}

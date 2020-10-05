package no.nav.pto_proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import({TestControllerConfig.class})
public class TestApplicationConfig {

    @Bean
    public ProxyConfig proxyConfig() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("test-app", "test-key");

        return new ProxyConfig("http://localhost:8080/sink", keyMap);
    }

}

package no.nav.pto_proxy.config;

import no.nav.pto_proxy.ApiGwProxyController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Configuration
@Import({ApiGwProxyController.class})
public class TestApplicationConfig {

    @Bean
    public ProxyConfig proxyConfig() {
        return new ProxyConfig("http://localhost", Collections.emptyMap());
    }

}

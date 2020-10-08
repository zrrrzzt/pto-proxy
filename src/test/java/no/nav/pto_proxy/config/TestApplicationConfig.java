package no.nav.pto_proxy.config;

import no.nav.pto_proxy.filter.PostRequestZuulFilter;
import no.nav.pto_proxy.filter.PreRequestZuulFilter;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@EnableZuulProxy
@Configuration
@Import({TestControllerConfig.class})
public class TestApplicationConfig {

    @Bean
    public ProxyConfig proxyConfig() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("test-app", "test-key");

        return new ProxyConfig("http://localhost:8080/sink", keyMap);
    }

    @Bean
    public PreRequestZuulFilter proxyPreFilter() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("test-app", "test-key");

        return new PreRequestZuulFilter("/proxy2", keyMap);
    }

    @Bean
    public PostRequestZuulFilter proxyPostFilter() {
        return new PostRequestZuulFilter();
    }

}

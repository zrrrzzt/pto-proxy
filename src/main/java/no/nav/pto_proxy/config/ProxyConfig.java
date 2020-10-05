package no.nav.pto_proxy.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ProxyConfig {
    String apiGwUrl;
    // Map of proxied applications and the API-gw key for that application
    Map<String, String> keyMap;
}

package no.nav.pto_proxy.utils;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ProxyCorsConfigSource implements CorsConfigurationSource {

    private final CorsConfiguration corsConfiguration;

    private final String proxyPathPrefix;

    public ProxyCorsConfigSource(String proxyPathPrefix) {
        this.proxyPathPrefix = proxyPathPrefix;

        corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "HEAD", "PUT", "DELETE", "PATCH"));
        corsConfiguration.setMaxAge(1800L); // 30 min
    }

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith(proxyPathPrefix)) {
            return null;
        }

        return corsConfiguration;
    }

}

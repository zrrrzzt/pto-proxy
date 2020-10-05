package no.nav.pto_proxy.config;

import no.nav.common.auth.context.UserRole;
import no.nav.common.auth.oidc.filter.OidcAuthenticationFilter;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.log.LogFilter;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.pto_proxy.ApiGwProxyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.common.auth.Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME;
import static no.nav.common.auth.oidc.filter.OidcAuthenticator.fromConfigs;
import static no.nav.common.utils.EnvironmentUtils.isDevelopment;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

@Configuration
public class FilterConfig {

    private final static List<String> PROXIED_APPLICTIONS = List.of(
//            "veilarbaktivitet",
//            "veilarboppfolging",
//            "veilarbdialog",
//            "veilarblest",
//            "veilarbperson",
            "veilarbvedtakinfo"
    );

    private OidcAuthenticatorConfig azureAdB2CAuthConfig(EnvironmentProperties properties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(properties.getAadB2cDiscoveryUrl())
                .withClientId(properties.getAadB2cClientId())
                .withIdTokenCookieName(AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME)
                .withUserRole(UserRole.EKSTERN);
    }

    @Bean
    public FilterRegistrationBean logFilterRegistrationBean() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter(requireApplicationName(), isDevelopment().orElse(false)));
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistrationBean(EnvironmentProperties properties) {
        FilterRegistrationBean<OidcAuthenticationFilter> registration = new FilterRegistrationBean<>();
        OidcAuthenticationFilter authenticationFilter = new OidcAuthenticationFilter(
                fromConfigs(azureAdB2CAuthConfig(properties))
        );

        registration.setFilter(authenticationFilter);
        registration.setOrder(2);
        registration.addUrlPatterns("/proxy/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean proxyFilterRegistrationBean(EnvironmentProperties properties) {
        FilterRegistrationBean<ApiGwProxyFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new ApiGwProxyFilter("/proxy", properties.getApiGwUrl(), createProxyConfig(PROXIED_APPLICTIONS)));
        registration.setOrder(3);
        registration.addUrlPatterns("/proxy/*");
        return registration;
    }

    private static Map<String, String> createProxyConfig(List<String> applications) {
        Map<String, String> proxyConfig = new HashMap<>();

        applications.forEach(app -> {
            String apiGwKeyEnvName = "API_GW_KEY_" + app.toUpperCase();
            String apiGwKey = EnvironmentUtils.getRequiredProperty(apiGwKeyEnvName);
            proxyConfig.put(app, apiGwKey);
        });

        return proxyConfig;
    }

}

package no.nav.pto_proxy.config;

import no.nav.common.auth.context.UserRole;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.log.LogFilter;
import no.nav.pto_proxy.utils.ProxyCorsConfigSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;

import static no.nav.common.auth.Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME;
import static no.nav.common.utils.EnvironmentUtils.isDevelopment;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

@Configuration
public class FilterConfig {

    private OidcAuthenticatorConfig loginserviceIdportenConfig(EnvironmentProperties properties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(properties.getLoginserviceIdportenDiscoveryUrl())
                .withClientId(properties.getLoginserviceIdportenAudience())
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
    public FilterRegistrationBean corsFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter(new ProxyCorsConfigSource("/proxy")));
        registration.setOrder(2);
        registration.addUrlPatterns("/proxy/*");
        return registration;
    }

//    @Bean
//    public FilterRegistrationBean authenticationFilterRegistrationBean(EnvironmentProperties properties) {
//        FilterRegistrationBean<OidcAuthenticationFilter> registration = new FilterRegistrationBean<>();
//        OidcAuthenticationFilter authenticationFilter = new OidcAuthenticationFilter(
//                fromConfigs(loginserviceIdportenConfig(properties))
//        );
//
//        registration.setFilter(authenticationFilter);
//        registration.setOrder(3);
//        registration.addUrlPatterns("/proxy/*");
//        return registration;
//    }

}

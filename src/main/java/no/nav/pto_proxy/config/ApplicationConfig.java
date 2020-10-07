package no.nav.pto_proxy.config;

import no.nav.pto_proxy.utils.ProxyUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {

    private final static List<String> PROXIED_APPLICATIONS = List.of(
            "veilarbaktivitet",
            "veilarboppfolging",
            "veilarbdialog",
            "veilarblest",
            "veilarbperson",
            "veilarbvedtakinfo",
            "veilarbregistrering",
            "veilarbjobbsokerkompetanse"
    );

    @Bean
    public ProxyConfig proxyConfig(EnvironmentProperties properties) {
        return new ProxyConfig(properties.getApiGwUrl(), ProxyUtils.createProxyKeyMap(PROXIED_APPLICATIONS));
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/proxy/**")
                        .allowedOrigins("*.nav.no")
                        .allowedMethods("*")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }

}

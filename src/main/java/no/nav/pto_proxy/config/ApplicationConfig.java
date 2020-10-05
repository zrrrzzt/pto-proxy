package no.nav.pto_proxy.config;

import no.nav.pto_proxy.ProxyUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {

    public final static List<String> PROXIED_APPLICATIONS = List.of(
//            "veilarbaktivitet",
//            "veilarboppfolging",
//            "veilarbdialog",
//            "veilarblest",
//            "veilarbperson",
            "veilarbvedtakinfo"
    );

    @Bean
    public ProxyConfig proxyConfig(EnvironmentProperties properties) {
        return new ProxyConfig(properties.getApiGwUrl(), ProxyUtils.createProxyKeyMap(PROXIED_APPLICATIONS));
    }

}

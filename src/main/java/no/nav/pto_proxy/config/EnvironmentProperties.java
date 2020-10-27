package no.nav.pto_proxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.env")
public class EnvironmentProperties {

    private String loginserviceIdportenAudience;

    private String loginserviceIdportenDiscoveryUrl;
}

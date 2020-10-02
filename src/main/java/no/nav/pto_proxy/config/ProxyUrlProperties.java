package no.nav.pto_proxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.proxy")
public class ProxyUrlProperties {

    private String veilarbaktivitetUrl;

    private String veilarboppfolgingUrl;

    private String veilarbdialogUrl;

    private String veilarblestUrl;

    private String veilarbpersonUrl;

    private String veilarbvedtakinfoUrl;

}

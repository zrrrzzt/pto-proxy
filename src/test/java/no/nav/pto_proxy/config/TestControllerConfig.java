package no.nav.pto_proxy.config;

import no.nav.pto_proxy.ProxySinkController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ProxySinkController.class})
public class TestControllerConfig {}

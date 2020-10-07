package no.nav.pto_proxy.config;

import no.nav.pto_proxy.ProxySinkController;
import no.nav.pto_proxy.controller.ApiGwProxyController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApiGwProxyController.class, ProxySinkController.class})
public class TestControllerConfig {}

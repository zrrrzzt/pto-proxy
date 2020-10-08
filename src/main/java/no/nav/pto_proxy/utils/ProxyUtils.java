package no.nav.pto_proxy.utils;

import no.nav.common.utils.EnvironmentUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyUtils {

    public static Map<String, String> createApiGwKeyMap(List<String> applications) {
        Map<String, String> proxyConfig = new HashMap<>();

        applications.forEach(app -> {
            String apiGwKeyEnvName = "API_GW_KEY_" + app.toUpperCase();
            String apiGwKey = EnvironmentUtils.getRequiredProperty(apiGwKeyEnvName);
            proxyConfig.put(app, apiGwKey);
        });

        return proxyConfig;
    }

}

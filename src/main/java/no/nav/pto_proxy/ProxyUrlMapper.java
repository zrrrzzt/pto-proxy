package no.nav.pto_proxy;

import no.nav.pto_proxy.config.ProxyUrlProperties;

import java.util.HashMap;
import java.util.Map;

public class ProxyUrlMapper {

    private final Map<String, String> proxyUrlMap;

    public ProxyUrlMapper(ProxyUrlProperties proxyUrlProperties) {
        proxyUrlMap = createProxyUrlMap(proxyUrlProperties);
    }

    /**
     * Checks if a request has a proxy mapping.
     * @param requestPath request path (/veilarbaktivitet/api/example)
     * @return true if the path has a corresponding proxy mapping, false otherwise
     */
    public boolean hasProxyMapping(String requestPath) {
        return proxyUrlMap.containsKey(getApplicationFromRequestPath(requestPath));
    }

    public String mapRequestPathToProxyUrl(String requestPath) {
        String application = getApplicationFromRequestPath(requestPath);
        String proxyUrl = proxyUrlMap.get(application);

        if (proxyUrl == null) {
            throw new IllegalStateException("Unable to map request path to proxy url " + requestPath);
        }

        return proxyUrl;
    }

    private static String getApplicationFromRequestPath(String requestPath) {
        return requestPath.split("/")[0];
    }

    private static Map<String, String> createProxyUrlMap(ProxyUrlProperties proxyUrlProperties) {
        Map<String, String> proxyUrlMap = new HashMap<>();

        proxyUrlMap.put("veilarbaktivitet", proxyUrlProperties.getVeilarbaktivitetUrl());
        proxyUrlMap.put("veilarboppfolging", proxyUrlProperties.getVeilarboppfolgingUrl());
        proxyUrlMap.put("veilarbdialog", proxyUrlProperties.getVeilarbdialogUrl());
        proxyUrlMap.put("veilarblest", proxyUrlProperties.getVeilarblestUrl());
        proxyUrlMap.put("veilarbperson", proxyUrlProperties.getVeilarbpersonUrl());
        proxyUrlMap.put("veilarbvedtakinfo", proxyUrlProperties.getVeilarbvedtakinfoUrl());

        return proxyUrlMap;
    }

}

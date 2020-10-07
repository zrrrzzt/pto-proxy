package no.nav.pto_proxy.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProxyHeaderBlacklist {

    private final static Set<String> BLACKLISTED_HEADERS = new HashSet<>(List.of(
            // These are the "hop-by-hop" headers that should not be copied.
            "connection",
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailers",
            "transfer-encoding",
            "upgrade",

            // We set these ourselves
            "content-length",
            "content-type",

            // Not needed
            "vary",
            "content-encoding",
            "accept-encoding"
    ));

    public static boolean isNotListed(String headerName) {
        return !BLACKLISTED_HEADERS.contains(headerName.toLowerCase());
    }

}

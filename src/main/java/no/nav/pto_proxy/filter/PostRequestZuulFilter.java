package no.nav.pto_proxy.filter;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.log.MDCConstants;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;

import static no.nav.common.log.LogFilter.PREFERRED_NAV_CALL_ID_HEADER_NAME;
import static no.nav.common.utils.StringUtils.of;

@Slf4j
public class PostRequestZuulFilter extends ZuulFilter {

    private final static String NAV_CALL_ID = PREFERRED_NAV_CALL_ID_HEADER_NAME.toLowerCase();

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        // The LogFilter sets Nav-Call-Id before the response is sent back so we should remove duplicates
        filterOutDuplicateNavCallId(RequestContext.getCurrentContext());
        return null;
    }

    private void filterOutDuplicateNavCallId(RequestContext ctx) {
        String callId = of(MDC.get(MDCConstants.MDC_CALL_ID)).orElse("");
        List<Pair<String, String>> filteredResponseHeaders = new ArrayList<>();
        List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();

        if (zuulResponseHeaders != null) {
            for (Pair<String, String> header : zuulResponseHeaders) {
                String headerName = header.first().toLowerCase();

                // Ikke videresend call id med lik id
                if (headerName.equals(NAV_CALL_ID) && header.second().equals(callId)) {
                    continue;
                }

                Pair<String, String> pair = new Pair<>(header.first(), header.second());
                filteredResponseHeaders.add(pair);
            }
        }

        ctx.put("zuulResponseHeaders", filteredResponseHeaders);
    }

}

package no.nav.pto_proxy.filter;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static no.nav.common.log.LogFilter.PREFERRED_NAV_CALL_ID_HEADER_NAME;

@Slf4j
public class PostRequestZuulFilter extends ZuulFilter {

    private final static String NAV_CALL_ID = PREFERRED_NAV_CALL_ID_HEADER_NAME.toLowerCase();

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        // The LogFilter sets Nav-Call-Id before the response is sent back
        filterOutDuplicateNavCallId(ctx);

        log.info("Proxy response: {} {} {}", response.getStatus(), request.getMethod(), request.getRequestURL());

        return null;
    }

    private void filterOutDuplicateNavCallId(RequestContext ctx) {
        List<Pair<String, String>> filteredResponseHeaders = new ArrayList<>();

        List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
        if (zuulResponseHeaders != null) {
            for (Pair<String, String> header : zuulResponseHeaders) {
                if (!header.first().toLowerCase().equals(NAV_CALL_ID)) {
                    Pair<String, String> pair = new Pair<>(header.first(), header.second());
                    filteredResponseHeaders.add(pair);
                }
            }
        }

        ctx.put("zuulResponseHeaders", filteredResponseHeaders);
    }

}

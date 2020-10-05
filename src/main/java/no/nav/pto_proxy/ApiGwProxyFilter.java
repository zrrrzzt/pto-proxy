package no.nav.pto_proxy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.rest.client.RestClient;
import okhttp3.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public class ApiGwProxyFilter implements Filter {

    private final static String API_GW_KEY_HEADER = "x-nav-apiKey";

    private final String proxyPathPrefix;

    private final String apiGwUrl;

    private final OkHttpClient proxyClient;

    // Map of proxied applications and the API-gw key for that application
    private final Map<String, String> proxyConfig;

    public ApiGwProxyFilter(String proxyPathPrefix, String apiGwUrl, Map<String, String> proxyConfig) {
        this.proxyPathPrefix = proxyPathPrefix;
        this.apiGwUrl = apiGwUrl;
        this.proxyConfig = proxyConfig;

        proxyClient = RestClient.baseClient();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String fullRequestUrl = UrlUtils.getFullUrl(request);
        String urlWithoutPrefix = UrlUtils.stripStartPath(proxyPathPrefix, fullRequestUrl);
        String appName = UrlUtils.getFirstSegment(urlWithoutPrefix);

        String apiGwKey = proxyConfig.get(appName);

        if (apiGwKey == null) {
            response.setStatus(404);
            return;
        }

        String proxyUrl = no.nav.common.utils.UrlUtils.joinPaths(apiGwUrl, urlWithoutPrefix);
        Request proxyRequest = createProxyRequest(proxyUrl, apiGwKey, request);

        try (Response proxyResponse = proxyClient.newCall(proxyRequest).execute()) {
            copyFromProxyResponse(proxyResponse, response);
        } catch (Exception e) {
            log.error("Proxy request feilet. FullRequestUrl: {}", fullRequestUrl, e);
            response.setStatus(500);
        }
    }

    private static Request createProxyRequest(String proxyUrl, String apiGwKey, HttpServletRequest request) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(proxyUrl);

        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            request.getHeaders(headerName).asIterator().forEachRemaining(headerValue -> {
                requestBuilder.addHeader(headerName, headerValue);
            });
        });

        requestBuilder.header(API_GW_KEY_HEADER, apiGwKey);

        if (request.getContentLengthLong() > 0) {
            MediaType contentType = MediaType.get(request.getContentType());
            byte[] requestContent = getRequestContent(request);

            requestBuilder.method(request.getMethod(), RequestBody.create(contentType, requestContent));
        } else {
            requestBuilder.method(request.getMethod(), null);
        }

        return requestBuilder.build();
    }

    private static void copyFromProxyResponse(Response proxyResponse, HttpServletResponse response) {
        response.setStatus(proxyResponse.code());

        Map<String, List<String>> headers = proxyResponse.headers().toMultimap();

        headers.forEach((headerName, headerValues) -> {
            headerValues.forEach(value -> {
                response.addHeader(headerName, value);
            });
        });

        ResponseBody body = proxyResponse.body();

        if (body != null) {
            try {
                setResponseBodyContent(body.bytes(), response);
                response.setContentLengthLong(body.contentLength());

                if (body.contentType() != null) {
                    response.setContentType(body.contentType().toString());
                }
            } catch (IOException e) {
                log.error("Failed to copy proxy response body", e);
                response.setStatus(500);
            }
        }
    }

    @SneakyThrows
    private static byte[] getRequestContent(HttpServletRequest request) {
        try (InputStream os = request.getInputStream()) {
            return os.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to get request body", e);
            throw e;
        }
    }

    @SneakyThrows
    private static void setResponseBodyContent(byte[] bodyContent, HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            os.write(bodyContent, 0, bodyContent.length);
        } catch (Exception e) {
            log.error("Failed to set response body", e);
            throw e;
        }
    }

}

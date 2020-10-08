package no.nav.pto_proxy.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.rest.client.RestClient;
import no.nav.pto_proxy.config.ProxyConfig;
import no.nav.pto_proxy.utils.ProxyHeaderBlacklist;
import no.nav.pto_proxy.utils.UrlUtils;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/proxy2")
public class ApiGwProxyController {

    public final static String API_GW_KEY_HEADER = "x-nav-apiKey";

    private final OkHttpClient proxyClient;

    private final ProxyConfig proxyConfig;

    public ApiGwProxyController(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
        proxyClient = RestClient.baseClient();
    }

    @RequestMapping("**")
    public ResponseEntity proxy(HttpServletRequest request) {

        // Ex: /proxy/veilarbvedtakinfo/api/ping?test=true
        String pathWithQueryString = UrlUtils.getPathWithQueryString(request);

        // Ex: /veilarbvedtakinfo/api/ping?test=true
        String pathWithoutPrefix = UrlUtils.stripStartPath("/proxy2", pathWithQueryString);

        // Ex: veilarbvedtakinfo
        String appName = UrlUtils.getFirstSegment(pathWithoutPrefix);

        String apiGwKey = proxyConfig.getKeyMap().get(appName);

        if (apiGwKey == null) {
            log.error("Could not find key for application: {} and url: {}", appName, pathWithQueryString);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String proxyUrl = no.nav.common.utils.UrlUtils.joinPaths(proxyConfig.getApiGwUrl(), pathWithoutPrefix);
        Request proxyRequest = createProxyRequest(proxyUrl, apiGwKey, request);

        try (Response proxyResponse = proxyClient.newCall(proxyRequest).execute()) {
            return createResponseEntity(proxyResponse);
        } catch (Exception e) {
            log.error("Proxy request feilet. Path: {}", pathWithQueryString, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Request createProxyRequest(String proxyUrl, String apiGwKey, HttpServletRequest request) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(proxyUrl);

        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (ProxyHeaderBlacklist.isNotListed(headerName)) {
                request.getHeaders(headerName).asIterator().forEachRemaining(headerValue -> {
                    requestBuilder.addHeader(headerName, headerValue);
                });
            }
        });

        requestBuilder.header(API_GW_KEY_HEADER, apiGwKey);

        if (request.getContentLengthLong() > 0) {
            byte[] requestContent = getRequestContent(request);

            String contentType = request.getContentType();
            MediaType mediaType = contentType != null
                    ? MediaType.get(contentType)
                    : null;

            requestBuilder.method(request.getMethod(), RequestBody.create(mediaType, requestContent));
        } else {
            requestBuilder.method(request.getMethod(), null);
        }

        return requestBuilder.build();
    }

    private static ResponseEntity createResponseEntity(Response proxyResponse) throws IOException {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(proxyResponse.code());

        Map<String, List<String>> headers = proxyResponse.headers().toMultimap();

        headers.forEach((headerName, headerValues) -> {
            if (ProxyHeaderBlacklist.isNotListed(headerName)) {
                responseBuilder.header(headerName, headerValues.toArray(new String[0]));
            }
        });

        ResponseBody body = proxyResponse.body();
        byte[] bytes = body != null ? body.bytes() : new byte[0];

        if (bytes.length > 0) {
            if (body.contentType() != null) {
                responseBuilder.contentType(org.springframework.http.MediaType.valueOf(body.contentType().toString()));
            } else {
                // Default to text/plain if the proxy response does not contain a Content-Type
                responseBuilder.contentType(org.springframework.http.MediaType.TEXT_PLAIN);
            }

            return responseBuilder.body(bytes);
        }

        return responseBuilder.build();
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

}

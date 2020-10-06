package no.nav.pto_proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.pto_proxy.config.ProxyConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.pto_proxy.ApiGwProxyController.API_GW_KEY_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ApiGwProxyController.class)
public class ApiGwProxyControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(43657);

    private static final Map<String, String> KEY_MAP;

    static {
        KEY_MAP = new HashMap<>();
        KEY_MAP.put("test-app1", "test-key1");
        KEY_MAP.put("test-app2", "test-key2");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class AdditionalConfig {
        @Bean
        public ProxyConfig proxyConfig() {
            return new ProxyConfig("http://localhost:43657", KEY_MAP);
        }
    }

    @Test
    public void should_proxy_empty_request() throws Exception {
        givenThat(WireMock.get(urlEqualTo("/test-app1/hello"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get("/proxy/test-app1/hello"))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    public void should_add_api_gw_header() throws Exception {
        givenThat(WireMock.get(urlEqualTo("/test-app1/hello"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get("/proxy/test-app1/hello"));

        verify(getRequestedFor(urlMatching("/test-app1/hello"))
                .withHeader(API_GW_KEY_HEADER, matching(KEY_MAP.get("test-app1"))));

    }

    @Test
    public void should_forward_request_headers() throws Exception {
        givenThat(WireMock.get(urlEqualTo("/test-app1/hello"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/proxy/test-app1/hello")
                    .header("Custom-Header1", "test1")
                    .header("Custom-Header2", "test2")
        );

        verify(
                getRequestedFor(urlMatching("/test-app1/hello"))
                        .withHeader("Custom-Header1", matching("test1"))
                        .withHeader("Custom-Header2", matching("test2"))
        );
    }

    @Test
    public void should_forward_response_headers() throws Exception {
        givenThat(WireMock.get(urlEqualTo("/test-app1/hello"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Custom-Header1", "test1")
                                .withHeader("Custom-Header2", "test2")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/proxy/test-app1/hello"))
                .andExpect(MockMvcResultMatchers.header().string("Custom-Header1", "test1"))
                .andExpect(MockMvcResultMatchers.header().string("Custom-Header2", "test2"));
    }

    @Test
    public void should_return_404_for_missing_proxy() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/proxy/test-app3/hello"))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    public void should_send_request_body_to_proxy_without_content_type() throws Exception {
        String requestData = objectMapper.writeValueAsString(new RequestData("Ola Nordmann"));

        givenThat(WireMock.post(urlEqualTo("/test-app1/send"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.post("/proxy/test-app1/send").content(requestData));

        verify(postRequestedFor(urlMatching("/test-app1/send"))
                .withoutHeader("Content-Type")
                .withRequestBody(new EqualToJsonPattern(requestData, true, false)));
    }

    @Test
    public void should_send_request_body_to_proxy_with_content_type() throws Exception {
        String requestData = objectMapper.writeValueAsString(new RequestData("Ola Nordmann"));

        givenThat(WireMock.post(urlEqualTo("/test-app1/send"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.post("/proxy/test-app1/send")
                .content(requestData)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        verify(postRequestedFor(urlMatching("/test-app1/send"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(new EqualToJsonPattern(requestData, true, false)));
    }

    @Test
    public void should_return_response_from_proxy_with_default_content_type_text() throws Exception {
        String responseData = objectMapper.writeValueAsString(new ResponseData("Ola Nordmann"));

        givenThat(WireMock.get(urlEqualTo("/test-app1/receive"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withBody(responseData)
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/proxy/test-app1/receive"))
                .andExpect(MockMvcResultMatchers.content().json(responseData))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    public void should_return_response_from_proxy_with_content_type() throws Exception {
        String responseData = objectMapper.writeValueAsString(new ResponseData("Ola Nordmann"));

        givenThat(WireMock.get(urlEqualTo("/test-app1/receive"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseData)
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/proxy/test-app1/receive"))
                .andExpect(MockMvcResultMatchers.content().json(responseData))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
    }

    @Data
    @AllArgsConstructor
    public static class RequestData {
        String name;
    }

    @Data
    @AllArgsConstructor
    public static class ResponseData {
        String name;
    }

}

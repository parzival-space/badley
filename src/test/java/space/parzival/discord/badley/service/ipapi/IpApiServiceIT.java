package space.parzival.discord.badley.service.ipapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import space.parzival.discord.badley.configuration.properties.tools.IpApiProperties;
import space.parzival.discord.badley.service.ipapi.model.IpApiGeolocationResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(IpApiProperties.class)
@RestClientTest(value = IpApiService.class, properties = "badley.ai.tools.ipapi.enabled=true")
class IpApiServiceIT {
    @Autowired
    private IpApiService service;

    @Autowired
    private IpApiProperties properties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void getGeolocationInfo_returns_validData() {
        server.expect(anything())
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/ipapi/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        IpApiGeolocationResponse response = service.getGeolocationInfo();

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("United States", response.getCountry());
        assertEquals("Virginia", response.getRegionName());
        assertEquals("Ashburn", response.getCity());
        assertEquals("20149", response.getZip());
        assertEquals("Google Public DNS", response.getOrg());
        assertEquals("AS15169 Google LLC", response.getAs());
        assertEquals("GOOGLE", response.getAsName());
        assertEquals("dns.google", response.getReverse());
        assertFalse(response.isMobile());
        assertFalse(response.isProxy());
        assertTrue(response.isHosting());
        assertEquals("8.8.8.8", response.getQuery());
    }

    @Test
    void getGeolocationInfo_returns_errorData() {
        server.expect(anything())
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/ipapi/error-response.json"),
                MediaType.APPLICATION_JSON
            ));

        IpApiGeolocationResponse response = service.getGeolocationInfo();

        assertNotNull(response);
        assertEquals("fail", response.getStatus());
        assertEquals("invalid query", response.getMessage());
        assertEquals("8.8.8.8.8", response.getQuery());
    }
}

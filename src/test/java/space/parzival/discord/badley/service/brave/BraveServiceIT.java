package space.parzival.discord.badley.service.brave;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.BraveProperties;
import space.parzival.discord.badley.service.brave.model.BraveQueryResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(BraveProperties.class)
@RestClientTest(value = BraveService.class, properties = "badley.ai.tools.brave.token=token")
class BraveServiceIT {
    @Autowired
    private BraveService service;

    @MockitoSpyBean
    private BraveProperties properties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void query_returns_validData() {
        server.expect(request -> request.getURI().getPath().equals("/res/v1/web/search"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/brave/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        BraveQueryResponse result = service.query("test");

        assertNotNull(result);
        assertEquals("search", result.getType());
        assertEquals(10, result.getWeb().getResults().size());
        assertEquals("Parzival", result.getWeb().getResults().getFirst().getTitle());
        assertEquals("https://parzival.space/", result.getWeb().getResults().getFirst().getUrl());
    }

    @Test
    void query_returns_errorResponse() {
        server.expect(request -> request.getURI().getPath().equals("/res/v1/web/search"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/brave/error-response.json"),
                MediaType.APPLICATION_JSON
            ));

        BraveQueryResponse result = service.query("test");

        assertNotNull(result);
        assertEquals("ErrorResponse", result.getType());
        assertNull(result.getWeb());
    }
}

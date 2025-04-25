package space.parzival.discord.badley.service.google;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.GoogleProperties;
import space.parzival.discord.badley.service.google.model.GoogleQueryResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(GoogleProperties.class)
@RestClientTest(value = GoogleService.class, properties = {
    "badley.ai.tools.google.token=token",
    "badley.ai.tools.google.engine-id=engineId"
})
class GoogleServiceIT {
    @Autowired
    private GoogleService service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void query_returns_validData() {
        server.expect(queryParam("q", "test"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/google/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        GoogleQueryResponse response = service.query("test");

        assertNotNull(response);
        assertEquals("customsearch#search", response.getKind());
        assertEquals(10, response.getItems().size());
        assertEquals("parzival-space (Parzival) · GitHub", response.getItems().getFirst().getTitle());
        assertEquals("https://github.com/parzival-space", response.getItems().getFirst().getLink());
    }

    @Test
    void testQuery_returns_errorResponse() {
        server.expect(queryParam("q", "test"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/google/error-response.json"),
                MediaType.APPLICATION_JSON
            ));

        GoogleQueryResponse response = service.query("test");

        assertNotNull(response);
        assertNull(response.getKind());
        assertNull(response.getItems());
    }
}

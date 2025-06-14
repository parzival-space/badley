package space.parzival.discord.badley.service.duckduckgo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.tools.DuckDuckGoProperties;
import space.parzival.discord.badley.service.duckduckgo.model.DuckDuckGoInstantAnswersResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(DuckDuckGoProperties.class)
@RestClientTest(value = DuckDuckGoService.class, properties = "badley.ai.tools.duckduckgo.enabled=true")
class DuckDuckGoServiceIT {
    @Autowired
    private DuckDuckGoService duckDuckGoService;

    @MockitoSpyBean
    private DuckDuckGoProperties duckDuckGoProperties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void queryInstantAnswers_returns_validData() {
        server.expect(req -> req.getURI().getPath().equals("/"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/duckduckgo/answers/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        DuckDuckGoInstantAnswersResponse result = duckDuckGoService.queryInstantAnswers("Discord");

        assertNotNull(result);
        assertNotNull(result.getInfobox());
        assertEquals(27, result.getInfobox().getContent().size());
        assertEquals("Discord Inc.", result.getInfobox().getContent().getFirst().getValue());
        assertNotNull(result.getRelatedTopics());
        assertEquals(10, result.getRelatedTopics().size());
        assertEquals("Instant messaging clients for Linux", result.getRelatedTopics().getFirst().getText());
    }

    @Test
    void queryInstantAnswers_returns_emptyData() {
        server.expect(req -> req.getURI().getPath().equals("/"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/duckduckgo/answers/empty-response.json"),
                MediaType.APPLICATION_JSON
            ));

        DuckDuckGoInstantAnswersResponse result = duckDuckGoService.queryInstantAnswers("Discord");

        assertNotNull(result);
        assertNotNull(result.getInfobox());
        assertEquals(0, result.getInfobox().getContent().size());
        assertNotNull(result.getRelatedTopics());
        assertEquals(0, result.getRelatedTopics().size());
    }
}

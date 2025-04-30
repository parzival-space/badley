package space.parzival.discord.badley.service.wikipedia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.service.wikipedia.model.WikiParsePageResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryPagesResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryResponse;
import space.parzival.discord.badley.service.wikipedia.model.query.WikiParsePage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(value = WikipediaService.class, properties = {
    "badley.ai.tools.wikipedia.enabled=true"
})
class WikipediaServiceIT {
    @Autowired
    private WikipediaService service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void queryForPages_returns_emptyArticles() throws JsonProcessingException {
        String apiResponseJson = objectMapper.writeValueAsString(WikiQueryResponse.builder()
            .queryResult(WikiQueryPagesResponse.builder()
                .pages(Map.of())
                .build())
            .build());
        server.expect(requestTo("/?action=query&format=json&titles=Test&prop=extracts%7Cinfo"))
            .andRespond(withSuccess(apiResponseJson, MediaType.APPLICATION_JSON));

        WikiQueryPagesResponse response = service.queryForPages("Test");
        assertEquals(0, response.getPages().size());
    }

    @Test
    void queryForPages_throws_becauseNullResponse() {
        server.expect(requestTo("/?action=query&format=json&titles=Test&prop=extracts%7Cinfo"))
            .andRespond(withSuccess());

        assertThrows(IllegalArgumentException.class, () -> {
            service.queryForPages("Test");
        });
    }

    @Test
    void getRandomPage_returns_emptyArticles() throws JsonProcessingException {
        String apiResponseJson = objectMapper.writeValueAsString(WikiQueryResponse.builder()
            .queryResult(WikiQueryPagesResponse.builder()
                .pages(Map.of())
                .build())
            .build());
        server.expect(requestTo("/?action=query&generator=random&grnnamespace=0&grnlimit=1&prop=extracts%7Cinfo&format=json"))
            .andRespond(withSuccess(apiResponseJson, MediaType.APPLICATION_JSON));

        WikiQueryPagesResponse response = service.getRandomPage();
        assertEquals(0, response.getPages().size());
    }

    @Test
    void getRandomPage_throws_becauseNullResponse() {
        server.expect(requestTo("/?action=query&generator=random&grnnamespace=0&grnlimit=1&prop=extracts%7Cinfo&format=json"))
            .andRespond(withSuccess());

        assertThrows(IllegalArgumentException.class, () -> {
            service.getRandomPage();
        });
    }

    @Test
    void parsePage_returns_validData() throws JsonProcessingException {
        String apiResponseJson = objectMapper.writeValueAsString(WikiParsePageResponse.builder()
            .parseResult(WikiParsePage.builder()
                .parsedText("Hello World")
                .build())
            .build());
        server.expect(requestTo("/?action=parse&format=json&page=Test&prop=text&formatversion=2"))
            .andRespond(withSuccess(apiResponseJson, MediaType.APPLICATION_JSON));

        WikiParsePageResponse response = service.parsePage("Test");
        assertEquals("Hello World", response.getParseResult().getParsedText());
    }
}

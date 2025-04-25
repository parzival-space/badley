package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.wikipedia.WikipediaService;
import space.parzival.discord.badley.service.wikipedia.model.WikiParsePageResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryPagesResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryResponse;
import space.parzival.discord.badley.service.wikipedia.model.query.WikiParsePage;
import space.parzival.discord.badley.service.wikipedia.model.query.WikiQueryPage;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WikipediaToolsTest {

    @Test
    void queryWikipedia_return_validData() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.queryForPages("test"))
                .thenReturn(createWikiQueryPagesResponse(1234, "Test extract"));

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.queryWikipedia("test");

        assertNotNull(result);
        assertTrue(result.contains("Title: Test Title"));
        assertTrue(result.contains("Page ID: 1234"));
        assertTrue(result.contains("Test extract"));
    }

    @Test
    void queryWikipedia_return_notFound() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.queryForPages("test"))
                .thenReturn(createWikiQueryPagesResponse(-1, "Test extract"));

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.queryWikipedia("test");

        assertNotNull(result);
        assertTrue(result.contains("No page found for the given title."));
    }

    @Test
    void queryWikipedia_return_parsedPageInformation() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.queryForPages("test"))
                .thenReturn(createWikiQueryPagesResponse(1234, ""));
        when(wikipediaService.parsePage("Test Title"))
                .thenReturn(createWikiParsePageResponse());

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.queryWikipedia("test");

        assertNotNull(result);
        assertTrue(result.contains("Title: Test Title"));
        assertTrue(result.contains("Page ID: 1234"));
        assertTrue(result.contains("Parsed text"));
    }

    @Test
    void queryWikipedia_return_emptyParsedPageInformation() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.queryForPages("test"))
                .thenReturn(createWikiQueryPagesResponse(1234, ""));

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.queryWikipedia("test");

        assertNotNull(result);
        assertTrue(result.contains("No page content found for the given title."));
    }

    @Test
    void queryWikipedia_return_error() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.queryForPages("test"))
                .thenThrow(new RuntimeException());

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.queryWikipedia("test");

        assertNotNull(result);
        assertTrue(result.contains("Error while querying Wikipedia"));
    }

    @Test
    void randomWikipedia_return_validData() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.getRandomPage())
                .thenReturn(createWikiQueryPagesResponse(1234, "Test extract"));

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.randomWikipedia();

        assertNotNull(result);
        assertTrue(result.contains("Title: Test Title"));
        assertTrue(result.contains("Page ID: 1234"));
        assertTrue(result.contains("Test extract"));
    }

    @Test
    void randomWikipedia_return_error() {
        WikipediaService wikipediaService = mock(WikipediaService.class);
        when(wikipediaService.getRandomPage()).thenThrow(new RuntimeException());

        WikipediaTools wikipediaTools = new WikipediaTools(wikipediaService);
        String result = wikipediaTools.randomWikipedia();

        assertNotNull(result);
        assertTrue(result.contains("Error while retrieving random page from Wikipedia"));
    }

    private WikiQueryResponse createWikiQueryResponse(int pageId, String extract) {
        return WikiQueryResponse.builder()
                .queryResult(createWikiQueryPagesResponse(pageId, extract))
                .build();
    }

    private WikiQueryPagesResponse createWikiQueryPagesResponse(int pageId, String extract) {
        return WikiQueryPagesResponse.builder()
                .pages(Map.of(String.valueOf(pageId), createWikiQueryPage(pageId, extract)))
                .build();
    }

    private WikiQueryPage createWikiQueryPage(int pageId, String extract) {
        return WikiQueryPage.builder()
                .pageId(pageId)
                .namespace(1)
                .title("Test Title")
                .extract(extract)
                .contentModel("Test content model")
                .pageLanguage("en")
                .pageLanguageHtmlCode("en")
                .pageLanguageDirection("ltr")
                .lastUpdated(OffsetDateTime.now())
                .lastRevisionId(123456)
                .length(100)
                .build();
    }

    private WikiParsePageResponse createWikiParsePageResponse() {
        return WikiParsePageResponse.builder()
                .parseResult(createWikiParsePage())
                .build();
    }

    private WikiParsePage createWikiParsePage() {
        return WikiParsePage.builder()
                .title("Test Title")
                .pageId(1234)
                .parsedText("Parsed text")
                .build();
    }
}
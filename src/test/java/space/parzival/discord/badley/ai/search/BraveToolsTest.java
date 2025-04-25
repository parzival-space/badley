package space.parzival.discord.badley.ai.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.brave.BraveService;
import space.parzival.discord.badley.service.brave.model.BraveQueryResponse;
import space.parzival.discord.badley.service.brave.model.query.BraveQueryWebResult;
import space.parzival.discord.badley.service.brave.model.query.BraveQueryWebResults;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BraveToolsTest {
    private BraveService searchService;

    @BeforeEach
    void setUp() {
        searchService = mock(BraveService.class);
    }

    @Test
    void searchBrave_returns_validData() {
        when(searchService.query(anyString())).thenReturn(createBraveQueryResponse());

        BraveTools braveTools = new BraveTools(searchService);
        String result = braveTools.searchBrave("test query");

        assertNotNull(result);
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("Title: Example Title"));
        assertTrue(result.contains("Description: Example Description"));
        assertTrue(result.contains("Family Friendly: true"));
        assertTrue(result.contains("Language: en"));
    }

    @Test
    void searchBrave_returns_errorMessage() {
        when(searchService.query(anyString())).thenThrow(new RuntimeException("Test exception"));

        BraveTools braveTools = new BraveTools(searchService);
        String result = braveTools.searchBrave("test query");

        assertNotNull(result);
        assertTrue(result.contains("Error fetching web search results: Test exception"));
    }

    private BraveQueryResponse createBraveQueryResponse() {
        return BraveQueryResponse.builder()
                .web(createBraveQueryWebResults())
                .build();
    }

    private BraveQueryWebResults createBraveQueryWebResults() {
        return BraveQueryWebResults.builder()
                .resultType("web")
                .results(List.of(createBraveQueryWebResult()))
                .familyFriendly(true)
                .build();
    }

    private BraveQueryWebResult createBraveQueryWebResult() {
        return BraveQueryWebResult.builder()
                .url("https://example.com")
                .title("Example Title")
                .description("Example Description")
                .familyFriendly(true)
                .language("en")
                .build();
    }
}
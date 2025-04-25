package space.parzival.discord.badley.ai.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.google.GoogleService;
import space.parzival.discord.badley.service.google.model.GoogleQueryResponse;
import space.parzival.discord.badley.service.google.model.query.GoogleQueryResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoogleToolsTest {
    private GoogleService googleService;

    @BeforeEach
    void setUp() {
        googleService = mock(GoogleService.class);
    }

    @Test
    void searchGoogle_returns_validData() {
        when(googleService.query(anyString())).thenReturn(createGoogleQueryResponse());

        GoogleTools googleTools = new GoogleTools(googleService);
        String result = googleTools.searchGoogle("test query");

        assertNotNull(result);
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("Title: Example Title"));
        assertTrue(result.contains("Description: Example Description"));
    }

    @Test
    void searchGoogle_returns_errorMessage() {
        when(googleService.query(anyString())).thenThrow(new RuntimeException("Test exception"));

        GoogleTools googleTools = new GoogleTools(googleService);
        String result = googleTools.searchGoogle("test query");

        assertNotNull(result);
        assertTrue(result.contains("Error fetching web search results: Test exception"));
    }

    private GoogleQueryResponse createGoogleQueryResponse() {
        return GoogleQueryResponse.builder()
                .items(List.of(createGoogleQueryResult()))
                .build();
    }

    private GoogleQueryResult createGoogleQueryResult() {
        return GoogleQueryResult.builder()
                .kind("customsearch#result")
                .title("Example Title")
                .htmlTitle("Example Title")
                .link("https://example.com")
                .displayLink("example.com")
                .snippet("Example Description")
                .htmlSnippet("Example Description")
                .formattedUrl("https://example.com")
                .htmlFormattedUrl("https://example.com")
                .build();
    }
}
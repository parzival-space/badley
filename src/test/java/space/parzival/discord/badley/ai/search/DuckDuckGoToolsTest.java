package space.parzival.discord.badley.ai.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.duckduckgo.DuckDuckGoService;
import space.parzival.discord.badley.service.duckduckgo.model.DuckDuckGoInstantAnswersResponse;
import space.parzival.discord.badley.service.duckduckgo.model.instantanswers.Infobox;
import space.parzival.discord.badley.service.duckduckgo.model.instantanswers.InfoboxLabelValuePair;
import space.parzival.discord.badley.service.duckduckgo.model.instantanswers.RelatedTopic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DuckDuckGoToolsTest {
    private DuckDuckGoService duckDuckGoService;

    @BeforeEach
    void setUp() {
        duckDuckGoService = mock(DuckDuckGoService.class);
    }

    @Test
    void searchDuckDuckGo_returns_validData() {
        DuckDuckGoInstantAnswersResponse response = createResponse();
        when(duckDuckGoService.queryInstantAnswers(anyString())).thenReturn(response);

        DuckDuckGoTools duckDuckGoTools = new DuckDuckGoTools(duckDuckGoService);
        String result = duckDuckGoTools.searchDuckDuckGo("test query");

        assertNotNull(result);
        assertTrue(result.contains("Example Title"));
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("Example Description"));
        assertTrue(result.contains("Label 1: Value 1"));
        assertTrue(result.contains("Label 2: Value 2"));
        assertTrue(result.contains("Related Topic 1 (https://related1.com)"));
        assertTrue(result.contains("Related Topic 2 (https://related2.com)"));
    }

    @Test
    void searchDuckDuckGo_returns_errorMessage() {
        when(duckDuckGoService.queryInstantAnswers(anyString())).thenThrow(new RuntimeException("Test exception"));

        DuckDuckGoTools duckDuckGoTools = new DuckDuckGoTools(duckDuckGoService);
        String result = duckDuckGoTools.searchDuckDuckGo("test query");

        assertNotNull(result);
        assertTrue(result.contains("Error fetching web search results: Test exception"));
    }

    DuckDuckGoInstantAnswersResponse createResponse() {
        return DuckDuckGoInstantAnswersResponse.builder()
            .abstractUrl("https://example.com")
            .heading("Example Title")
            .abstractText("Example Description")
            .infobox(Infobox.builder()
                .content(List.of(
                    InfoboxLabelValuePair.builder().label("Label 1").value("Value 1").build(),
                    InfoboxLabelValuePair.builder().label("Label 2").value("Value 2").build()
                ))
                .build())
            .relatedTopics(List.of(
                RelatedTopic.builder()
                    .text("Related Topic 1")
                    .firstUrl("https://related1.com")
                    .build(),
                RelatedTopic.builder()
                    .text("Related Topic 2")
                    .firstUrl("https://related2.com")
                    .build()
            ))
            .build();
    }
}

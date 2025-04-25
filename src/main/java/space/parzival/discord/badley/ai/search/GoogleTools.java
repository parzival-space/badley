package space.parzival.discord.badley.ai.search;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.service.google.GoogleService;

import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.google.token")
@AllArgsConstructor
public class GoogleTools implements AiTools {
    private final GoogleService googleService;

    private static final String WEB_SEARCH_RESULT = """
            %s:
            - Title: %s
            - Description: %s
            """;

    @Tool(description = "Search the web for information.")
    public String searchGoogle(String query) {
        log.debug("AI is requesting web search results from Google for query: {}", query);

        try {
            var response = googleService.query(query);

            return response.getItems().stream().map(result ->
                    String.format(WEB_SEARCH_RESULT,
                            result.getLink(), result.getTitle(), result.getSnippet()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Error fetching web search results: {}", e.getMessage());
            return "Error fetching web search results: " + e.getMessage();
        }
    }
}

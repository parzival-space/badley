package space.parzival.discord.badley.ai.tools.search;

import com.google.api.services.customsearch.v1.CustomSearchAPI;
import com.google.api.services.customsearch.v1.model.Search;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.tools.AiTools;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.google.enabled", havingValue = "true")
@AllArgsConstructor
public class GoogleTools implements AiTools {
    private static final String WEB_SEARCH_RESULT = """
        ${url}:
        - Title: ${title}
        - Description: ${description}
        """.stripIndent();
    private final CustomSearchAPI googleCustomSearchAPI;

    @Tool(description = "Search the web for information.")
    public String searchGoogle(String query) {
        log.debug("AI is requesting web search results from Google for query: {}", query);

        try {
            Search response = googleCustomSearchAPI.cse().list()
                .setQ(query)
                .setSafe("off")
                .execute();

            return response.getItems().stream().map(result ->
                    StringSubstitutor.replace(WEB_SEARCH_RESULT, Map.of(
                        "url", result.getLink(),
                        "title", result.getTitle(),
                        "description", result.getSnippet()
                    )))
                .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("Error fetching web search results: {}", e.getMessage());
            return "Error fetching web search results: " + e.getMessage();
        }
    }
}

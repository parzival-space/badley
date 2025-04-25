package space.parzival.discord.badley.ai.search;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;
import space.parzival.discord.badley.service.brave.BraveService;
import space.parzival.discord.badley.service.brave.model.BraveQueryResponse;

import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.brave.token")
@AllArgsConstructor
public class BraveTools implements AiToolsService {
    private final BraveService braveService;

    private static final String WEB_SEARCH_RESULT = """
            %s:
            - Title: %s
            - Description: %s
            - Family Friendly: %b
            - Language: %s
            """;

    @Tool(description = "Search the web for information.")
    public String search(String query) {
        log.debug("AI is requesting web search results from Brave for query: {}", query);

        try {
            BraveQueryResponse response = braveService.query(query);

            return response.getWeb().getResults().stream().map(result ->
                    String.format(WEB_SEARCH_RESULT,
                            result.getUrl(), result.getTitle(), result.getDescription(),
                            result.isFamilyFriendly(), result.getLanguage()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Error fetching web search results: {}", e.getMessage());
            return "Error fetching web search results: " + e.getMessage();
        }
    }
}

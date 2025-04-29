package space.parzival.discord.badley.ai.search;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.service.brave.BraveService;
import space.parzival.discord.badley.service.brave.model.BraveQueryResponse;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.brave.token")
@AllArgsConstructor
public class BraveTools implements AiTools {
    private static final String WEB_SEARCH_RESULT = """
        ${url}:
        - Title: ${title}
        - Description: ${description}
        - Family Friendly: ${familyFriendly}
        - Language: ${language}
        """.stripIndent();
    private final BraveService braveService;

    @Tool(description = "Search the web for information.")
    public String searchBrave(String query) {
        log.debug("AI is requesting web search results from Brave for query: {}", query);

        try {
            BraveQueryResponse response = braveService.query(query);

            return response.getWeb().getResults().stream().map(result ->
                StringSubstitutor.replace(WEB_SEARCH_RESULT, Map.of(
                    "url", result.getUrl(),
                    "title", result.getTitle(),
                    "description", result.getDescription(),
                    "familyFriendly", result.isFamilyFriendly(),
                    "language", result.getLanguage()
                ))).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Error fetching web search results: {}", e.getMessage());
            return "Error fetching web search results: " + e.getMessage();
        }
    }
}

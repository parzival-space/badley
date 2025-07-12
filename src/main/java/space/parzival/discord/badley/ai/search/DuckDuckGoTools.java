package space.parzival.discord.badley.ai.search;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.service.duckduckgo.DuckDuckGoService;
import space.parzival.discord.badley.service.duckduckgo.model.DuckDuckGoInstantAnswersResponse;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.duckduckgo.enabled", havingValue = "true")
@AllArgsConstructor
public class DuckDuckGoTools implements AiTools {
    private static final String INSTANT_ANSWER_RESULT = """
        Title: ${title}
        URL: ${url}
        Summary: ${summary}
        
        Infobox:
        ${infobox}
        
        Related Topics:
        ${relatedTopics}
        """.stripIndent();
    private final DuckDuckGoService duckDuckGoService;

    @Tool(description = "Search real-time, up-to-date information and news from the Internet using DuckDuckGo. If this fails, use another search engine.")
    public String searchDuckDuckGo(String query) {
        log.debug("AI is requesting web search results from DuckDuckGo for query: {}", query);

        try {
            DuckDuckGoInstantAnswersResponse response = duckDuckGoService.queryInstantAnswers(query);

            return StringSubstitutor.replace(INSTANT_ANSWER_RESULT, Map.of(
                "title", response.getHeading(),
                "url", response.getAbstractUrl(),
                "summary", response.getAbstractText(),
                "infobox", response.getInfobox().getContent().stream()
                    .filter(lvp -> !lvp.getLabel().contains("instance")) // Exclude instance labels
                    .map(lvp -> "* " + lvp.getLabel() + ": " + lvp.getValue())
                    .collect(Collectors.joining("\n")),
                "relatedTopics", response.getRelatedTopics().stream()
                    .map(topic -> "* " + topic.getText() + " (" + topic.getFirstUrl() + ")")
                    .collect(Collectors.joining("\n"))
            ));
        } catch (Exception e) {
            log.error("Error fetching web search results: {}", e.getMessage());
            return "Error fetching web search results: " + e.getMessage();
        }
    }
}

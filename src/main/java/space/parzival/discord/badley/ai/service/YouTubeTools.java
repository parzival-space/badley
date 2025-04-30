package space.parzival.discord.badley.ai.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.youtube.enabled", havingValue = "true")
@AllArgsConstructor
public class YouTubeTools implements AiTools {
    private static final String SEARCH_RESULT_TEMPLATE = """
        ${result.id}:
        - Kind: ${result.kind}
        - Title: ${result.title}
        - Description: ${result.description}
        - Channel: ${result.channelTitle}
        - Published At: ${result.publishedAt}
        - Live Broadcast: ${result.liveBroadcastContent}
        """.stripIndent();
    private final YouTube youTube;

    @Tool(description = "Search for videos on YouTube")
    public String searchYouTube(String query) {
        log.debug("AI is searching for videos on YouTube for query: {}", query);

        try {
            SearchListResponse response = youTube.search()
                .list(List.of("id", "snippet"))
                .setQ(query)
                .execute();

            if (response.getItems().isEmpty()) {
                log.debug("No results found for query: {}", query);
                return "No results found for the given query.";
            }

            return response.getItems().stream().map(result -> StringSubstitutor.replace(SEARCH_RESULT_TEMPLATE, Map.of(
                "result.id", switch (result.getId().getKind()) {
                    case "youtube#video" -> "https://youtube.com/video/" + result.getId().getVideoId();
                    case "youtube#channel" -> "https://youtube.com/channel/" + result.getId().getChannelId();
                    case "youtube#playlist" -> "https://youtube.com/playlist/" + result.getId().getPlaylistId();
                    default -> result.getId().getKind();
                },
                "result.kind", result.getId().getKind(),
                "result.title", result.getSnippet().getTitle(),
                "result.description", result.getSnippet().getDescription(),
                "result.channelTitle", result.getSnippet().getChannelTitle(),
                "result.publishedAt", result.getSnippet().getPublishedAt(),
                "result.liveBroadcastContent", result.getSnippet().getLiveBroadcastContent()
            ))).collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            log.error("Error while searching YouTube: {}", e.getMessage());
            return "An error occurred while searching YouTube: " + e.getMessage();
        }
    }
}

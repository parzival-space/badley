package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;
import space.parzival.discord.badley.service.steam.SteamService;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;

import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.steam.token")
@AllArgsConstructor
public class SteamTools implements AiToolsService {
    private SteamService steam;

    private static final String GAME_INFO_TEMPLATE = """
            %s (%s):
            - ID: %s
            - Price: %.2f (%s)
            - Meta Score: %s
            - Platforms:
              - Windows: %b
              - Mac: %b
              - Linux: %b
            """;

    @Tool(description = "Search the Steam store.")
    public String searchStore(
            @ToolParam(description = "The search Query") String query,
            @ToolParam(description = "The result language written out. Example 'english'") String lang,
            @ToolParam(description = "Two character country code used for currency. Example: 'US'") String countryCode) {
        log.debug("AI is requesting Steam store search for query: {}, {}, {}", query, lang, countryCode);

        try {
            StoreSearchResponse response = steam.searchStore(query, lang, countryCode);

            if (response.getTotal() == 0) {
                log.debug("No results found for query: {}, {}", query, lang);
                return "No results found for your query.";
            }

            return response.getItems().stream().map(game -> String.format(
                    GAME_INFO_TEMPLATE,
                    game.getName(), game.getType(), game.getId(),
                    game.getPrice() != null ? game.getPrice().getFinalPrice() / 100.0 : 0,
                    game.getPrice() != null ? game.getPrice().getCurrency() : "N/A",
                    game.getMetaScore(), game.getPlatforms().isWindows(), game.getPlatforms().isMac(),
                    game.getPlatforms().isLinux()
            )).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Error fetching Steam store search results: {}", e.getMessage());
            return "Error fetching Steam store search results: " + e.getMessage();
        }
    }
}

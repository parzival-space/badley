package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;
import space.parzival.discord.badley.service.steam.SteamService;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.store.StoreFeaturedGame;

import java.time.format.DateTimeFormatter;
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

    private static final String GAME_INFO_SALE_TEMPLATE = """
            %s:
            - ID: %s
            - Price: %.2f (%s)
            - Discount: %d%% (was %.2f)
            - Discount Expiration: %s
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

    @Tool(description = "Gets the current featured categories from the Steam store.")
    public String getFeaturedCategories(
            @ToolParam(description = "The result language written out. Example 'english'") String lang,
            @ToolParam(description = "Two character country code used for currency. Example: 'US'") String countryCode) {
        log.debug("AI is requesting Steam store featured categories: {}, {}", lang, countryCode);

        try {
            StoreFeaturedResponse response = steam.getFeaturedCategories(lang, countryCode);
            StringBuilder result = new StringBuilder();

            // special
            if (response.getSpecials() != null) {
                result.append(response.getSpecials().getName());
                result.append("\n");
                response.getSpecials().getItems().forEach(game -> result.append(fillGameInfoSaleTemplate(game)));
            }

            // coming soon
            if (response.getComingSoon() != null) {
                result.append(response.getComingSoon().getName());
                result.append("\n");
                response.getComingSoon().getItems().forEach(game -> result.append(fillGameInfoSaleTemplate(game)));
            }

            // top sellers
            if (response.getTopSellers() != null) {
                result.append(response.getTopSellers().getName());
                result.append("\n");
                response.getTopSellers().getItems().forEach(game -> result.append(fillGameInfoSaleTemplate(game)));
            }

            // new sellers
            if (response.getNewReleases() != null) {
                result.append(response.getNewReleases().getName());
                result.append("\n");
                response.getNewReleases().getItems().forEach(game -> result.append(fillGameInfoSaleTemplate(game)));
            }

            return result.toString();
        } catch (Exception e) {
            log.error("Error fetching Steam store featured categories: {}", e.getMessage());
            return "Error fetching Steam store featured categories: " + e.getMessage();
        }
    }

    private String fillGameInfoSaleTemplate(StoreFeaturedGame game) {
        return String.format(
                GAME_INFO_SALE_TEMPLATE,
                game.getName(), game.getId(),
                game.getFinalPrice() / 100.0, game.getCurrency(),
                game.getDiscountPercent(), game.getOriginalPrice() / 100.0,
                game.getDiscountExpiration() != null ?
                        game.getDiscountExpiration().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                        "N/A",
                game.isWindowsAvailable(), game.isMacAvailable(), game.isLinuxAvailable()
        );
    }
}

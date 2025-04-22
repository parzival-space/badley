package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;
import space.parzival.discord.badley.service.steam.SteamService;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
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

    private static final String GAME_INFO_ADVANCED_TEMPLATE = """
            %s (%s):
            - ID: %s
            - Price: %.2f (%s)
            - Meta Score: %s
            - Description: %s
            - Developers: %s
            - Publishers: %s
            - DLC Count: %d
            - Website: %s
            - System Requirements:
              - Windows: %s
              - Mac: %s
              - Linux: %s
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
                log.debug("No results found for query: {}", query);
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
            log.error("Error fetching Steam store search results: {}", e.getMessage(), e);
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
            log.error("Error fetching Steam store featured categories: {}", e.getMessage(), e);
            return "Error fetching Steam store featured categories: " + e.getMessage();
        }
    }

    @Tool(description = "Gets more details about a specific game. ID has to be requested beforehand using the search tool.")
    public String getGameDetails(
            @ToolParam(description = "The game id to get more details for.") String gameId,
            @ToolParam(description = "The result language written out. Example 'english'") String lang,
            @ToolParam(description = "Two character country code used for currency. Example: 'US'") String countryCode) {
        log.debug("AI is requesting Steam store game details: {}, {}, {}", gameId, lang, countryCode);

        try {
            StoreAppDetailsResponse response = steam.getAppDetails(gameId, lang, countryCode);

            if (!response.isSuccess()) {
                log.debug("No results found for game IDs: {}", gameId);
                return "No results found for your game IDs.";
            }

            return String.format(
                    GAME_INFO_ADVANCED_TEMPLATE,
                    response.getGame().getName(), response.getGame().getType(), response.getGame().getId(),
                    response.getGame().getPrice() != null ? response.getGame().getPrice().getFinalPrice() / 100.0 : 0,
                    response.getGame().getPrice() != null ? response.getGame().getPrice().getCurrency() : "N/A",
                    response.getGame().getMetacritic() != null ? response.getGame().getMetacritic().getScore() : "N/A",
                    response.getGame().getShortDescription(),
                    String.join(", ", response.getGame().getDevelopers()),
                    String.join(", ", response.getGame().getPublishers()),
                    response.getGame().getPackages() != null ? response.getGame().getPackages().size() : 0,
                    response.getGame().getWebsite(),
                    response.getGame().getPcRequirements() != null ? response.getGame().getPcRequirements().getMinimum() : "N/A",
                    response.getGame().getMacRequirements() != null ? response.getGame().getMacRequirements().getMinimum() : "N/A",
                    response.getGame().getLinuxRequirements() != null ? response.getGame().getLinuxRequirements().getMinimum() : "N/A",
                    response.getGame().getPlatforms() != null && response.getGame().getPlatforms().isWindows(),
                    response.getGame().getPlatforms() != null && response.getGame().getPlatforms().isMac(),
                    response.getGame().getPlatforms() != null && response.getGame().getPlatforms().isLinux()
            );
        } catch (Exception e) {
            log.error("Error fetching Steam store game details: {}", e.getMessage(), e);
            return "Error fetching Steam store game details: " + e.getMessage();
        }
    }

    @Tool(description = "Downloads the user profile from Steam.")
    public String downloadUserProfile(
            @ToolParam(description = "The 17-digit Steam ID or custom vanity ID of the user profile.") String userId
    ) {
        log.debug("AI is requesting Steam user profile: {}", userId);

        try {
            String response = steam.downloadProfile(userId);
            log.debug("Steam user profile downloaded: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error fetching Steam user profile: {}", e.getMessage(), e);
            return "Error fetching Steam user profile: " + e.getMessage();
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

package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.service.steam.SteamService;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsRequirements;
import space.parzival.discord.badley.service.steam.model.store.StoreFeaturedGame;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummariesResult;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.steam.token")
@AllArgsConstructor
public class SteamTools implements AiTools {
    private SteamService steam;

    private static final String GAME_INFO_TEMPLATE = """
            ${title} (${type}):
            - ID: ${id}
            - Price: ${price} (${currency})
            - Meta Score: ${score}
            - Platforms:
              - Windows: ${windows}
              - Mac: ${mac}
              - Linux: ${linux}
            """.stripIndent();

    private static final String GAME_INFO_ADVANCED_TEMPLATE = """
            ${title} (${type}):
            - ID: ${id}
            - Price: ${price} (${currency})
            - Meta Score: ${score}
            - Description: ${description}
            - Developers: ${developers}
            - Publishers: ${publishers}
            - DLC Count: ${dlc_count}
            - Website: ${website}
            - System Requirements:
              - Windows: ${windows_requirements}
              - Mac: ${mac_requirements}
              - Linux: ${linux_requirements}
            - Platforms:
              - Windows: ${windows}
              - Mac: ${mac}
              - Linux: ${linux}
            """.stripIndent();

    private static final String GAME_INFO_SALE_TEMPLATE = """
            ${title}:
            - ID: ${id}
            - Price: ${price} (${currency})
            - Discount: ${discount}% (was ${original_price})
            - Discount Expiration: ${discount_expiration}
            - Platforms:
              - Windows: ${windows}
              - Mac: ${mac}
              - Linux: ${linux}
            """.stripIndent();

    private static final String USER_INFO_TEMPLATE = """
            ${username}:
            - ID: ${id}
            - Real Name: ${real_name}
            - Country: ${country}
            - Profile URL: ${profile_url}
            - Avatar URL: ${avatar_url}
            - Last Logoff: ${last_logoff}
            - Creation Date: ${creation_date}
            """.stripIndent();

    private static final String FEATURED_CATEGORIES_TEMPLATE = """
            Specials:
            ${specials}
            
            Coming Soon:
            ${coming_soon}
            
            Top Sellers:
            ${top_sellers}
            
            New Releases:
            ${new_releases}
            """.stripIndent();

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

            return response.getItems().stream().map(game -> StringSubstitutor.replace(GAME_INFO_TEMPLATE, Map.of(
                        "title", game.getName(),
                        "type", game.getType(),
                        "id", game.getId(),
                        "price", game.getPrice() != null ? game.getPrice().getFinalPrice() / 100.0 : 0,
                        "currency", game.getPrice() != null ? game.getPrice().getCurrency() : "N/A",
                        "score", game.getMetaScore() != null ? game.getMetaScore() : "N/A",
                        "windows", game.getPlatforms() != null && game.getPlatforms().isWindows(),
                        "mac", game.getPlatforms() != null && game.getPlatforms().isMac(),
                        "linux", game.getPlatforms() != null && game.getPlatforms().isLinux()
                    )))
                    .collect(Collectors.joining("\n"));
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

            return StringSubstitutor.replace(FEATURED_CATEGORIES_TEMPLATE, Map.of(
                    "specials", Optional.ofNullable(response.getSpecials().getItems()).map(items -> items.stream()
                            .map(this::fillGameInfoSaleTemplate)
                            .collect(Collectors.joining("\n"))).orElse("N/A"),
                    "coming_soon", Optional.ofNullable(response.getComingSoon().getItems()).map(items -> items.stream()
                            .map(this::fillGameInfoSaleTemplate)
                            .collect(Collectors.joining("\n"))).orElse("N/A"),
                    "top_sellers", Optional.ofNullable(response.getTopSellers().getItems()).map(items -> items.stream()
                            .map(this::fillGameInfoSaleTemplate)
                            .collect(Collectors.joining("\n"))).orElse("N/A"),
                    "new_releases", Optional.ofNullable(response.getNewReleases().getItems()).map(items -> items.stream()
                            .map(this::fillGameInfoSaleTemplate)
                            .collect(Collectors.joining("\n"))).orElse("N/A")
            ));
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

            Map<String, Object> gameDetails = new HashMap<>();
            gameDetails.put("title", response.getGame().getName());
            gameDetails.put("type", response.getGame().getType());
            gameDetails.put("id", response.getGame().getId());
            gameDetails.put("price", response.getGame().getPrice() != null ? response.getGame().getPrice().getFinalPrice() / 100.0 : 0);
            gameDetails.put("currency", response.getGame().getPrice() != null ? response.getGame().getPrice().getCurrency() : "N/A");
            gameDetails.put("score", response.getGame().getMetacritic().getScore());
            gameDetails.put("description", response.getGame().getShortDescription());
            gameDetails.put("developers", String.join(", ", response.getGame().getDevelopers()));
            gameDetails.put("publishers", String.join(", ", response.getGame().getPublishers()));
            gameDetails.put("dlc_count", Optional.ofNullable(response.getGame().getPackages()).map(List::size).orElse(0));
            gameDetails.put("website", response.getGame().getWebsite());
            gameDetails.put("windows_requirements", Optional.ofNullable(response.getGame().getPcRequirements()).map(StoreAppDetailsRequirements::getMinimum).orElse("N/A"));
            gameDetails.put("mac_requirements", Optional.ofNullable(response.getGame().getMacRequirements()).map(StoreAppDetailsRequirements::getMinimum).orElse("N/A"));
            gameDetails.put("linux_requirements", Optional.ofNullable(response.getGame().getLinuxRequirements()).map(StoreAppDetailsRequirements::getMinimum).orElse("N/A"));
            gameDetails.put("windows", response.getGame().getPlatforms().isWindows());
            gameDetails.put("mac", response.getGame().getPlatforms().isMac());
            gameDetails.put("linux", response.getGame().getPlatforms().isLinux());
            return StringSubstitutor.replace(GAME_INFO_ADVANCED_TEMPLATE, gameDetails);
        } catch (Exception e) {
            log.error("Error fetching Steam store game details: {}", e.getMessage(), e);
            return "Error fetching Steam store game details: " + e.getMessage();
        }
    }

    @Tool(description = "Get a users Steam ID from their profile URL.")
    public String getUserIdFromProfileUrl(String profileUrl) {
        log.debug("AI is requesting Steam user ID from profile URL: {}", profileUrl);

        try {
            WebApiGenericResponse<WebApiResolveVanityUrlResult> response = steam.resolveProfileUrl(profileUrl);

            if (response.getResponse() == null || response.getResponse().getSteamId() == null) {
                log.debug("No results found for profile URL: {}", profileUrl);
                return "No results found for your profile URL.";
            }

            return response.getResponse().getSteamId();
        } catch (Exception e) {
            log.error("Error fetching Steam user ID from profile URL: {}", e.getMessage(), e);
            return "Error fetching Steam user ID from profile URL: " + e.getMessage();
        }
    }

    @Tool(description = "Get details about a user via their Steam ID. The ID has to be requested beforehand.")
    public String getUserDetailsFromId(String userId) {
        log.debug("AI is requesting Steam user details from ID: {}", userId);

        try {
            WebApiGenericResponse<WebApiPlayerSummariesResult> response = steam.getPlayerSummary(userId);

            if (response.getResponse() == null || response.getResponse().getPlayers() == null || response.getResponse().getPlayers().isEmpty()) {
                log.debug("No results found for user ID: {}", userId);
                return "No results found for your user ID.";
            }

            return StringSubstitutor.replace(USER_INFO_TEMPLATE, Map.of(
                    "username", response.getResponse().getPlayers().getFirst().getPersonaName(),
                    "id", response.getResponse().getPlayers().getFirst().getSteamId(),
                    "real_name", response.getResponse().getPlayers().getFirst().getRealName(),
                    "country", response.getResponse().getPlayers().getFirst().getLastCountryCode(),
                    "profile_url", response.getResponse().getPlayers().getFirst().getProfileUrl(),
                    "avatar_url", response.getResponse().getPlayers().getFirst().getAvatarUrl(),
                    "last_logoff", response.getResponse().getPlayers().getFirst().getLastLogOff().toLocalDateTime()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "creation_date", response.getResponse().getPlayers().getFirst().getTimeCreated().toLocalDateTime()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        } catch (Exception e) {
            log.error("Error fetching Steam user details from ID: {}", e.getMessage(), e);
            return "Error fetching Steam user details from ID: " + e.getMessage();
        }
    }

    private String fillGameInfoSaleTemplate(StoreFeaturedGame game) {
        return StringSubstitutor.replace(GAME_INFO_SALE_TEMPLATE, Map.of(
            "title", game.getName(),
                "id", game.getId(),
                "price", game.getFinalPrice() / 100.0,
                "currency", game.getCurrency(),
                "discount", game.getDiscountPercent(),
                "original_price", game.getOriginalPrice() / 100.0,
                "discount_expiration", game.getDiscountExpiration().toLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "windows", game.isWindowsAvailable(),
                "mac", game.isMacAvailable(),
                "linux", game.isLinuxAvailable()
        ));
    }
}

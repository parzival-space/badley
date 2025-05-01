package space.parzival.discord.badley.ai.tools.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.tools.AiTools;
import space.parzival.discord.badley.service.steam.SteamStoreService;
import space.parzival.discord.badley.service.steam.SteamWebService;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.WebApiPlayerBansResponse;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsMetacritic;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsRequirements;
import space.parzival.discord.badley.service.steam.model.store.StoreFeaturedGame;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerLevelInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummariesResult;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiRecentGamesInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.steam.enabled", havingValue = "true")
@AllArgsConstructor
public class SteamTools implements AiTools {
    private static final String GAME_INFO_TEMPLATE = """
        ${game.title} (${game.type}):
        - ID: ${game.id}
        - Price: ${game.price} (${game.currency})
        - Meta Score: ${game.score}
        - Platforms:
          - Windows: ${game.windows}
          - Mac: ${game.mac}
          - Linux: ${game.linux}
        """.stripIndent();
    private static final String GAME_INFO_ADVANCED_TEMPLATE = """
        ${advanced.title} (${advanced.type}):
        - ID: ${advanced.id}
        - Price: ${advanced.price} (${advanced.currency})
        - Meta Score: ${advanced.score}
        - Description: ${advanced.description}
        - Developers: ${advanced.developers}
        - Publishers: ${advanced.publishers}
        - DLC Count: ${advanced.dlc_count}
        - Website: ${advanced.website}
        - System Requirements:
          - Windows: ${advanced.windows_requirements}
          - Mac: ${advanced.mac_requirements}
          - Linux: ${advanced.linux_requirements}
        - Platforms:
          - Windows: ${advanced.windows}
          - Mac: ${advanced.mac}
          - Linux: ${advanced.linux}
        """.stripIndent();
    private static final String GAME_INFO_SALE_TEMPLATE = """
        ${sale.title}:
        - ID: ${sale.id}
        - Price: ${sale.price} (${sale.currency})
        - Discount: ${sale.discount}% (was ${sale.original_price})
        - Discount Expiration: ${sale.discount_expiration}
        - Platforms:
          - Windows: ${sale.windows}
          - Mac: ${sale.mac}
          - Linux: ${sale.linux}
        """.stripIndent();
    private static final String USER_INFO_TEMPLATE = """
        ${user.username}:
        - ID: ${user.id}
        - Real Name: ${user.real_name}
        - Country: ${user.country}
        - Profile URL: ${user.profile_url}
        - Avatar URL: ${user.avatar_url}
        - Last Logoff: ${user.last_logoff}
        - Creation Date: ${user.creation_date}
        """.stripIndent();
    private static final String USER_INFO_BANS_TEMPLATE = """
        Ban Statistics:
          - Community Banned: ${bans.community_banned}
          - VAC Banned: ${bans.vac_banned}
          - Number of VAC Bans: ${bans.number_of_vac_bans}
          - Days Since Last Ban: ${bans.days_since_last_ban}
          - Number of Game Bans: ${bans.number_of_game_bans}
          - Economy Ban: ${bans.economy_ban}
        """.stripIndent();
    private static final String RECENT_GAME_INFO_TEMPLATE = """
        ${recent.title}:
        - ID: ${recent.id}
        - Playtime (2 weeks): ${recent.playtime_2weeks}
        - Playtime (forever): ${recent.playtime_forever}
        - Playtime Windows (forever): ${recent.playtime_windows_forever}
        - Playtime Mac (forever): ${recent.playtime_mac_forever}
        - Playtime Linux (forever): ${recent.playtime_linux_forever}
        - Playtime Steam Deck (forever): ${recent.playtime_deck_forever}
        """.stripIndent();
    private static final String FEATURED_CATEGORIES_TEMPLATE = """
        Specials:
        ${featured.specials}
        
        Coming Soon:
        ${featured.coming_soon}
        
        Top Sellers:
        ${featured.top_sellers}
        
        New Releases:
        ${featured.new_releases}
        """.stripIndent();
    private SteamWebService steam;
    private SteamStoreService steamStore;

    @Tool(description = "Search the Steam store.")
    public String searchStore(
        @ToolParam(description = "The search Query") String query,
        @ToolParam(description = "The result language written out. Example 'english'") String lang,
        @ToolParam(description = "Two character country code used for currency. Example: 'US'") String countryCode) {
        log.debug("AI is requesting Steam store search for query: {}, {}, {}", query, lang, countryCode);

        try {
            StoreSearchResponse response = steamStore.searchStore(query, lang, countryCode);

            if (response.getTotal() == 0) {
                log.debug("No results found for query: {}", query);
                return "No results found for your query.";
            }

            return response.getItems().stream().map(game -> StringSubstitutor.replace(GAME_INFO_TEMPLATE, Map.of(
                    "game.title", game.getName(),
                    "game.type", game.getType(),
                    "game.id", game.getId(),
                    "game.price", game.getPrice() != null ? game.getPrice().getFinalPrice() / 100.0 : 0,
                    "game.currency", game.getPrice() != null ? game.getPrice().getCurrency() : "N/A",
                    "game.score", game.getMetaScore() != null ? game.getMetaScore() : "N/A",
                    "game.windows", game.getPlatforms() != null && game.getPlatforms().isWindows(),
                    "game.mac", game.getPlatforms() != null && game.getPlatforms().isMac(),
                    "game.linux", game.getPlatforms() != null && game.getPlatforms().isLinux()
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
            StoreFeaturedResponse response = steamStore.getFeaturedCategories(lang, countryCode);

            return StringSubstitutor.replace(FEATURED_CATEGORIES_TEMPLATE, Map.of(
                "featured.specials", Optional.ofNullable(response.getSpecials().getItems()).map(items -> items.stream()
                    .map(this::fillGameInfoSaleTemplate)
                    .collect(Collectors.joining("\n"))).orElse("N/A"),
                "featured.coming_soon", Optional.ofNullable(response.getComingSoon().getItems()).map(items -> items.stream()
                    .map(this::fillGameInfoSaleTemplate)
                    .collect(Collectors.joining("\n"))).orElse("N/A"),
                "featured.top_sellers", Optional.ofNullable(response.getTopSellers().getItems()).map(items -> items.stream()
                    .map(this::fillGameInfoSaleTemplate)
                    .collect(Collectors.joining("\n"))).orElse("N/A"),
                "featured.new_releases", Optional.ofNullable(response.getNewReleases().getItems()).map(items -> items.stream()
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
            StoreAppDetailsResponse response = steamStore.getAppDetails(gameId, lang, countryCode);

            if (!response.isSuccess()) {
                log.debug("No results found for game IDs: {}", gameId);
                return "No results found for your game IDs.";
            }

            Map<String, Object> gameDetails = new HashMap<>();
            gameDetails.put("advanced.title", response.getGame().getName());
            gameDetails.put("advanced.type", response.getGame().getType());
            gameDetails.put("advanced.id", response.getGame().getId());
            gameDetails.put("advanced.price",
                response.getGame().getPrice() != null ? response.getGame().getPrice().getFinalPrice() / 100.0 : 0);
            gameDetails.put("advanced.currency",
                response.getGame().getPrice() != null ? response.getGame().getPrice().getCurrency() : "N/A");
            gameDetails.put("advanced.score",
                Optional.ofNullable(response.getGame().getMetacritic())
                    .map(StoreAppDetailsMetacritic::getScore).orElse(0));
            gameDetails.put("advanced.description", response.getGame().getShortDescription());
            gameDetails.put("advanced.developers", String.join(", ", response.getGame().getDevelopers()));
            gameDetails.put("advanced.publishers", String.join(", ", response.getGame().getPublishers()));
            gameDetails.put("advanced.dlc_count",
                Optional.ofNullable(response.getGame().getPackages())
                    .map(List::size).orElse(0));
            gameDetails.put("advanced.website", response.getGame().getWebsite());
            gameDetails.put("advanced.windows_requirements",
                Optional.ofNullable(response.getGame().getPcRequirements())
                    .map(StoreAppDetailsRequirements::getMinimum).orElse("N/A"));
            gameDetails.put("advanced.mac_requirements",
                Optional.ofNullable(response.getGame().getMacRequirements())
                    .map(StoreAppDetailsRequirements::getMinimum).orElse("N/A"));
            gameDetails.put("advanced.linux_requirements",
                Optional.ofNullable(response.getGame().getLinuxRequirements())
                    .map(StoreAppDetailsRequirements::getMinimum).orElse("N/A"));
            gameDetails.put("advanced.windows", response.getGame().getPlatforms().isWindows());
            gameDetails.put("advanced.mac", response.getGame().getPlatforms().isMac());
            gameDetails.put("advanced.linux", response.getGame().getPlatforms().isLinux());
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
                "user.username", response.getResponse().getPlayers().getFirst().getPersonaName(),
                "user.id", response.getResponse().getPlayers().getFirst().getSteamId(),
                "user.real_name", Optional.ofNullable(response.getResponse().getPlayers().getFirst().getRealName()).orElse(""),
                "user.country", Optional.ofNullable(response.getResponse().getPlayers().getFirst().getLastCountryCode()).orElse(""),
                "user.profile_url", response.getResponse().getPlayers().getFirst().getProfileUrl(),
                "user.avatar_url", response.getResponse().getPlayers().getFirst().getAvatarUrl(),
                "user.last_logoff", Optional.ofNullable(response.getResponse().getPlayers().getFirst().getLastLogOff()).map(u -> u.toLocalDateTime()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).orElse("N/A"),
                "user.creation_date", Optional.ofNullable(response.getResponse().getPlayers().getFirst().getTimeCreated()).map(u -> u.toLocalDateTime()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).orElse("N/A")
            ));
        } catch (Exception e) {
            log.error("Error fetching Steam user details from ID: {}", e.getMessage(), e);
            return "Error fetching Steam user details from ID: " + e.getMessage();
        }
    }

    @Tool(description = "Get details about a user via their Steam ID. The ID has to be requested beforehand.")
    public String getUserBanDetails(String userId) {
        log.debug("AI is requesting Steam user ban details from ID: {}", userId);

        try {
            WebApiPlayerBansResponse response = steam.getPlayerBans(userId);

            if (response == null || response.getPlayers() == null || response.getPlayers().isEmpty()) {
                log.debug("No ban details found for user ID: {}", userId);
                return "No ban details found for your user ID.";
            }

            return StringSubstitutor.replace(USER_INFO_BANS_TEMPLATE, Map.of(
                "bans.community_banned", response.getPlayers().getFirst().isCommunityBanned(),
                "bans.vac_banned", response.getPlayers().getFirst().isVacBanned(),
                "bans.number_of_vac_bans", response.getPlayers().getFirst().getNumberOfVacBans(),
                "bans.days_since_last_ban", response.getPlayers().getFirst().getDaysSinceLastBan(),
                "bans.number_of_game_bans", response.getPlayers().getFirst().getNumberOfGameBans(),
                "bans.economy_ban", response.getPlayers().getFirst().getEconomyBan()
            ));
        } catch (Exception e) {
            log.error("Error fetching Steam user ban details from ID: {}", e.getMessage(), e);
            return "Error fetching Steam user ban details from ID: " + e.getMessage();
        }
    }

    @Tool(description = "Get a users recent games via their Steam ID. The ID has to be requested beforehand.")
    public String getUserRecentGames(String userId) {
        log.debug("AI is requesting Steam user recent games from ID: {}", userId);

        try {
            WebApiGenericResponse<WebApiRecentGamesInfo> response = steam.getRecentPlayedGames(userId);

            if (response == null || response.getResponse() == null || response.getResponse().getGames() == null) {
                log.debug("No recent games found for user ID: {}", userId);
                return "No recent games found for your user ID.";
            }

            return response.getResponse().getGames().stream()
                .map(game -> StringSubstitutor.replace(RECENT_GAME_INFO_TEMPLATE, Map.of(
                    "recent.title", game.getName(),
                    "recent.id", game.getId(),
                    "recent.playtime_2weeks", game.getPlaytime2Weeks() / 60,
                    "recent.playtime_forever", game.getPlaytimeForever() / 60,
                    "recent.playtime_windows_forever", game.getPlaytimeWindowsForever() / 60,
                    "recent.playtime_mac_forever", game.getPlaytimeMacForever() / 60,
                    "recent.playtime_linux_forever", game.getPlaytimeLinuxForever() / 60,
                    "recent.playtime_deck_forever", game.getPlaytimeDeckForever() / 60
                ))).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Error fetching Steam user recent games from ID: {}", e.getMessage(), e);
            return "Error fetching Steam user recent games from ID: " + e.getMessage();
        }
    }

    @Tool(description = "Get a users Steam level via their Steam ID. The ID has to be requested beforehand.")
    public String getUserLevel(String userId) {
        log.debug("AI is requesting Steam user level from ID: {}", userId);

        try {
            WebApiGenericResponse<WebApiPlayerLevelInfo> levelResponse = steam.getPlayerLevel(userId);

            if (levelResponse == null || levelResponse.getResponse() == null) {
                log.debug("No level details found for user ID: {}", userId);
                return "No level details found for your user ID.";
            }

            return String.format("User Level: %d", levelResponse.getResponse().getLevel());
        } catch (Exception e) {
            log.error("Error fetching Steam user level from ID: {}", e.getMessage(), e);
            return "Error fetching Steam user level from ID: " + e.getMessage();
        }
    }

    private String fillGameInfoSaleTemplate(StoreFeaturedGame game) {
        return StringSubstitutor.replace(GAME_INFO_SALE_TEMPLATE, Map.of(
            "sale.title", game.getName(),
            "sale.id", game.getId(),
            "sale.price", game.getFinalPrice() / 100.0,
            "sale.currency", game.getCurrency(),
            "sale.discount", game.getDiscountPercent(),
            "sale.original_price", game.getOriginalPrice() / 100.0,
            "sale.discount_expiration", Optional.ofNullable(game.getDiscountExpiration()).map(expirationTime -> expirationTime.toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).orElse("N/A"),
            "sale.windows", game.isWindowsAvailable(),
            "sale.mac", game.isMacAvailable(),
            "sale.linux", game.isLinuxAvailable()
        ));
    }
}

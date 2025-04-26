package space.parzival.discord.badley.service.steam;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.configuration.properties.SteamProperties;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.WebApiPlayerBansResponse;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerLevelInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummariesResult;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiRecentGamesInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Service uses multiple public and undocumented APIs from Valve.
 * The undocumented APIs have been documented by the community and are available
 * on the <a href="https://github.com/Revadike/InternalSteamWebAPI/wiki">Revadlike/InternalSteamAPI</a> GitHub page.
 * Additional documentations can be found on the <a href="https://steamapi.xpaw.me">Steam Web API Documentation</a>
 * by xPaw.
 */
@Service
@ConditionalOnProperty(value = "badley.ai.tools.steam.token")
public class SteamWebService {
    private static final Pattern PROFILE_ID_PATTERN =
        Pattern.compile("https?://steamcommunity\\.com/(?:id/([\\w-]+)|profiles/(\\d+))/?");

    private final RestTemplate apiRestTemplate;
    private final SteamProperties properties;

    public SteamWebService(RestTemplateBuilder restTemplateBuilder, SteamProperties properties) {
        apiRestTemplate = restTemplateBuilder
            .rootUri("https://api.steampowered.com")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .build();

        this.properties = properties;
    }

    /**
     * Resolves a Steam profile URL to a Steam ID.
     *
     * @param profileUrl The profile URL to resolve.
     */
    public WebApiGenericResponse<WebApiResolveVanityUrlResult> resolveProfileUrl(String profileUrl) {
        Matcher matcher = PROFILE_ID_PATTERN.matcher(profileUrl);
        if (!matcher.find())
            throw new IllegalArgumentException("Invalid profile URL: " + profileUrl);

        if (matcher.group(1) != null) {
            UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
                .path("/ISteamUser/ResolveVanityURL/v1/")
                .queryParam("key", properties.getToken())
                .queryParam("vanityurl", matcher.group(1))
                .build();

            ParameterizedTypeReference<WebApiGenericResponse<WebApiResolveVanityUrlResult>> responseType =
                new ParameterizedTypeReference<>() {
                };

            return apiRestTemplate.exchange(
                appDetailsUri.toUriString(),
                HttpMethod.GET,
                null,
                responseType
            ).getBody();
        }

        return WebApiGenericResponse.<WebApiResolveVanityUrlResult>builder()
            .response(WebApiResolveVanityUrlResult.builder()
                .steamId(matcher.group(2))
                .success(1)
                .build())
            .build();
    }

    /**
     * Retrieves the player summary for a given Steam ID.
     *
     * @param userId The Steam ID to retrieve the summary for.
     */
    public WebApiGenericResponse<WebApiPlayerSummariesResult> getPlayerSummary(String userId) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
            .path("/ISteamUser/GetPlayerSummaries/v2/")
            .queryParam("key", properties.getToken())
            .queryParam("steamids", userId)
            .build();

        ParameterizedTypeReference<WebApiGenericResponse<WebApiPlayerSummariesResult>> responseType =
            new ParameterizedTypeReference<>() {
            };

        return apiRestTemplate.exchange(
            appDetailsUri.toUriString(),
            HttpMethod.GET,
            null,
            responseType
        ).getBody();
    }

    /**
     * Retrieves the player bans for a given Steam ID.
     *
     * @param userId The Steam ID to retrieve the bans for.
     */
    public WebApiPlayerBansResponse getPlayerBans(String userId) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
            .path("/ISteamUser/GetPlayerBans/v1/")
            .queryParam("key", properties.getToken())
            .queryParam("steamids", userId)
            .build();

        return apiRestTemplate.getForObject(appDetailsUri.toUriString(), WebApiPlayerBansResponse.class);
    }

    /**
     * Retrieves the player level for a given Steam ID.
     *
     * @param userId The Steam ID to retrieve the level for.
     */
    public WebApiGenericResponse<WebApiPlayerLevelInfo> getPlayerLevel(String userId) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
            .path("/IPlayerService/GetSteamLevel/v1/")
            .queryParam("key", properties.getToken())
            .queryParam("steamid", userId)
            .build();

        ParameterizedTypeReference<WebApiGenericResponse<WebApiPlayerLevelInfo>> responseType =
            new ParameterizedTypeReference<>() {};

        return apiRestTemplate.exchange(
            appDetailsUri.toUriString(),
            HttpMethod.GET,
            null,
            responseType
        ).getBody();
    }

    /**
     * Retrieves the recently played games for a given Steam ID.
     *
     * @param userId The Steam ID to retrieve the recently played games for.
     */
    public WebApiGenericResponse<WebApiRecentGamesInfo> getRecentPlayedGames(String userId) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
            .path("/IPlayerService/GetRecentlyPlayedGames/v1/")
            .queryParam("key", properties.getToken())
            .queryParam("steamid", userId)
            .build();

        ParameterizedTypeReference<WebApiGenericResponse<WebApiRecentGamesInfo>> responseType =
            new ParameterizedTypeReference<>() {};

        return apiRestTemplate.exchange(
            appDetailsUri.toUriString(),
            HttpMethod.GET,
            null,
            responseType
        ).getBody();
    }
}

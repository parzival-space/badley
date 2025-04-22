package space.parzival.discord.badley.service.steam;

import jakarta.annotation.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.configuration.properties.SteamProperties;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummariesResult;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Service uses multiple public and undocumented APIs from Valve.
 * The undocumented APIs have been documented by the community and are available
 * on the <a href="https://github.com/Revadike/InternalSteamWebAPI/wiki">Revadlike/InternalSteamAPI</a> GitHub page.
 * Additional documentations can be found on the <a href="https://steamapi.xpaw.me">Stean Web API Documentation</a>
 * by xPaw.
 */
@Service
@ConditionalOnProperty(value = "badley.ai.tools.steam.token")
public class SteamService {
    private static final String FALLBACK_LANGUAGE = "english";
    private static final String FALLBACK_COUNTRY_CODE = "US";

    private static final Pattern PROFILE_ID_PATTERN =
            Pattern.compile("https?://steamcommunity\\.com/(?:id/([\\w-]+)|profiles/(\\d+))/?");

    private final RestTemplate storeRestTemplate;
    private final RestTemplate webApiRestTemplate;
    private final SteamProperties properties;

    public SteamService(RestTemplateBuilder restTemplateBuilder, SteamProperties properties) {
        storeRestTemplate = restTemplateBuilder
                .rootUri("https://store.steampowered.com/api")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

        webApiRestTemplate = restTemplateBuilder
                .rootUri("https://api.steampowered.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

        this.properties = properties;
    }

    /**
     * Searches the Steam store for games.
     *
     * @param query       The search query to use.
     * @param language    The language to use for the search. If null, "english" will be used.
     * @param countryCode The country code to use for the search. If null, "US" will be used.
     */
    public StoreSearchResponse searchStore(String query, @Nullable String language, @Nullable String countryCode) {
        UriComponents searchUri = UriComponentsBuilder.newInstance()
                .path("/storesearch")
                .queryParam("term", query)
                .queryParam("l", Optional.ofNullable(language).orElse(FALLBACK_LANGUAGE))
                .queryParam("cc", Optional.ofNullable(countryCode).orElse(FALLBACK_COUNTRY_CODE))
                .build();

        return storeRestTemplate.getForObject(searchUri.toUriString(), StoreSearchResponse.class);
    }

    /**
     * Fetches the featured categories from the Steam store.
     *
     * @param language The language to use for the request. If null, "english" will be used.
     * @param countryCode The country code to use for the request. If null, "US" will be used.
     */
    public StoreFeaturedResponse getFeaturedCategories(@Nullable String language, @Nullable String countryCode) {
        UriComponents featuredUri = UriComponentsBuilder.newInstance()
                .path("/featuredcategories")
                .queryParam("l", Optional.ofNullable(language).orElse(FALLBACK_LANGUAGE))
                .queryParam("cc", Optional.ofNullable(countryCode).orElse(FALLBACK_COUNTRY_CODE))
                .build();

        return storeRestTemplate.getForObject(featuredUri.toUriString(), StoreFeaturedResponse.class);
    }

    /**
     * Retrieves the details of specific apps from the Steam store.
     *
     * @param appId The list of app IDs to retrieve details for.
     * @param language The language to use for the request. If null, "english" will be used.
     * @param countryCode The country code to use for the request. If null, "US" will be used.
     */
    public StoreAppDetailsResponse getAppDetails(String appId, @Nullable String language, @Nullable String countryCode) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
                .path("/appdetails")
                .queryParam("appids", appId)
                .queryParam("l", Optional.ofNullable(language).orElse(FALLBACK_LANGUAGE))
                .queryParam("cc", Optional.ofNullable(countryCode).orElse(FALLBACK_COUNTRY_CODE))
                .build();

        ResponseEntity<Map<String, StoreAppDetailsResponse>> response = storeRestTemplate.exchange(
                appDetailsUri.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() == null)
            return StoreAppDetailsResponse.builder()
                    .success(false)
                    .game(null)
                    .build();

        return response.getBody().getOrDefault(
                appId,
                StoreAppDetailsResponse.builder()
                        .success(false)
                        .game(null)
                        .build()
        );
    }

    /**
     * Resolves a Steam profile URL to a Steam ID.
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
                    new ParameterizedTypeReference<>() {};

            return webApiRestTemplate.exchange(
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
     * @param userId The Steam ID to retrieve the summary for.
     */
    public WebApiGenericResponse<WebApiPlayerSummariesResult> getPlayerSummary(String userId) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
                .path("/ISteamUser/GetPlayerSummaries/v2/")
                .queryParam("key", properties.getToken())
                .queryParam("steamids", userId)
                .build();

        ParameterizedTypeReference<WebApiGenericResponse<WebApiPlayerSummariesResult>> responseType =
                new ParameterizedTypeReference<>() {};

        return webApiRestTemplate.exchange(
                appDetailsUri.toUriString(),
                HttpMethod.GET,
                null,
                responseType
        ).getBody();
    }
}

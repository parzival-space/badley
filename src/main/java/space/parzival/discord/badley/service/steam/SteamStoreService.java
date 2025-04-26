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
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;

import java.util.Map;
import java.util.Optional;

/**
 * This Service uses multiple public and undocumented APIs from Valve.
 * The undocumented APIs have been documented by the community and are available
 * on the <a href="https://github.com/Revadike/InternalSteamWebAPI/wiki">Revadlike/InternalSteamAPI</a> GitHub page.
 * Additional documentations can be found on the <a href="https://steamapi.xpaw.me">Stean Web API Documentation</a>
 * by xPaw.
 */
@Service
@ConditionalOnProperty(value = "badley.ai.tools.steam.token")
public class SteamStoreService {
    private static final String FALLBACK_LANGUAGE = "english";
    private static final String FALLBACK_COUNTRY_CODE = "US";

    private final RestTemplate apiRestTemplate;

    public SteamStoreService(RestTemplateBuilder restTemplateBuilder) {
        apiRestTemplate = restTemplateBuilder
            .rootUri("https://store.steampowered.com/api")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .build();
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

        return apiRestTemplate.getForObject(searchUri.toUriString(), StoreSearchResponse.class);
    }

    /**
     * Fetches the featured categories from the Steam store.
     *
     * @param language    The language to use for the request. If null, "english" will be used.
     * @param countryCode The country code to use for the request. If null, "US" will be used.
     */
    public StoreFeaturedResponse getFeaturedCategories(@Nullable String language, @Nullable String countryCode) {
        UriComponents featuredUri = UriComponentsBuilder.newInstance()
            .path("/featuredcategories")
            .queryParam("l", Optional.ofNullable(language).orElse(FALLBACK_LANGUAGE))
            .queryParam("cc", Optional.ofNullable(countryCode).orElse(FALLBACK_COUNTRY_CODE))
            .build();

        return apiRestTemplate.getForObject(featuredUri.toUriString(), StoreFeaturedResponse.class);
    }

    /**
     * Retrieves the details of specific apps from the Steam store.
     *
     * @param appId       The list of app IDs to retrieve details for.
     * @param language    The language to use for the request. If null, "english" will be used.
     * @param countryCode The country code to use for the request. If null, "US" will be used.
     */
    public StoreAppDetailsResponse getAppDetails(String appId, @Nullable String language, @Nullable String countryCode) {
        UriComponents appDetailsUri = UriComponentsBuilder.newInstance()
            .path("/appdetails")
            .queryParam("appids", appId)
            .queryParam("l", Optional.ofNullable(language).orElse(FALLBACK_LANGUAGE))
            .queryParam("cc", Optional.ofNullable(countryCode).orElse(FALLBACK_COUNTRY_CODE))
            .build();

        ResponseEntity<Map<String, StoreAppDetailsResponse>> response = apiRestTemplate.exchange(
            appDetailsUri.toUriString(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            }
        );

        if (response.getBody() == null)
            return StoreAppDetailsResponse.builder()
                .success(false)
                .game(null)
                .build();

        return response.getBody().getOrDefault( // NOSONAR - checked above
            appId,
            StoreAppDetailsResponse.builder()
                .success(false)
                .game(null)
                .build()
        );
    }
}

package space.parzival.discord.badley.service.google;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.configuration.properties.GoogleProperties;
import space.parzival.discord.badley.service.google.model.GoogleQueryResponse;

import java.util.Optional;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.google.token")
public class GoogleService {
    private final RestTemplate apiRestTemplate;
    private final GoogleProperties properties;

    public GoogleService(RestTemplateBuilder restTemplateBuilder, GoogleProperties googleProperties) {
        this.apiRestTemplate = restTemplateBuilder
                .rootUri("https://www.googleapis.com")
                .build();
        this.properties = googleProperties;
    }

    /**
     * Queries the Google Programmable Search API for a web search with the given parameters.
     *
     * @param query    The search query to perform.
     * @param country  The country code to use for the search. (Two lowercase-letter ISO 3166-1 alpha-2 code)
     * @param language The language code to use for the search. (Two-letter ISO 639-1 code)
     * @param count    The number of results to return. (Less or equal to 10)
     * @param offset   The number of results to skip. (Less or equal to 10)
     * @return The response containing the search results.
     */
    public GoogleQueryResponse query(String query, String country, String language, int count, int offset) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
                .path("/customsearch/v1")
                .queryParam("key", properties.getToken())
                .queryParam("cx", properties.getEngineId())
                .queryParam("q", query)
                .queryParamIfPresent("gl", Optional.ofNullable(country != null ? country.toLowerCase() : null))
                .queryParamIfPresent("lr", Optional.ofNullable(language != null ? "lang_" + language.toLowerCase() : null))
                .queryParam("num", count)
                .queryParam("start", offset)
                .queryParam("safesearch", "off")
                .build();

        return apiRestTemplate.getForObject(apiUri.toUriString(), GoogleQueryResponse.class);
    }

    /**
     * Queries the Google Programmable Search API for a web search with the given parameters.
     *
     * @param query The search query to perform.
     * @return The response containing the search results.
     */
    public GoogleQueryResponse query(String query) {
        return query(query, null, null, 10, 1);
    }
}

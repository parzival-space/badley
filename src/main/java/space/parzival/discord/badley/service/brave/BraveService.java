package space.parzival.discord.badley.service.brave;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.configuration.properties.tools.BraveProperties;
import space.parzival.discord.badley.service.brave.model.BraveQueryResponse;

import java.util.List;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.brave.token")
public class BraveService {
    private final RestTemplate apiRestTemplate;

    public BraveService(RestTemplateBuilder restTemplateBuilder, BraveProperties braveProperties) {
        this.apiRestTemplate = restTemplateBuilder
            .rootUri("https://api.search.brave.com")
            .defaultHeader("x-subscription-token", braveProperties.getToken())
            .build();
    }

    /**
     * Queries the Brave Search API for a web search with the given parameters.
     *
     * @param query    The search query to perform.
     * @param country  The country code to use for the search. (Two uppercase-letter ISO 3166-1 alpha-2 code)
     * @param language The language code to use for the search. (Two-letter ISO 639-1 code)
     * @param count    The number of results to return. (Less or equal to 50)
     * @param offset   The number of results to skip. (Less or equal to 9)
     * @return The response containing the search results.
     * @apiNote This API has a rate limit of 2000 requests per month on the free plan.
     */
    public BraveQueryResponse query(String query, String country, String language, int count, int offset) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/res/v1/web/search")
            .queryParam("q", query)
            .queryParam("country", country)
            .queryParam("search_lang", language)
            .queryParam("count", count)
            .queryParam("offset", offset)
            //.queryParam("freshness", "pw") // pd, pw, pm
            .queryParam("summary", false)
            .queryParam("safesearch", "off")
            .queryParam("result_filter", "web")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return apiRestTemplate.exchange(
            apiUri.toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            BraveQueryResponse.class
        ).getBody();
    }

    /**
     * Queries the Brave Search API for a web search with the given parameters.
     *
     * @param query The search query to perform.
     * @return The response containing the search results.
     * @apiNote This API has a rate limit of 2000 requests per month on the free plan.
     */
    public BraveQueryResponse query(String query) {
        return query(query, "US", "en", 10, 0);
    }
}

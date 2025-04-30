package space.parzival.discord.badley.service.wikipedia;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.service.wikipedia.model.WikiParsePageResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryPagesResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryResponse;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.wikipedia.enabled", havingValue = "true")
public class WikipediaService {
    private final RestTemplate apiRestTemplate;

    public WikipediaService(RestTemplateBuilder restTemplateBuilder) {
        this.apiRestTemplate = restTemplateBuilder
            .rootUri("https://en.wikipedia.org/w/api.php")
            .defaultHeader("Accept", "application/json")
            .build();
    }

    /**
     * Queries the Wikipedia API for a page or multiple pages with the given title.
     *
     * @param pageTitle The title of the page to query.
     * @return The response containing the page information as well as the extracted html.
     */
    public WikiQueryPagesResponse queryForPages(String pageTitle) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/")
            .queryParam("action", "query")
            .queryParam("format", "json")
            .queryParam("titles", pageTitle)
            .queryParam("prop", "extracts|info")
            .build();

        WikiQueryResponse response = apiRestTemplate.getForObject(apiUri.toUriString(), WikiQueryResponse.class);
        if (response == null)
            throw new IllegalArgumentException("Could not parse response from Wikipedia API");

        return response.getQueryResult();
    }

    /**
     * Queries the Wikipedia API for a random page.
     *
     * @return The response containing the page information as well as the extracted html.
     */
    public WikiQueryPagesResponse getRandomPage() {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/")
            .queryParam("action", "query")
            .queryParam("generator", "random")
            .queryParam("grnnamespace", 0)
            .queryParam("grnlimit", 1)
            .queryParam("prop", "extracts|info")
            .queryParam("format", "json")
            .build();

        WikiQueryResponse response = apiRestTemplate.getForObject(apiUri.toUriString(), WikiQueryResponse.class);
        if (response == null)
            throw new IllegalArgumentException("Could not parse response from Wikipedia API");

        return response.getQueryResult();
    }

    public WikiParsePageResponse parsePage(String pageTitle) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/")
            .queryParam("action", "parse")
            .queryParam("format", "json")
            .queryParam("page", pageTitle)
            .queryParam("prop", "text")
            .queryParam("formatversion", 2)
            .build();

        return apiRestTemplate.getForObject(apiUri.toUriString(), WikiParsePageResponse.class);
    }
}

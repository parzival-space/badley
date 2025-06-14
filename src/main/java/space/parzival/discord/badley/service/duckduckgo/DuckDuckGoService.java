package space.parzival.discord.badley.service.duckduckgo;

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
import space.parzival.discord.badley.service.duckduckgo.model.DuckDuckGoInstantAnswersResponse;

import java.util.List;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.duckduckgo.enabled", havingValue = "true")
public class DuckDuckGoService {
    private final RestTemplate apiRestTemplate;

    public DuckDuckGoService(RestTemplateBuilder restTemplateBuilder) {
        this.apiRestTemplate = restTemplateBuilder
            .rootUri("https://api.duckduckgo.com")
            .build();
    }

    public DuckDuckGoInstantAnswersResponse queryInstantAnswers(String query) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/")
            .queryParam("format", "json")
            .queryParam("no_html", "1")
            .queryParam("no_redirect", "1")
            .queryParam("q", query)
            .queryParam("skip_disambig", "1")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return apiRestTemplate.exchange(
            apiUri.toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            DuckDuckGoInstantAnswersResponse.class
        ).getBody();
    }
}

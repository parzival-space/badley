package space.parzival.discord.badley.service.numberapi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.service.numberapi.model.NumberApiResponse;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.number-api.enabled", havingValue = "true")
public class NumberApiService {
    private final RestTemplate restTemplate;

    public NumberApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .rootUri("http://numbersapi.com") // NOSONAR - this api is not https
            .defaultHeader("Accept", "application/json")
            .build();
    }

    /**
     * Requests trivia about the given number.
     *
     * @param number The number to request trivia about.
     * @return A NumberApiResponse containing trivia about the number.
     */
    public NumberApiResponse getTrivia(int number) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/" + number + "/trivia")
            .queryParam("json")
            .build();

        return restTemplate.getForObject(apiUri.toUriString(), NumberApiResponse.class);
    }

    /**
     * Requests math facts about the given number.
     *
     * @param number The number to request math facts about.
     * @return A NumberApiResponse containing math facts about the number.
     */
    public NumberApiResponse getMath(int number) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/" + number + "/math")
            .queryParam("json")
            .build();

        return restTemplate.getForObject(apiUri.toUriString(), NumberApiResponse.class);
    }

    /**
     * Requests date information about the given number.
     * @param number The number to request date information about.
     * @return A NumberApiResponse containing date information about the number.
     */
    public NumberApiResponse getDate(int number) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/" + number + "/date")
            .queryParam("json")
            .build();

        return restTemplate.getForObject(apiUri.toUriString(), NumberApiResponse.class);
    }

    /**
     * Requests year information about the given number.
     *
     * @param number The number to request year information about.
     * @return A NumberApiResponse containing year information about the number.
     */
    public NumberApiResponse getYear(int number) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/" + number + "/year")
            .queryParam("json")
            .build();

        return restTemplate.getForObject(apiUri.toUriString(), NumberApiResponse.class);
    }
}

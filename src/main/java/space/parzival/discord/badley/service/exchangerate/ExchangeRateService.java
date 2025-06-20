package space.parzival.discord.badley.service.exchangerate;

import jakarta.transaction.NotSupportedException;
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
import space.parzival.discord.badley.configuration.properties.tools.ExchangeRateApiProperties;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateSupportedCodesResponse;

import java.util.List;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.exchange-rate-api.enabled", havingValue = "true")
public class ExchangeRateService {
    private RestTemplate authRestTemplate;
    private ExchangeRateApiProperties exchangeRateApiProperties;

    public ExchangeRateService(RestTemplateBuilder restTemplateBuilder, ExchangeRateApiProperties properties) {
        this.authRestTemplate = restTemplateBuilder
            .rootUri("https://open.er-api.com/v6/" + properties.getToken() + "/")
            .defaultHeader("Content-Type", "application/json")
            .build();

        this.exchangeRateApiProperties = properties;
    }

    /**
     * Gets the list of supported currencies as well as their ISO code from the ExchangeRate API.
     * @return The response containing the supported currency codes.
     * @throws NotSupportedException If the API key is not provided, an exception is thrown.
     */
    public ExchangeRateSupportedCodesResponse getSupportedCodes() throws NotSupportedException {
        if (this.exchangeRateApiProperties.getToken() == null)
            throw new NotSupportedException("Requesting supported currency codes requires a API key. You did not provide one!");

        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .path("/codes")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return authRestTemplate.exchange(
            apiUri.toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ExchangeRateSupportedCodesResponse.class
        ).getBody();
    }

}

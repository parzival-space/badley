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
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateRatesResponse;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateSupportedCodesResponse;

import java.util.List;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.exchange-rate-api.enabled", havingValue = "true")
public class ExchangeRateService {
    private RestTemplate restTemplate;
    private ExchangeRateApiProperties exchangeRateApiProperties;

    public ExchangeRateService(RestTemplateBuilder restTemplateBuilder, ExchangeRateApiProperties properties) {
        this.restTemplate = restTemplateBuilder
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
            throw new NotSupportedException("Requesting supported currency codes requires an API key. You did not provide one!");

        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .scheme("https")
            .host("v6.exchangerate-api.com")
            .path("/v6/" + this.exchangeRateApiProperties.getToken() + "/codes")
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return restTemplate.exchange(
            apiUri.toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ExchangeRateSupportedCodesResponse.class
        ).getBody();
    }

    /**
     * Gets the latest exchange rates for a given base currency code.
     * @param baseCode The base currency code (e.g., "USD", "EUR").
     * @return The response containing the exchange rates for the specified base currency.
     */
    public ExchangeRateRatesResponse getRates(String baseCode) {
        UriComponents apiUri = UriComponentsBuilder.newInstance()
            .scheme("https")
            .host(
                this.exchangeRateApiProperties.getToken() != null
                    ? "v6.exchangerate-api.com"
                    : "open.er-api.com"
            )
            .path(
                this.exchangeRateApiProperties.getToken() != null
                    ? "/v6/" + this.exchangeRateApiProperties.getToken() + "/latest/" + baseCode
                    : "/v6/latest/" + baseCode
            )
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return restTemplate.exchange(
            apiUri.toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ExchangeRateRatesResponse.class
        ).getBody();
    }
}

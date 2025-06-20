package space.parzival.discord.badley.service.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Map;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = ExchangeRateRatesResponse.ExchangeRateRatesResponseBuilder.class)
public class ExchangeRateRatesResponse {
    @JsonProperty("result")
    String result;

    @JsonProperty("base_code")
    String baseCode;

    @JsonProperty("conversion_rates")
    @JsonAlias("rates") // the free api uses "rates" instead of "conversion_rates"
    Map<String, Float> conversionRates;
}

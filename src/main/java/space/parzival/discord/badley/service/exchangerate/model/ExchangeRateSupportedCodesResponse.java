package space.parzival.discord.badley.service.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = ExchangeRateSupportedCodesResponse.SupportedCodesResponseBuilder.class)
public class ExchangeRateSupportedCodesResponse {
    @JsonProperty("result")
    String result;

    @JsonProperty("supported_codes")
    List<String[]> supportedCodes;
}

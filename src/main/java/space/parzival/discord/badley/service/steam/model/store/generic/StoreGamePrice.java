package space.parzival.discord.badley.service.steam.model.store.generic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreGamePrice.StoreGamePriceBuilder.class)
public class StoreGamePrice {
    @JsonProperty("currency")
    String currency;

    @JsonProperty("initial")
    int initialPrice;

    @JsonProperty("final")
    int finalPrice;
}

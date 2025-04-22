package space.parzival.discord.badley.service.steam.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreSearchPrice.StoreSearchPriceBuilder.class)
public class StoreSearchPrice {
    @JsonProperty("currency")
    String currency;

    @JsonProperty("initial")
    int initialPrice;

    @JsonProperty("final")
    int finalPrice;
}

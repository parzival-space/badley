package space.parzival.discord.badley.service.steam.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreFeaturedGame.StoreFeaturedGameBuilder.class)
public class StoreFeaturedGame {
    @JsonProperty("id")
    int id;

    @JsonProperty("type")
    int type;

    @JsonProperty("name")
    String name;

    @JsonProperty("discounted")
    boolean discounted;

    @JsonProperty("discount_percent")
    int discountPercent;

    @JsonProperty("original_price")
    int originalPrice;

    @JsonProperty("final_price")
    int finalPrice;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("windows_available")
    boolean windowsAvailable;

    @JsonProperty("mac_available")
    boolean macAvailable;

    @JsonProperty("linux_available")
    boolean linuxAvailable;

    @JsonProperty("discount_expiration")
    OffsetDateTime discountExpiration;

    @JsonProperty("controller_support")
    String controllerSupport;
}

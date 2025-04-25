package space.parzival.discord.badley.service.steam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsGame;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreAppDetailsResponse.StoreAppDetailsResponseBuilder.class)
public class StoreAppDetailsResponse {
    @JsonProperty("success")
    boolean success;

    @JsonProperty("data")
    StoreAppDetailsGame game;
}

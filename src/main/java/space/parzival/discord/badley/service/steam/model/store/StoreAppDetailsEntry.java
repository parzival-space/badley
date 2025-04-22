package space.parzival.discord.badley.service.steam.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreAppDetailsEntry.StoreAppDetailsEntryBuilder.class)
public class StoreAppDetailsEntry {
    @JsonProperty("success")
    boolean success;

    @JsonProperty("data")
    StoreAppDetailsGame game;
}

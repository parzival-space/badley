package space.parzival.discord.badley.service.steam.model.store;

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
@JsonDeserialize(builder = StoreAppDetailsMetacritic.StoreAppDetailsMetacriticBuilder.class)
public class StoreAppDetailsMetacritic {
    @JsonProperty("score")
    int score;

    @JsonProperty("url")
    String url;
}

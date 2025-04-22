package space.parzival.discord.badley.service.steam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import space.parzival.discord.badley.service.steam.model.store.StoreSearchGame;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreSearchResponse.StoreSearchResponseBuilder.class)
public class StoreSearchResponse {
    @JsonProperty("total")
    int total;

    @JsonProperty("items")
    List<StoreSearchGame> items;
}

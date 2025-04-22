package space.parzival.discord.badley.service.steam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import space.parzival.discord.badley.service.steam.model.store.StoreFeaturedContainer;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreFeaturedResponse.StoreFeaturedResponseBuilder.class)
public class StoreFeaturedResponse {
    @JsonProperty("status")
    int status;

    @JsonProperty("specials")
    StoreFeaturedContainer specials;

    @JsonProperty("coming_soon")
    StoreFeaturedContainer comingSoon;

    @JsonProperty("top_sellers")
    StoreFeaturedContainer topSellers;

    @JsonProperty("new_releases")
    StoreFeaturedContainer newReleases;
}

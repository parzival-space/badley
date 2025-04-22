package space.parzival.discord.badley.service.steam.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import space.parzival.discord.badley.service.steam.model.generic.StoreGamePlatforms;
import space.parzival.discord.badley.service.steam.model.generic.StoreGamePrice;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreSearchGame.StoreSearchGameBuilder.class)
public class StoreSearchGame {
    @JsonProperty("type")
    String type;

    @JsonProperty("name")
    String name;

    @JsonProperty("id")
    int id;

    @JsonProperty("price")
    StoreGamePrice price;

    @JsonProperty("tiny_image")
    String tinyImage;

    @JsonProperty("metascore")
    String metaScore;

    @JsonProperty("platforms")
    StoreGamePlatforms platforms;

    @JsonProperty("streamingvideo")
    boolean streamingVideo;

    @JsonProperty("controller_support")
    String controllerSupport;
}

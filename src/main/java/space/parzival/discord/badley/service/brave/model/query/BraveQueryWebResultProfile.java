package space.parzival.discord.badley.service.brave.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = BraveQueryWebResultProfile.BraveQueryWebResultProfileBuilder.class)
public class BraveQueryWebResultProfile {
    @JsonProperty("name")
    String name;

    @JsonProperty("url")
    String url;

    @JsonProperty("long_name")
    String domain;

    @JsonProperty("img")
    String imgUrl;
}

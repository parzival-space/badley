package space.parzival.discord.badley.service.steam.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreSearchPlatforms.StoreSearchPlatformsBuilder.class)
public class StoreSearchPlatforms {
    @JsonProperty("windows")
    boolean windows;

    @JsonProperty("mac")
    boolean mac;

    @JsonProperty("linux")
    boolean linux;
}

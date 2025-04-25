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
@JsonDeserialize(builder = StoreAppDetailsSupportInfo.StoreAppDetailsSupportInfoBuilder.class)
public class StoreAppDetailsSupportInfo {
    @JsonProperty("url")
    String url;

    @JsonProperty("email")
    String email;
}

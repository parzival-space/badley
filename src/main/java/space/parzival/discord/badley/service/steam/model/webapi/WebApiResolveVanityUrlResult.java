package space.parzival.discord.badley.service.steam.model.webapi;

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
@JsonDeserialize(builder = WebApiResolveVanityUrlResult.WebApiResolveVanityUrlResultBuilder.class)
public class WebApiResolveVanityUrlResult {
    @JsonProperty("steamid")
    String steamId;

    @JsonProperty("success")
    int success;
}

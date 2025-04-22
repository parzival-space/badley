package space.parzival.discord.badley.service.steam.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = WebApiResolveVanityUrlResponse.WebApiResolveVanityUrlResponseBuilder.class)
public class WebApiResolveVanityUrlResponse {
    @JsonProperty("response")
    WebApiResolveVanityUrlResult response;
}

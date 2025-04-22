package space.parzival.discord.badley.service.steam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = WebApiGenericResponse.WebApiGenericResponseBuilder.class)
public class WebApiGenericResponse<T> {
    @JsonProperty("response")
    T response;
}

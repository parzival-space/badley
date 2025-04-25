package space.parzival.discord.badley.service.brave.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import space.parzival.discord.badley.service.brave.model.query.BraveQueryInfo;
import space.parzival.discord.badley.service.brave.model.query.BraveQueryWebResults;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = BraveQueryResponse.BraveQueryResponseBuilder.class)
public class BraveQueryResponse {
    @JsonProperty("query")
    BraveQueryInfo query;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains redundant information.
     */
    @JsonProperty("mixed")
    Object mixed;

    @JsonProperty("type")
    String type;

    @JsonProperty("web")
    BraveQueryWebResults web;
}

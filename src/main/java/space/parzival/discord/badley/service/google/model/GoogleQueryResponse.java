package space.parzival.discord.badley.service.google.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import space.parzival.discord.badley.service.google.model.query.GoogleQueryResult;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = GoogleQueryResponse.GoogleQueryResponseBuilder.class)
public class GoogleQueryResponse {
    @JsonProperty("kind")
    String kind;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains unused information.
     */
    @JsonProperty("url")
    Object url;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains unused information.
     */
    @JsonProperty("queries")
    Object queries;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains redundant information.
     */
    @JsonProperty("context")
    Object context;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains redundant information.
     */
    @JsonProperty("searchInformation")
    Object searchInformation;

    @JsonProperty("items")
    List<GoogleQueryResult> items;
}

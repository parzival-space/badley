package space.parzival.discord.badley.service.brave.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = BraveQueryWebResult.BraveQueryWebResultBuilder.class)
public class BraveQueryWebResult {
    @JsonProperty("title")
    String title;

    @JsonProperty("url")
    String url;

    @JsonProperty("is_source_local")
    boolean isSourceLocal;

    @JsonProperty("is_source_both")
    boolean isSourceBoth;

    @JsonProperty("description")
    String description;

    @JsonProperty("profile")
    BraveQueryWebResultProfile profile;

    @JsonProperty("language")
    String language;

    @JsonProperty("family_friendly")
    boolean familyFriendly;

    @JsonProperty("type")
    String resultType;

    @JsonProperty("subtype")
    String resultSubtype;

    @JsonProperty("is_live")
    boolean isLive;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains redundant information.
     */
    @JsonProperty("meta_url")
    Object metaUrl;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains redundant information.
     */
    @JsonProperty("thumbnail")
    Object thumbnail;
}

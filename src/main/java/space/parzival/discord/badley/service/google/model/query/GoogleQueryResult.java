package space.parzival.discord.badley.service.google.model.query;

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
@JsonDeserialize(builder = GoogleQueryResult.GoogleQueryResultBuilder.class)
public class GoogleQueryResult {
    @JsonProperty("kind")
    String kind;

    @JsonProperty("title")
    String title;

    @JsonProperty("htmlTitle")
    String htmlTitle;

    @JsonProperty("link")
    String link;

    @JsonProperty("displayLink")
    String displayLink;

    @JsonProperty("snippet")
    String snippet;

    @JsonProperty("htmlSnippet")
    String htmlSnippet;

    @JsonProperty("formattedUrl")
    String formattedUrl;

    @JsonProperty("htmlFormattedUrl")
    String htmlFormattedUrl;

    /**
     * @apiNote DO NOT USE THIS FIELD!
     * @implNote This field will not be parsed as it contains redundant information.
     */
    @JsonProperty("pagemap")
    Object pageMap;
}

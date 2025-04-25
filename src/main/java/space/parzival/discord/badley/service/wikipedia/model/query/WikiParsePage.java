package space.parzival.discord.badley.service.wikipedia.model.query;

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
@JsonDeserialize(builder = WikiParsePage.WikiParsePageBuilder.class)
public class WikiParsePage {
    @JsonProperty("title")
    String title;

    @JsonProperty("pageid")
    int pageId;

    @JsonProperty("text")
    String parsedText;
}

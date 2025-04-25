package space.parzival.discord.badley.service.wikipedia.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import space.parzival.discord.badley.service.wikipedia.model.query.WikiParsePage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = WikiParsePageResponse.WikiParsePageResponseBuilder.class)
public class WikiParsePageResponse {
    @JsonProperty("parse")
    WikiParsePage parseResult;
}

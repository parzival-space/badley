package space.parzival.discord.badley.service.wikipedia.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.time.OffsetDateTime;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = WikiQueryPage.WikiQueryPageBuilder.class)
public class WikiQueryPage {
    @JsonProperty("pageid")
    int pageId;

    @JsonProperty("ns")
    int namespace;

    @JsonProperty("title")
    String title;

    @JsonProperty("extract")
    String extract;

    @JsonProperty("contentmodel")
    String contentModel;

    @JsonProperty("pagelanguage")
    String pageLanguage;

    @JsonProperty("pagelanguagehtmlcode")
    String pageLanguageHtmlCode;

    @JsonProperty("pagelanguagedirection")
    String pageLanguageDirection;

    @JsonProperty("touched")
    OffsetDateTime lastUpdated;

    @JsonProperty("lastrevid")
    int lastRevisionId;

    @JsonProperty("length")
    int length;
}

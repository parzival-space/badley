package space.parzival.discord.badley.service.duckduckgo.model.instantanswers;

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
@JsonDeserialize(builder = RelatedTopic.RelatedTopicBuilder.class)
public class RelatedTopic {
    @JsonProperty("FirstURL")
    String firstUrl;

    @JsonProperty("Result")
    String result;

    @JsonProperty("Text")
    String text;
}

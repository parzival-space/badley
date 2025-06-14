package space.parzival.discord.badley.service.duckduckgo.model.instantanswers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = Infobox.InfoboxBuilder.class)
public class Infobox {
    @JsonProperty("content")
    @Builder.Default
    List<InfoboxLabelValuePair> content = List.of();

    @JsonProperty("meta")
    @Builder.Default
    List<InfoboxLabelValuePair> meta = List.of();
}

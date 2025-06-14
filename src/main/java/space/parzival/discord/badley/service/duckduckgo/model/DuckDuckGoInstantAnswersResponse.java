package space.parzival.discord.badley.service.duckduckgo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import space.parzival.discord.badley.service.duckduckgo.model.instantanswers.Infobox;
import space.parzival.discord.badley.service.duckduckgo.model.instantanswers.RelatedTopic;

import java.io.IOException;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = DuckDuckGoInstantAnswersResponse.DuckDuckGoInstantAnswersResponseBuilder.class)
public class DuckDuckGoInstantAnswersResponse {
    @JsonProperty("AbstractSource")
    String abstractSource;

    @JsonProperty("AbstractText")
    String abstractText;

    @JsonProperty("AbstractURL")
    String abstractUrl;

    @JsonProperty("Heading")
    String heading;

    @JsonProperty("Infobox")
    @JsonDeserialize(using = InfoboxDeserializer.class)
    Infobox infobox;

    @JsonProperty("RelatedTopics")
    List<RelatedTopic> relatedTopics;

    // DuckDuckGo's Instant Answers API can return an Infobox as a JSON object or a string.
    // An empty infobox is represented as an empty string. Very JSON compliant...
    private static class InfoboxDeserializer extends JsonDeserializer<Infobox> {

        @Override
        public Infobox deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            JsonNode node = p.getCodec().readTree(p);

            // return an empty Infobox if the node is an empty string
            if (node.isTextual() && node.asText().isEmpty())
                return Infobox.builder().build();

            return mapper.treeToValue(node, Infobox.class);
        }
    }
}

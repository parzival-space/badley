package space.parzival.discord.badley.service.duckduckgo.model.instantanswers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
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

import java.io.IOException;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = InfoboxLabelValuePair.InfoboxLabelValuePairBuilder.class)
public class InfoboxLabelValuePair {
    @JsonProperty("data_type")
    String dataType;

    @JsonProperty("label")
    String label;

    @JsonProperty("value")
    @JsonDeserialize(using = ObjectAndStringDeserializer.class)
    String value;

    @JsonProperty("wiki_order")
    Integer wikiOrder;

    /// For deserializing the `value` field which can be either an object or a string, we use a custom deserializer that
    /// is capable of handling both cases. If the `value` is an object, it will convert it to a string representation.
    /// If it is a string, it will return the string as is.
    private static class ObjectAndStringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper)  p.getCodec();
            JsonNode node = p.getCodec().readTree(p);
            if (node.isObject() || node.isArray()) {
                return mapper.writeValueAsString(node);
            } else if (node.isTextual()) {
                return node.asText();
            } else {
                throw new IOException("Unexpected JSON type: " + node.getNodeType());
            }
        }
    }
}

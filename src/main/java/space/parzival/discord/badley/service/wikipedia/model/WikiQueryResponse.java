package space.parzival.discord.badley.service.wikipedia.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = WikiQueryResponse.WikiQueryResponseBuilder.class)
public class WikiQueryResponse {
    @JsonProperty("batchcomplete")
    String batchComplete;

    @JsonProperty("query")
    WikiQueryPagesResponse queryResult;
}

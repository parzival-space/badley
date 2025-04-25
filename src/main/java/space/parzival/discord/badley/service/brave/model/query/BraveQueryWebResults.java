package space.parzival.discord.badley.service.brave.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = BraveQueryWebResults.BraveQueryWebResultsBuilder.class)
public class BraveQueryWebResults {
    @JsonProperty("type")
    String resultType;

    @JsonProperty("results")
    List<BraveQueryWebResult> results;

    @JsonProperty("family_friendly")
    boolean familyFriendly;
}

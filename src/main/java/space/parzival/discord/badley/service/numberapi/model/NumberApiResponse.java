package space.parzival.discord.badley.service.numberapi.model;

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
@JsonDeserialize(builder = NumberApiResponse.NumberApiResponseBuilder.class)
public class NumberApiResponse {
    @JsonProperty("text")
    String text;

    @JsonProperty("year")
    int year;

    @JsonProperty("date")
    String date;

    @JsonProperty("number")
    int number;

    @JsonProperty("found")
    boolean found;

    @JsonProperty("type")
    String type;
}

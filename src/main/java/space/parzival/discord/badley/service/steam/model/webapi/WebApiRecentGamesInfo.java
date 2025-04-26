package space.parzival.discord.badley.service.steam.model.webapi;

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
@JsonDeserialize(builder = WebApiRecentGamesInfo.WebApiRecentGamesInfoBuilder.class)
public class WebApiRecentGamesInfo {
    @JsonProperty("total_count")
    int total;

    @JsonProperty("games")
    List<WebApiRecentGameInfo> games;
}

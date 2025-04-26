package space.parzival.discord.badley.service.steam.model.webapi;

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
@JsonDeserialize(builder = WebApiPlayerBanInfo.WebApiPlayerBanInfoBuilder.class)
public class WebApiPlayerBanInfo {
    @JsonProperty("SteamId")
    String steamId;

    @JsonProperty("CommunityBanned")
    boolean communityBanned;

    @JsonProperty("VACBanned")
    boolean vacBanned;

    @JsonProperty("NumberOfVACBans")
    int numberOfVacBans;

    @JsonProperty("DaysSinceLastBan")
    int daysSinceLastBan;

    @JsonProperty("NumberOfGameBans")
    int numberOfGameBans;

    @JsonProperty("EconomyBan")
    String economyBan;
}

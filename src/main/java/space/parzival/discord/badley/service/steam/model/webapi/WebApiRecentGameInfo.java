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
@JsonDeserialize(builder = WebApiRecentGameInfo.WebApiRecentGameInfoBuilder.class)
public class WebApiRecentGameInfo {
    @JsonProperty("appid")
    int id;

    @JsonProperty("name")
    String name;

    @JsonProperty("playtime_2weeks")
    int playtime2Weeks;

    @JsonProperty("playtime_forever")
    int playtimeForever;

    @JsonProperty("img_icon_url")
    String iconUrl;

    @JsonProperty("playtime_windows_forever")
    int playtimeWindowsForever;

    @JsonProperty("playtime_mac_forever")
    int playtimeMacForever;

    @JsonProperty("playtime_linux_forever")
    int playtimeLinuxForever;

    @JsonProperty("playtime_deck_forever")
    int playtimeDeckForever;
}

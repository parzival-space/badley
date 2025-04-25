package space.parzival.discord.badley.service.steam.model.webapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = WebApiPlayerSummary.WebApiPlayerSummaryBuilder.class)
public class WebApiPlayerSummary {
    @JsonProperty("steamid")
    String steamId;

    @JsonProperty("personaname")
    String personaName;

    @JsonProperty("realname")
    String realName;

    @JsonProperty("profileurl")
    String profileUrl;

    @JsonProperty("avatarfull")
    String avatarUrl;

    @JsonProperty("loccountrycode")
    String lastCountryCode;

    @JsonProperty("lastlogoff")
    OffsetDateTime lastLogOff;

    @JsonProperty("timecreated")
    OffsetDateTime timeCreated;
}

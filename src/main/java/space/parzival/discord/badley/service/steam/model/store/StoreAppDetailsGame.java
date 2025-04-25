package space.parzival.discord.badley.service.steam.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import space.parzival.discord.badley.service.steam.model.store.generic.StoreGamePlatforms;
import space.parzival.discord.badley.service.steam.model.store.generic.StoreGamePrice;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = StoreAppDetailsGame.StoreAppDetailsGameBuilder.class)
public class StoreAppDetailsGame {
    @JsonProperty("type")
    String type;

    @JsonProperty("name")
    String name;

    @JsonProperty("steam_appid")
    int id;

    @JsonProperty("required_age")
    int requiredAge;

    @JsonProperty("is_free")
    boolean isFree;

    @JsonProperty("controller_support")
    String controllerSupport;

    @JsonProperty("dlc")
    List<Integer> dlc;

    @JsonProperty("detailed_description")
    String detailedDescription;

    @JsonProperty("about_the_game")
    String aboutTheGame;

    @JsonProperty("short_description")
    String shortDescription;

    @JsonProperty("supported_languages")
    String supportedLanguages;

    @JsonProperty("website")
    String website;

    @JsonProperty("pc_requirements")
    StoreAppDetailsRequirements pcRequirements;

    @JsonProperty("mac_requirements")
    StoreAppDetailsRequirements macRequirements;

    @JsonProperty("linux_requirements")
    StoreAppDetailsRequirements linuxRequirements;

    @JsonProperty("developers")
    List<String> developers;

    @JsonProperty("publishers")
    List<String> publishers;

    @JsonProperty("price_overview")
    StoreGamePrice price;

    @JsonProperty("packages")
    List<Integer> packages;

    @JsonProperty("platforms")
    StoreGamePlatforms platforms;

    @JsonProperty("metacritic")
    StoreAppDetailsMetacritic metacritic;

    @JsonProperty("support_info")
    StoreAppDetailsSupportInfo supportInfo;
}

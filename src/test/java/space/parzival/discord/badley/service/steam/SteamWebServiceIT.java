package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.SteamProperties;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.WebApiPlayerBansResponse;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerLevelInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummariesResult;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiRecentGamesInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(SteamProperties.class)
@RestClientTest(value = SteamWebService.class, properties = {
    "badley.ai.tools.steam.token=token",
})
class SteamWebServiceIT {
    @Autowired
    private SteamWebService service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void resolveProfileUrl_returns_validData() {
        server.expect(requestTo("/ISteamUser/ResolveVanityURL/v1/?key=token&vanityurl=parzival-space"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/valid-resolvevanityurl-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiGenericResponse<WebApiResolveVanityUrlResult> response =
            service.resolveProfileUrl("https://steamcommunity.com/id/parzival-space/");

        assertNotNull(response);
        assertEquals(1, response.getResponse().getSuccess());
        assertEquals("76561198197402058", response.getResponse().getSteamId());
    }

    @Test
    void resolveProfileUrl_returns_emptyData() {
        server.expect(requestTo("/ISteamUser/ResolveVanityURL/v1/?key=token&vanityurl=test"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/empty-resolvevanityurl-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiGenericResponse<WebApiResolveVanityUrlResult> response =
            service.resolveProfileUrl("https://steamcommunity.com/id/test/");

        assertNotNull(response);
        assertEquals(42, response.getResponse().getSuccess());
        assertNull(response.getResponse().getSteamId());
    }

    @Test
    void getPlayerSummary_returns_validData() {
        server.expect(requestTo("/ISteamUser/GetPlayerSummaries/v2/?key=token&steamids=76561198197402058"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/valid-getplayersummaries-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiGenericResponse<WebApiPlayerSummariesResult> response =
            service.getPlayerSummary("76561198197402058");

        assertNotNull(response);
        assertEquals(1, response.getResponse().getPlayers().size());
        assertEquals("76561198197402058", response.getResponse().getPlayers().getFirst().getSteamId());
        assertEquals("Parzival", response.getResponse().getPlayers().getFirst().getPersonaName());
    }

    @Test
    void getPlayerSummary_returns_emptyData() {
        server.expect(requestTo("/ISteamUser/GetPlayerSummaries/v2/?key=token&steamids=76561198197402058"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/empty-getplayersummaries-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiGenericResponse<WebApiPlayerSummariesResult> response =
            service.getPlayerSummary("76561198197402058");

        assertNotNull(response);
        assertEquals(0, response.getResponse().getPlayers().size());
    }

    @Test
    void getPlayerBans_returns_validData() {
        server.expect(requestTo("/ISteamUser/GetPlayerBans/v1/?key=token&steamids=76561198197402058"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/valid-getplayerbans-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiPlayerBansResponse response =
            service.getPlayerBans("76561198197402058");

        assertNotNull(response);
        assertEquals(1, response.getPlayers().size());
        assertEquals("76561198197402058", response.getPlayers().getFirst().getSteamId());
        assertFalse(response.getPlayers().getFirst().isCommunityBanned());
        assertTrue(response.getPlayers().getFirst().isVacBanned());
        assertEquals(1, response.getPlayers().getFirst().getNumberOfVacBans());
        assertEquals(1757, response.getPlayers().getFirst().getDaysSinceLastBan());
        assertEquals(0, response.getPlayers().getFirst().getNumberOfGameBans());
        assertEquals("none", response.getPlayers().getFirst().getEconomyBan());
    }

    @Test
    void getPlayerLevel_returns_validData() {
        server.expect(requestTo("/IPlayerService/GetSteamLevel/v1/?key=token&steamid=76561198197402058"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/valid-getsteamlevel-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiGenericResponse<WebApiPlayerLevelInfo> response =
            service.getPlayerLevel("76561198197402058");

        assertNotNull(response);
        assertEquals(94, response.getResponse().getLevel());
    }

    @Test
    void getRecentPlayedGames_returns_validData() {
        server.expect(requestTo("/IPlayerService/GetRecentlyPlayedGames/v1/?key=token&steamid=76561198197402058"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/web/valid-recentplayedgames-response.json"),
                MediaType.APPLICATION_JSON
            ));

        WebApiGenericResponse<WebApiRecentGamesInfo> response =
            service.getRecentPlayedGames("76561198197402058");

        assertNotNull(response);
        assertEquals(11, response.getResponse().getTotal());
        assertEquals(1172620, response.getResponse().getGames().getFirst().getId());
        assertEquals("Sea of Thieves", response.getResponse().getGames().getFirst().getName());
        assertEquals(501, response.getResponse().getGames().getFirst().getPlaytime2Weeks());
        assertEquals(13558, response.getResponse().getGames().getFirst().getPlaytimeForever());
        assertEquals("f95f362708fc326511c5d86566c447ee625bf776",
            response.getResponse().getGames().getFirst().getIconUrl());
        assertEquals(13395, response.getResponse().getGames().getFirst().getPlaytimeWindowsForever());
        assertEquals(0, response.getResponse().getGames().getFirst().getPlaytimeMacForever());
        assertEquals(162, response.getResponse().getGames().getFirst().getPlaytimeLinuxForever());
        assertEquals(88, response.getResponse().getGames().getFirst().getPlaytimeDeckForever());
    }
}

package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.steam.SteamStoreService;
import space.parzival.discord.badley.service.steam.SteamWebService;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.WebApiPlayerBansResponse;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsGame;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsMetacritic;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsRequirements;
import space.parzival.discord.badley.service.steam.model.store.StoreFeaturedContainer;
import space.parzival.discord.badley.service.steam.model.store.StoreFeaturedGame;
import space.parzival.discord.badley.service.steam.model.store.StoreSearchGame;
import space.parzival.discord.badley.service.steam.model.store.generic.StoreGamePlatforms;
import space.parzival.discord.badley.service.steam.model.store.generic.StoreGamePrice;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerBanInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerLevelInfo;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummariesResult;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiPlayerSummary;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SteamToolsTest {

    @Test
    void searchStore_returns_validData() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);
        StoreSearchResponse mockResponse = StoreSearchResponse.builder()
            .total(1)
            .items(List.of(createStoreSearchGame()))
            .build();

        when(steamStoreService.searchStore(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.searchStore("Test Game", "english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Test Game (app)"));
        assertTrue(response.contains("Price: 19.99 (USD)"));
        assertTrue(response.contains("Meta Score: 85"));
    }

    @Test
    void searchStore_returns_errorMessage() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);

        when(steamStoreService.searchStore(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Error fetching data"));

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.searchStore("Test Game", "english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Error fetching data"));
    }

    @Test
    void getFeaturedCategories_returns_validData() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);
        StoreFeaturedResponse mockResponse = StoreFeaturedResponse.builder()
            .comingSoon(StoreFeaturedContainer.builder().items(List.of(createStoreFeaturedGame())).build())
            .newReleases(StoreFeaturedContainer.builder().items(List.of(createStoreFeaturedGame())).build())
            .specials(StoreFeaturedContainer.builder().items(List.of(createStoreFeaturedGame())).build())
            .topSellers(StoreFeaturedContainer.builder().items(List.of(createStoreFeaturedGame())).build())
            .build();
        when(steamStoreService.getFeaturedCategories(anyString(), anyString())).thenReturn(mockResponse);

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getFeaturedCategories("english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Test Game"));
    }

    @Test
    void getFeaturedCategories_returns_errorMessage() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);

        when(steamStoreService.getFeaturedCategories(anyString(), anyString()))
            .thenThrow(new RuntimeException("Error fetching data"));

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getFeaturedCategories("english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Error fetching data"));
    }

    @Test
    void getGameDetails_returns_validData() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);
        StoreAppDetailsResponse mockResponse = StoreAppDetailsResponse.builder()
            .game(createStoreAppDetailsGame())
            .success(true)
            .build();
        when(steamStoreService.getAppDetails(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getGameDetails("123456", "english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Test Game (game)"));
        assertTrue(response.contains("Price: 10.0 (USD)"));
    }

    @Test
    void getGameDetails_returns_errorMessage() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);

        when(steamStoreService.getAppDetails(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Error fetching data"));

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getGameDetails("123456", "english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Error fetching data"));
    }

    @Test
    void getUserIdFromProfileUrl_returns_validData() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);
        WebApiGenericResponse<WebApiResolveVanityUrlResult> mockResponse = WebApiGenericResponse.<WebApiResolveVanityUrlResult>builder()
            .response(WebApiResolveVanityUrlResult.builder()
                .steamId("123456789")
                .success(1)
                .build())
            .build();
        when(steamWebService.resolveProfileUrl(anyString())).thenReturn(mockResponse);

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getUserIdFromProfileUrl("https://steamcommunity.com/id/testuser");

        assertNotNull(response);
        assertTrue(response.contains("123456789"));
    }

    @Test
    void getUserIdFromProfileUrl_returns_errorMessage() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);

        when(steamWebService.resolveProfileUrl(anyString()))
            .thenThrow(new RuntimeException("Error fetching data"));

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getUserIdFromProfileUrl("https://steamcommunity.com/id/testuser");

        assertNotNull(response);
        assertTrue(response.contains("Error fetching data"));
    }

    @Test
    void getUserDetailsFromId_return_validData() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);
        WebApiGenericResponse<WebApiPlayerSummariesResult> mockResponse = WebApiGenericResponse.<WebApiPlayerSummariesResult>builder()
            .response(WebApiPlayerSummariesResult.builder()
                .players(List.of(createWebApiPlayerSummary()))
                .build())
            .build();
        when(steamWebService.getPlayerSummary(anyString())).thenReturn(mockResponse);
        when(steamWebService.getPlayerLevel(anyString())).thenReturn(createWebApiPlayerLevelInfo());
        when(steamWebService.getPlayerBans(anyString())).thenReturn(createWebApiPlayerBansResponse());

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getUserDetailsFromId("123456789");

        assertNotNull(response);
        assertTrue(response.contains("Test User"));
        assertTrue(response.contains("Level: 100"));
        assertTrue(response.contains("Real Name"));
        assertTrue(response.contains("https://steamcommunity.com/id/testuser"));
        assertTrue(response.contains("https://avatar.url"));
    }

    @Test
    void getUserDetailsFromId_returns_errorMessage() {
        SteamWebService steamWebService = mock(SteamWebService.class);
        SteamStoreService steamStoreService = mock(SteamStoreService.class);

        when(steamWebService.getPlayerSummary(anyString()))
            .thenThrow(new RuntimeException("Error fetching data"));

        SteamTools steamTools = new SteamTools(steamWebService, steamStoreService);
        String response = steamTools.getUserDetailsFromId("123456789");

        assertNotNull(response);
        assertTrue(response.contains("Error fetching data"));
    }

    private StoreSearchGame createStoreSearchGame() {
        return StoreSearchGame.builder()
            .id(123456)
            .name("Test Game")
            .type("app")
            .platforms(StoreGamePlatforms.builder()
                .windows(true)
                .mac(false)
                .linux(false)
                .build())
            .price(StoreGamePrice.builder()
                .currency("USD")
                .initialPrice(2999)
                .finalPrice(1999)
                .build())
            .metaScore("85")
            .build();
    }

    private StoreFeaturedGame createStoreFeaturedGame() {
        return StoreFeaturedGame.builder()
            .id(123456)
            .name("Test Game")
            .type(0)
            .discounted(true)
            .discountPercent(50)
            .originalPrice(3999)
            .finalPrice(1999)
            .currency("USD")
            .windowsAvailable(true)
            .macAvailable(false)
            .linuxAvailable(false)
            .discountExpiration(OffsetDateTime.now())
            .controllerSupport("full")
            .build();
    }

    private StoreAppDetailsGame createStoreAppDetailsGame() {
        return StoreAppDetailsGame.builder()
            .name("Test Game")
            .type("game")
            .id(123456)
            .price(StoreGamePrice.builder()
                .finalPrice(1000)
                .initialPrice(1000)
                .currency("USD")
                .build())
            .metacritic(StoreAppDetailsMetacritic.builder()
                .score(100)
                .url("https://www.metacritic.com")
                .build())
            .shortDescription("Test description")
            .developers(List.of("Test Developer"))
            .publishers(List.of("Test Publisher"))
            .website("https://www.testgame.com")
            .dlc(List.of())
            .pcRequirements(StoreAppDetailsRequirements.builder()
                .minimum("Test minimum requirements")
                .build())
            .macRequirements(StoreAppDetailsRequirements.builder()
                .minimum("Test minimum requirements")
                .build())
            .linuxRequirements(StoreAppDetailsRequirements.builder()
                .minimum("Test minimum requirements")
                .build())
            .platforms(StoreGamePlatforms.builder()
                .windows(true)
                .mac(false)
                .linux(false)
                .build())
            .build();
    }

    private WebApiPlayerSummary createWebApiPlayerSummary() {
        return WebApiPlayerSummary.builder()
            .steamId("123456789")
            .personaName("Test User")
            .realName("Real Name")
            .profileUrl("https://steamcommunity.com/id/testuser")
            .avatarUrl("https://avatar.url")
            .lastCountryCode("US")
            .lastLogOff(OffsetDateTime.now())
            .timeCreated(OffsetDateTime.now())
            .build();
    }

    private WebApiGenericResponse<WebApiPlayerLevelInfo> createWebApiPlayerLevelInfo() {
        return WebApiGenericResponse.<WebApiPlayerLevelInfo>builder()
            .response(WebApiPlayerLevelInfo.builder()
                .level(100)
                .build())
            .build();
    }

    private WebApiPlayerBansResponse createWebApiPlayerBansResponse() {
        return WebApiPlayerBansResponse.builder()
            .players(List.of(
                WebApiPlayerBanInfo.builder()
                    .communityBanned(true)
                    .vacBanned(true)
                    .daysSinceLastBan(30)
                    .numberOfGameBans(0)
                    .economyBan("none")
                    .build()
            ))
            .build();
    }
}

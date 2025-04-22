package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.steam.SteamService;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.store.StoreSearchGame;
import space.parzival.discord.badley.service.steam.model.store.StoreSearchPlatforms;
import space.parzival.discord.badley.service.steam.model.store.StoreSearchPrice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SteamToolsTest {

    @Test
    void searchStore_returns_validData() {
        SteamService steamService = mock(SteamService.class);
        StoreSearchResponse mockResponse = StoreSearchResponse.builder()
                .total(1)
                .items(List.of(createStoreSearchGame()))
                .build();

        when(steamService.searchStore(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        SteamTools steamTools = new SteamTools(steamService);
        String response = steamTools.searchStore("Test Game", "english", "US");

        assertNotNull(response);
        assertTrue(response.contains("Test Game (app)"));
        assertTrue(response.contains("Price: 19.99 (USD)"));
        assertTrue(response.contains("Meta Score: 85"));

    }

    private StoreSearchGame createStoreSearchGame() {
        return StoreSearchGame.builder()
                .id(123456)
                .name("Test Game")
                .type("app")
                .platforms(StoreSearchPlatforms.builder()
                        .windows(true)
                        .mac(false)
                        .linux(false)
                        .build())
                .price(StoreSearchPrice.builder()
                        .currency("USD")
                        .initialPrice(2999)
                        .finalPrice(1999)
                        .build())
                .metaScore("85")
                .build();
    }
}
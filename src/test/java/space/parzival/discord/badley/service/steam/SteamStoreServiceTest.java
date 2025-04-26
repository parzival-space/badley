package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import space.parzival.discord.badley.configuration.properties.SteamProperties;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SteamStoreServiceTest {

    @Test
    void searchStore() {
        SteamStoreService steamService = new SteamStoreService(new RestTemplateBuilder());

        StoreSearchResponse result = steamService.searchStore("Sea of Thieves", "english", null);
        assertNotNull(result);

        assertNotNull(result.getItems());
        assertFalse(result.getItems().isEmpty());
        assertNotNull(result.getItems().getFirst());
    }

    @Test
    void getFeaturedCategories() {
        SteamStoreService steamService = new SteamStoreService(new RestTemplateBuilder());

        StoreFeaturedResponse result = steamService.getFeaturedCategories(null, null);
        assertNotNull(result);

        assertNotNull(result.getComingSoon());
        assertNotNull(result.getSpecials());
        assertNotNull(result.getTopSellers());
        assertNotNull(result.getNewReleases());
    }

    @Test
    void getAppDetails() {
        final String APP_ID = "346110";
        SteamStoreService steamService = new SteamStoreService(new RestTemplateBuilder());

        StoreAppDetailsResponse result = steamService.getAppDetails(APP_ID, null, null);
        assertNotNull(result);

        assertNotNull(result.getGame());
        assertEquals(Integer.valueOf(APP_ID), result.getGame().getId());
    }
}

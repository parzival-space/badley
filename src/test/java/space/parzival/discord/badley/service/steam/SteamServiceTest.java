package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;

import static org.junit.jupiter.api.Assertions.*;

class SteamServiceTest {

    @Test
    void searchStore() {
        SteamService steamService = new SteamService();

        StoreSearchResponse result = steamService.searchStore("Sea of Thieves", "english", null);
        assertNotNull(result);

        assertNotNull(result.getItems());
        assertFalse(result.getItems().isEmpty());
        assertNotNull(result.getItems().getFirst());
    }

    @Test
    void getFeaturedCategories() {
        SteamService steamService = new SteamService();

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
        SteamService steamService = new SteamService();

        StoreAppDetailsResponse result = steamService.getAppDetails(APP_ID, null, null);
        assertNotNull(result);

        assertNotNull(result.getGame());
        assertEquals(Integer.valueOf(APP_ID), result.getGame().getId());
    }
}
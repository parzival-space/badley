package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;

import java.util.List;

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
        final Integer APP_ID = 346110;
        SteamService steamService = new SteamService();

        StoreAppDetailsResponse result = steamService.getAppDetails(List.of(APP_ID), null, null);
        assertNotNull(result);

        assertNotNull(result.getItems());
        assertFalse(result.getItems().isEmpty());
        assertEquals(APP_ID, result.getItems().get(APP_ID.toString()).getGame().getId());
    }
}
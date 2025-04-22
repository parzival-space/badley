package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.configuration.properties.SteamProperties;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;
import space.parzival.discord.badley.service.steam.model.store.StoreSearchGame;

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
        assertNotNull(result.getItems().getLast().getPrice());
    }
}
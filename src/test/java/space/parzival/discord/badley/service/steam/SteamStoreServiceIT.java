package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.SteamProperties;
import space.parzival.discord.badley.service.steam.model.StoreAppDetailsResponse;
import space.parzival.discord.badley.service.steam.model.StoreFeaturedResponse;
import space.parzival.discord.badley.service.steam.model.StoreSearchResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(SteamProperties.class)
@RestClientTest(value = SteamStoreService.class, properties = {
    "badley.ai.tools.steam.token=token",
})
class SteamStoreServiceIT {
    @Autowired
    private SteamStoreService service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @Test
    void searchStore_returns_validData() {
        server.expect(requestTo("/storesearch?term=test&l=english&cc=DE"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/store/valid-storesearch-response.json"),
                MediaType.APPLICATION_JSON
            ));

        StoreSearchResponse response = service.searchStore("test", "english", "DE");

        assertNotNull(response);
        assertEquals(9, response.getTotal());
        assertEquals(9, response.getItems().size());
        assertEquals("Baldur's Gate 3", response.getItems().getFirst().getName());
        assertEquals(1086940, response.getItems().getFirst().getId());
        assertEquals("EUR", response.getItems().getFirst().getPrice().getCurrency());
        assertEquals(5999, response.getItems().getFirst().getPrice().getFinalPrice());
        assertEquals(5999, response.getItems().getFirst().getPrice().getInitialPrice());
        assertEquals("96", response.getItems().getFirst().getMetaScore());
        assertEquals("full", response.getItems().getFirst().getControllerSupport());
    }

    @Test
    void getFeaturedCategories_returns_validData() {
        server.expect(requestTo("/featuredcategories?l=english&cc=DE"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/store/valid-featuredcategories-response.json"),
                MediaType.APPLICATION_JSON
            ));

        StoreFeaturedResponse response = service.getFeaturedCategories("english", "DE");

        assertNotNull(response);
        assertEquals(1, response.getStatus());
        assertNotNull(response.getSpecials());
        assertEquals(10, response.getSpecials().getItems().size());
        assertNotNull(response.getComingSoon());
        assertEquals(10, response.getComingSoon().getItems().size());
        assertNotNull(response.getTopSellers());
        assertEquals(10, response.getTopSellers().getItems().size());
        assertNotNull(response.getNewReleases());
        assertEquals(30, response.getNewReleases().getItems().size());
    }

    @Test
    void getAppDetails_returns_validData() {
        server.expect(requestTo("/appdetails?appids=1086940&l=english&cc=DE"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/steam/store/valid-appdetails-response.json"),
                MediaType.APPLICATION_JSON
            ));

        StoreAppDetailsResponse response = service.getAppDetails("1086940", "english", "DE");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getGame());
        assertEquals(1086940, response.getGame().getId());
        assertEquals("Baldur's Gate 3", response.getGame().getName());
        assertEquals("Larian Studios", response.getGame().getDevelopers().getFirst());
        assertEquals("Larian Studios", response.getGame().getPublishers().getFirst());
    }

    @Test
    void getAppDetails_returns_emptyData() {
        server.expect(requestTo("/appdetails?appids=1086940&l=english&cc=DE"))
            .andRespond(withSuccess());

        StoreAppDetailsResponse response = service.getAppDetails("1086940", "english", "DE");

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getGame());
    }
}

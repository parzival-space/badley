package space.parzival.discord.badley.service.steam;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import space.parzival.discord.badley.configuration.properties.tools.SteamProperties;
import space.parzival.discord.badley.service.steam.model.WebApiGenericResponse;
import space.parzival.discord.badley.service.steam.model.webapi.WebApiResolveVanityUrlResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SteamWebServiceTest {
    private SteamWebService steamWebService;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);

        when(restTemplateBuilder.rootUri(anyString())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.defaultHeader(anyString(), anyString())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(null);

        steamWebService = new SteamWebService(restTemplateBuilder, mock(SteamProperties.class));
    }

    @Test
    void resolveProfileUrl_throws_invalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> steamWebService.resolveProfileUrl("invalid_url"));
    }

    @Test
    void resolveProfileUrl_returns_alreadyParsedProfileUrl() {
        WebApiGenericResponse<WebApiResolveVanityUrlResult> response =
            steamWebService.resolveProfileUrl("https://steamcommunity.com/profiles/12345678901234567");

        assertNotNull(response);
        assertEquals(1, response.getResponse().getSuccess());
        assertEquals("12345678901234567", response.getResponse().getSteamId());
    }
}

package space.parzival.discord.badley.service.exchangerate;

import jakarta.transaction.NotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.tools.ExchangeRateApiProperties;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateRatesResponse;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateSupportedCodesResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(ExchangeRateApiProperties.class)
@RestClientTest(value = ExchangeRateService.class, properties = "badley.ai.tools.exchange-rate-api.enabled=true")
class ExchangeRateServiceIT {
    @Autowired
    private ExchangeRateService exchangeRateService;

    @MockitoSpyBean
    private ExchangeRateApiProperties exchangeRateApiProperties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void getSupportedCodes_returns_validData() throws NotSupportedException {
        when(exchangeRateApiProperties.getToken()).thenReturn("test-token");

        server.expect(req -> req.getURI().getPath().equals("https://v6.exchangerate-api.com/v6/test-token/codes"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/exchangerate/auth/codes/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        ExchangeRateSupportedCodesResponse result = exchangeRateService.getSupportedCodes();

        assertNotNull(result);
        assertNotNull(result.getSupportedCodes());
        assertEquals(163, result.getSupportedCodes().size());
    }

    @Test
    void getSupportedCodes_throwsException_whenNoTokenProvided() {
        when(exchangeRateApiProperties.getToken()).thenReturn(null);

        assertThrows(NotSupportedException.class, () -> {
            exchangeRateService.getSupportedCodes();
        });
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void getRates_returns_validData_usingFreeAndAuthAPI(boolean provideToken) {
        if (provideToken) when(exchangeRateApiProperties.getToken()).thenReturn("test-token");

        String baseCode = "USD";

        // set path based on whether token is provided or not
        String reqPath = provideToken
            ? "https://v6.exchangerate-api.com/v6/test-token/latest/" + baseCode
            : "https://open.er-api.com/V6/latest/" + baseCode;

        String resPath = provideToken
            ? "classpath:mock/exchangerate/auth/rates/valid-response.json"
            : "classpath:mock/exchangerate/open/rates/valid-response.json";

        server.expect(req -> req.getURI().getPath().equals(reqPath))
            .andRespond(withSuccess(
                resourceLoader.getResource(resPath),
                MediaType.APPLICATION_JSON
            ));

        ExchangeRateRatesResponse result = exchangeRateService.getRates(baseCode);

        assertNotNull(result);
        assertEquals(baseCode, result.getBaseCode());
        assertNotNull(result.getConversionRates());
        assertTrue(result.getConversionRates().containsKey("EUR"));
    }
}

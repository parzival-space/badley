package space.parzival.discord.badley.ai.service;

import jakarta.transaction.NotSupportedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.exchangerate.ExchangeRateService;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateRatesResponse;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateSupportedCodesResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExchangeRateToolsTest {
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        exchangeRateService = mock(ExchangeRateService.class);
    }

    @Test
    void getSupportedCurrencies() throws NotSupportedException {
        when(exchangeRateService.getSupportedCodes()).thenReturn(ExchangeRateSupportedCodesResponse.builder()
            .result("mock")
            .supportedCodes(List.of(
                    new String[]{"USD", "United States Dollar"},
                    new String[]{"EUR", "Euro"},
                    new String[]{"GBP", "British Pound"}
            ))
            .build());

        ExchangeRateTools exchangeRateTools = new ExchangeRateTools(exchangeRateService);
        String result = exchangeRateTools.getSupportedCurrencies();

        assertNotNull(result);
        assertLinesMatch(Arrays.stream(result.split("\n")).toList(),
            List.of(
                "Supported Currencies:",
                "- United States Dollar (USD)",
                "- Euro (EUR)",
                "- British Pound (GBP)"
            )
        );
    }

    @Test
    void getCurrencyExchangeRates() {
        String baseCurrency = "USD";
        when(exchangeRateService.getRates(baseCurrency)).thenReturn(
            ExchangeRateRatesResponse.builder()
                .result("mock")
                .conversionRates(Map.of(
                    "GBP", 0.25f,
                    "EUR", 0.5f,
                    "USD", 1.0f
                ))
                .build()
        );

        ExchangeRateTools exchangeRateTools = new ExchangeRateTools(exchangeRateService);
        String result = exchangeRateTools.getCurrencyExchangeRates(baseCurrency);

        assertNotNull(result);
        assertTrue(result.contains("GBP: 0.25 USD"));
        assertTrue(result.contains("EUR: 0.5 USD"));
        assertTrue(result.contains("USD: 1.0 USD"));
    }
}

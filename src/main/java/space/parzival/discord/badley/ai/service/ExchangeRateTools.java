package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.service.exchangerate.ExchangeRateService;
import space.parzival.discord.badley.service.exchangerate.model.ExchangeRateSupportedCodesResponse;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.exchange-rate-api.enabled", havingValue = "true")
@AllArgsConstructor
public class ExchangeRateTools implements AiTools {
    private ExchangeRateService exchangeRateService;

    private static final String SUPPORTED_CURRENCIES_TEMPLATE = """
        Supported Currencies:
        ${currencies}
        """.stripIndent();

    @Tool(description = "Get a list of supported currencies and their ISO codes of which you can request their exchange rates.")
    public String getSupportedCurrencies() {
        log.debug("AI is requesting supported currencies from Exchange Rate API");

        try {
            ExchangeRateSupportedCodesResponse response = exchangeRateService.getSupportedCodes();

            return StringSubstitutor.replace(SUPPORTED_CURRENCIES_TEMPLATE, Map.of(
                "currencies", response.getSupportedCodes().stream()
                    .filter(code -> code.length >= 2)
                    .map(code -> "- " + code[1] + "(" + code[0] + ")" )
                    .collect(Collectors.joining("\n"))
            ));
        } catch (Exception e) {
            log.error("Error while fetching supported currencies: {}", e.getMessage(), e);
            return "Error fetching supported currencies.";
        }
    }
}

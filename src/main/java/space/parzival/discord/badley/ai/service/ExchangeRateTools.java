package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.exchange-rate-api.enabled", havingValue = "true")
@AllArgsConstructor
public class ExchangeRateTools implements AiTools {
}

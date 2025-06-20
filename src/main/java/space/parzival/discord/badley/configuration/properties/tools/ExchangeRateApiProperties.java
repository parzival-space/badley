package space.parzival.discord.badley.configuration.properties.tools;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.exchange-rate-api")
@AllArgsConstructor
public class ExchangeRateApiProperties {
    /**
     * Whether the Exchange Rate API integration is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * The API token to use for authentication.
     */
    private @Nullable String token;
}

package space.parzival.discord.badley.configuration.properties.tools;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.openweather")
@AllArgsConstructor
public class OpenWeatherProperties {
    /**
     * Whether the OpenWeather integration is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * The OpenWeather API token to use for authentication.
     */
    private @Nullable String token;
}

package space.parzival.discord.badley.configuration.properties;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.openweather")
@AllArgsConstructor
public class OpenWeatherProperties {
    /**
     * The OpenWeather API token to use for authentication.
     */
    private @Nullable String token;
}

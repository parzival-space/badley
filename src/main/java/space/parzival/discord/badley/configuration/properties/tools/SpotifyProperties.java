package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.spotify")
@AllArgsConstructor
public class SpotifyProperties {
    /**
     * Whether the Spotify integration is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * The Spotify client ID to use for authentication.
     */
    private String clientId;

    /**
     * The Spotify client secret to use for authentication.
     */
    private String clientSecret;
}

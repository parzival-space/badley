package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.spotify")
@AllArgsConstructor
public class SpotifyProperties {
    /**
     * The Spotify client ID to use for authentication.
     */
    String clientId;

    /**
     * The Spotify client secret to use for authentication.
     */
    String clientSecret;
}

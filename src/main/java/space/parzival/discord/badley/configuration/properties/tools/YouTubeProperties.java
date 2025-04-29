package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.youtube")
@AllArgsConstructor
public class YouTubeProperties {
    /**
     * YouTube Data API v3 key.
     */
    private String token;
}

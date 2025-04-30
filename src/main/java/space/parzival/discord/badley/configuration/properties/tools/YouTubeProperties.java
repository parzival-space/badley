package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.youtube")
@AllArgsConstructor
public class YouTubeProperties {
    /**
     * Whether the YouTube integration is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * YouTube Data API v3 key.
     */
    private String token;
}

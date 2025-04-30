package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.brave")
@AllArgsConstructor
public class BraveProperties {
    /**
     * Whether the Brave Search API is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * The Brave Search API key to use for authentication.
     */
    private String token;
}

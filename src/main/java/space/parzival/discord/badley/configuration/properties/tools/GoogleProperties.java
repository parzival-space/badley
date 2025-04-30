package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.google")
@AllArgsConstructor
public class GoogleProperties {
    /**
     * Whether the Google Search integration is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * Programmable Search Engine API key.
     */
    private String token;

    /**
     * Programmable Search Engine ID.
     */
    private String engineId;
}

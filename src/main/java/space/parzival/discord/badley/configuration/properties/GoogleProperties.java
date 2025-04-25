package space.parzival.discord.badley.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.google")
@AllArgsConstructor
public class GoogleProperties {
    /**
     * Programmable Search Engine API key.
     */
    private String token;

    /**
     * Programmable Search Engine ID.
     */
    private String engineId;
}

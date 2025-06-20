package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.number-api")
@AllArgsConstructor
public class NumberApiProperties {
    /**
     * Whether the Number API integration is enabled.
     */
    private @NotNull boolean enabled = true;
}

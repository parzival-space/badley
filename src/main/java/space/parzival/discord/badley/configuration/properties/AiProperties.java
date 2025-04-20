package space.parzival.discord.badley.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai")
@AllArgsConstructor
public class AiProperties {
    /**
     * The name of the AI Agent.
     */
    private @NotNull String name;

    /**
     * A description of the AI personality.
     */
    private @NotNull String personality;
}

package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.self-identity")
@AllArgsConstructor
public class SelfIdentityProperties {
    /**
     * Whether the self-identity tool is enabled.
     */
    private @NotNull boolean enabled = true;
}

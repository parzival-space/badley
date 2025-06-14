package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.duckduckgo")
@AllArgsConstructor
public class DuckDuckGoProperties {
    /**
     * Whether the DuckDuckGo Search API is enabled.
     */
    private @NotNull boolean enabled;
}

package space.parzival.discord.badley.configuration.properties.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.tools.ipapi")
@AllArgsConstructor
public class IpApiProperties {
    /**
     * Whether the IP API integration is enabled.
     */
    private @NotNull boolean enabled;

    /**
     * Allows the bot to expose its own IP address.
     */
    private IpVisibility exposeSelfVisibility = IpVisibility.SHOW_LOCATION;

    public enum IpVisibility {
        SHOW_IP_AND_LOCATION,
        SHOW_LOCATION,
        DISABLED
    }
}

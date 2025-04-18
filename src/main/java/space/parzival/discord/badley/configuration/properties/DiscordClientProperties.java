package space.parzival.discord.badley.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.discord")
public class DiscordClientProperties {
    private String token;
    private boolean autoReconnect = true;
}

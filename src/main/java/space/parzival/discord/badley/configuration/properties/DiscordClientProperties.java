package space.parzival.discord.badley.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.discord")
@AllArgsConstructor
public class DiscordClientProperties {
    /**
     * The Discord API token for the bot.
     * <a href="https://discord.com/developers/docs/topics/oauth2#bot-users">See Bot Users</a>
     */
    private String token;

    /**
     * Specifies whether the bot should automatically reconnect if the connection is lost.
     */
    private boolean autoReconnect = true;

    /**
     * The status message that the bot will display when it is online.
     */
    private String statusMessage = "Badley is online!";
}

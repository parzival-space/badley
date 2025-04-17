package space.parzival.discord.badley.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.configuration.properties.DiscordClientProperties;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
public class DiscordClientConfiguration {
    private List<? extends ListenerAdapter> discordEventListeners;

    @Bean
    public JDA discordClient(DiscordClientProperties discordClientProperties) {
        return JDABuilder
                .createLight(
                        discordClientProperties.getToken(),
                        EnumSet.of(
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.GUILD_MEMBERS,
                                GatewayIntent.MESSAGE_CONTENT
                        ))
                .addEventListeners(discordEventListeners != null ? discordEventListeners.toArray() : List.of().toArray())
                .setAutoReconnect(discordClientProperties.isAutoReconnect())
                .build();
    }
}

package space.parzival.discord.badley.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.CloseCode;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.configuration.properties.DiscordClientProperties;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

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
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.DIRECT_MESSAGES
                ))
            .setAutoReconnect(discordClientProperties.isAutoReconnect())
            .addEventListeners(discordEventListeners != null ? discordEventListeners.toArray() : List.of().toArray())
            .addEventListeners(new ListenerAdapter() {
                @Override
                public void onException(@Nonnull ExceptionEvent event) {
                    log.error("Exception in JDA event: ", event.getCause());
                }

                @Override
                public void onReady(@Nonnull ReadyEvent event) {
                    log.info("Connected to Discord as {}#{}", event.getJDA().getSelfUser().getName(),
                        event.getJDA().getSelfUser().getDiscriminator());

                    // update the bot's status message
                    event.getJDA().getPresence().setPresence(
                        OnlineStatus.ONLINE,
                        Activity.of(
                            Activity.ActivityType.CUSTOM_STATUS,
                            discordClientProperties.getStatusMessage()
                        )
                    );
                }

                @Override
                public void onSessionDisconnect(@Nonnull SessionDisconnectEvent event) {
                    log.warn("Disconnected from Discord with Code {}: {}",
                        Optional.ofNullable(event.getCloseCode())
                            .map(c -> String.valueOf(c.getCode()))
                            .orElse("Unknown"),
                        Optional.ofNullable(event.getCloseCode())
                            .map(CloseCode::getMeaning)
                            .orElse("Unknown"));
                }
            })
            .build();
    }
}

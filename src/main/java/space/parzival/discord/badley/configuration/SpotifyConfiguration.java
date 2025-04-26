package space.parzival.discord.badley.configuration;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import space.parzival.discord.badley.configuration.properties.SpotifyProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class SpotifyConfiguration {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private SpotifyApi apiClient;

    @Bean
    @ConditionalOnProperty(value = "badley.ai.tools.spotify.client-secret")
    public SpotifyApi spotifyApi(SpotifyProperties properties) {
        this.apiClient = SpotifyApi.builder()
            .setClientId(properties.getClientId())
            .setClientSecret(properties.getClientSecret())
            .build();

        // request a initial access token
        scheduleTokenRefreshAsync();

        return this.apiClient;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Spotify API client...");
        this.scheduler.shutdown();
    }

    private void scheduleTokenRefreshAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                final ClientCredentials clientCredentials = this.apiClient.clientCredentials().build().execute();
                this.apiClient.setAccessToken(clientCredentials.getAccessToken());

                // schedule the next token refresh time
                long delayInSeconds = clientCredentials.getExpiresIn() - 60L;
                log.info("Refreshed Spotify access token. Next refresh time: {}",
                    LocalDateTime.now().plusSeconds(delayInSeconds).format(DateTimeFormatter.ISO_LOCAL_TIME));

                this.scheduler.schedule(this::scheduleTokenRefreshAsync, delayInSeconds, TimeUnit.SECONDS);
            } catch (ParseException | SpotifyWebApiException | IOException e) {
                log.error("Error refreshing Spotify access token: {}", e.getMessage(), e);

                // retry in 1 minute
                this.scheduler.schedule(this::scheduleTokenRefreshAsync, 1, TimeUnit.MINUTES);
            }
        });
    }
}

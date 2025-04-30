package space.parzival.discord.badley.configuration.ai.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.configuration.properties.tools.YouTubeProperties;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class YouTubeConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public YouTube youtube(YouTubeProperties properties) throws GeneralSecurityException, IOException {
        return new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            null)
            .setApplicationName("Badley")
            .setGoogleClientRequestInitializer(
                new YouTubeRequestInitializer(properties.getToken()))
            .build();
    }
}

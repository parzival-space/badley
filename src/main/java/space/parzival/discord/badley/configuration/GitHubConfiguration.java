package space.parzival.discord.badley.configuration;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.configuration.properties.GitHubProperties;

import java.io.IOException;

@Slf4j
@Configuration
public class GitHubConfiguration {
    @Bean
    @ConditionalOnProperty(value = "badley.ai.tools.github.token")
    public GitHub github(GitHubProperties properties) throws IOException {
        return new GitHubBuilder()
            .withOAuthToken(properties.getToken())
            .build();
    }
}

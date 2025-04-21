package space.parzival.discord.badley.configuration;

import io.github.mbenincasa.javaopenweathermapclient.client.DefaultOpenWeatherMapClient;
import io.github.mbenincasa.javaopenweathermapclient.client.OpenWeatherMapClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.configuration.properties.OpenWeatherProperties;

@Configuration
public class OpenWeatherConfiguration {
    @Bean
    @ConditionalOnProperty(value = "badley.ai.tools.openweather.token")
    public OpenWeatherMapClient openWeatherClient(OpenWeatherProperties properties) {
        assert properties.getToken() != null;  // NOSONAR - if this is null, something in spring is wrong
        return new DefaultOpenWeatherMapClient(properties.getToken());
    }
}

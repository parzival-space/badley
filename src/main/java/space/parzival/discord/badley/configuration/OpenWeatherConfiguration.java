package space.parzival.discord.badley.configuration;

import io.github.mbenincasa.javaopenweathermapclient.client.DefaultOpenWeatherMapClient;
import io.github.mbenincasa.javaopenweathermapclient.client.OpenWeatherMapClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.configuration.properties.OpenWeatherProperties;

import java.util.Objects;

@Configuration
public class OpenWeatherConfiguration {
    @Bean
    @ConditionalOnProperty(value = "badley.ai.tools.openweather.token")
    public OpenWeatherMapClient openWeatherClient(OpenWeatherProperties properties) {
        Objects.requireNonNull(properties.getToken(),
            "OpenWeather token must not be null. Please check your configuration.");
        return new DefaultOpenWeatherMapClient(properties.getToken());
    }
}

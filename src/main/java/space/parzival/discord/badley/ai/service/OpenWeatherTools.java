package space.parzival.discord.badley.ai.service;

import io.github.mbenincasa.javaopenweathermapclient.client.OpenWeatherMapClient;
import io.github.mbenincasa.javaopenweathermapclient.request.common.Unit;
import io.github.mbenincasa.javarestclient.exception.RestClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.openweather.token")
@AllArgsConstructor
public class OpenWeatherTools implements AiTools {
    private final OpenWeatherMapClient openWeatherClient;

    private static final String WEATHER_DAY_TEMPLATE = """
        Forecast for ${location} [${country}] on ${date}:
        - Temperature: ${temperature} °C
        - Feels Like: ${feels_like} °C
        - Temperature Min: ${temperature_min} °C
        - Temperature Max: ${temperature_max} °C
        - Temperature KF: ${temperature_kf} °C
        - Pressure: ${pressure} hPa
        - Humidity: ${humidity}%
        - Wind Speed: ${wind_speed} m/s
        - Wind Direction: ${wind_direction}°
        
        Weather Descriptions:
        ${weather_descriptions}
        """.stripIndent();

    @Tool(description = "Get the current weather for a given location.")
    public String getCurrentWeather(String location) {
        log.debug("AI is requesting current weather for location: {}", location);

        try {
            var resp = openWeatherClient.currentWeather()
                .cityName(location, null, null)
                .units(Unit.METRIC)
                .response();

            Map<String, Object> weatherData = new HashMap<>();
            weatherData.put("location", resp.getName());
            weatherData.put("country", resp.getSys().getCountry());
            weatherData.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            weatherData.put("temperature", resp.getMain().getTemp());
            weatherData.put("feels_like", resp.getMain().getFeelsLike());
            weatherData.put("temperature_min", resp.getMain().getTempMin());
            weatherData.put("temperature_max", resp.getMain().getTempMax());
            weatherData.put("temperature_kf", resp.getMain().getTempKf());
            weatherData.put("pressure", resp.getMain().getPressure());
            weatherData.put("humidity", resp.getMain().getHumidity());
            weatherData.put("wind_speed", resp.getWind().getSpeed());
            weatherData.put("wind_direction", resp.getWind().getDeg());
            weatherData.put("weather_descriptions", resp.getWeather().stream()
                .map(weather -> String.format("- %s: %s", weather.getMain(), weather.getDescription()))
                .reduce((a, b) -> a + "\n" + b).orElse("No description available"));

            return StringSubstitutor.replace(WEATHER_DAY_TEMPLATE, weatherData);
        } catch (RestClientException e) {
            log.error("Error fetching current weather: {}", e.getMessage());
            return "Error fetching current weather: " + e.getMessage();
        }
    }

    @Tool(description = "Get the 5 day forecast for a given location.")
    public String getForecast(String location) {
        log.debug("AI is requesting 5 day forecast for location: {}", location);

        try {
            var resp = openWeatherClient.fiveDaysWeatherForecast()
                .cityName(location, null, null)
                .units(Unit.METRIC)
                .response();

            return resp.getList().stream().map(forecast -> {
                    Map<String, Object> forecastData = new HashMap<>();
                    forecastData.put("location", resp.getCity().getName());
                    forecastData.put("country", resp.getCity().getCountry());
                    forecastData.put("date", forecast.getDtTxt());
                    forecastData.put("temperature", forecast.getMain().getTemp());
                    forecastData.put("feels_like", forecast.getMain().getFeelsLike());
                    forecastData.put("temperature_min", forecast.getMain().getTempMin());
                    forecastData.put("temperature_max", forecast.getMain().getTempMax());
                    forecastData.put("temperature_kf", forecast.getMain().getTempKf());
                    forecastData.put("pressure", forecast.getMain().getPressure());
                    forecastData.put("humidity", forecast.getMain().getHumidity());
                    forecastData.put("wind_speed", forecast.getWind().getSpeed());
                    forecastData.put("wind_direction", forecast.getWind().getDeg());
                    forecastData.put("weather_descriptions", forecast.getWeather().stream()
                        .map(weather -> String.format("- %s: %s", weather.getMain(), weather.getDescription()))
                        .reduce((a, b) -> a + "\n" + b).orElse("No description available"));
                    return StringSubstitutor.replace(WEATHER_DAY_TEMPLATE, forecastData);
                })
                .collect(Collectors.joining("\n\n"));
        } catch (RestClientException e) {
            log.error("Error fetching forecast: {}", e.getMessage());
            return "Error fetching forecast: " + e.getMessage();
        }
    }
}

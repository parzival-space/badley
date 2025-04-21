package space.parzival.discord.badley.ai.service;

import io.github.mbenincasa.javaopenweathermapclient.client.OpenWeatherMapClient;
import io.github.mbenincasa.javaopenweathermapclient.request.common.Unit;
import io.github.mbenincasa.javarestclient.exception.RestClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.openweather.token")
@AllArgsConstructor
public class OpenWeatherTools implements AiToolsService {
    private final OpenWeatherMapClient openWeatherClient;

    private static final String WEATHER_DAY_TEMPLATE = """
        Forecast for %s [%s] on %s:
        - Temperature: %.2f °C
        - Feels Like: %.2f °C
        - Temperature Min: %.2f °C
        - Temperature Max: %.2f °C
        - Temperature KF: %.2f °C
        - Pressure: %d hPa
        - Humidity: %d%%
        - Wind Speed: %.2f m/s
        - Wind Direction: %d°
        
        Weather Descriptions:
        %s
        """;

    @Tool(description = "Get the current weather for a given location.")
    public String getCurrentWeather(String location) {
        log.debug("AI is requesting current weather for location: {}", location);

        try {
            var resp = openWeatherClient.currentWeather()
                    .cityName(location, null, null)
                    .units(Unit.METRIC)
                    .response();

            return String.format(WEATHER_DAY_TEMPLATE,
                    resp.getName(), resp.getSys().getCountry(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    resp.getMain().getTemp(), resp.getMain().getFeelsLike(), resp.getMain().getTempMin(),
                    resp.getMain().getTempMax(), resp.getMain().getTempKf(), resp.getMain().getPressure(),
                    resp.getMain().getHumidity(), resp.getWind().getSpeed(), resp.getWind().getDeg(),
                    resp.getWeather().stream()
                            .map(weather -> String.format("- %s: %s", weather.getMain(), weather.getDescription()))
                            .reduce((a, b) -> a + "\n" + b).orElse("No description available"));
        } catch(RestClientException e) {
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

            return resp.getList().stream().map(forecast ->
                    String.format(WEATHER_DAY_TEMPLATE,
                            resp.getCity().getName(), resp.getCity().getCountry(), forecast.getDtTxt(),
                            forecast.getMain().getTemp(), forecast.getMain().getFeelsLike(),
                            forecast.getMain().getTempMin(), forecast.getMain().getTempMax(),
                            forecast.getMain().getTempKf(), forecast.getMain().getPressure(),
                            forecast.getMain().getHumidity(), forecast.getWind().getSpeed(),
                            forecast.getWind().getDeg(),
                            forecast.getWeather().stream()
                                    .map(weather -> String.format("- %s: %s", weather.getMain(), weather.getDescription()))
                                    .reduce((a, b) -> a + "\n" + b).orElse("No description available")
                    )
            ).collect(Collectors.joining("\n\n"));
        } catch(RestClientException e) {
            log.error("Error fetching forecast: {}", e.getMessage());
            return "Error fetching forecast: " + e.getMessage();
        }
    }
}

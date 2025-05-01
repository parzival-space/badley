package space.parzival.discord.badley.ai.service;

import io.github.mbenincasa.javaopenweathermapclient.client.OpenWeatherMapClient;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.CurrentWeatherDTO;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.FiveDaysWeatherForecastDTO;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.common.Main;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.common.Weather;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.common.Wind;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.currentWeather.Sys;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.fiveDaysWeatherForecast.City;
import io.github.mbenincasa.javaopenweathermapclient.dto.response.fiveDaysWeatherForecast.ForecastList;
import io.github.mbenincasa.javaopenweathermapclient.request.currentWeather.CurrentWeatherRequest;
import io.github.mbenincasa.javaopenweathermapclient.request.weatherForecast.FiveDaysWeatherForecastRequest;
import io.github.mbenincasa.javarestclient.exception.RestClientException;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.ai.tools.service.OpenWeatherTools;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenWeatherToolsTest {

    @Test
    void getCurrentWeather_returns_validData() throws RestClientException {
        CurrentWeatherDTO currentWeatherDTO = createCurrentWeatherDTO();
        OpenWeatherMapClient openWeatherMapClient = mock(OpenWeatherMapClient.class);
        CurrentWeatherRequest currentWeatherRequest = mock(CurrentWeatherRequest.class);
        CurrentWeatherRequest.Builder currentWeatherRequestBuilder = mock(CurrentWeatherRequest.Builder.class);

        when(openWeatherMapClient.currentWeather()).thenReturn(currentWeatherRequest);
        when(currentWeatherRequest.cityName("Mock City", null, null)).thenReturn(currentWeatherRequestBuilder);
        when(currentWeatherRequestBuilder.units(any())).thenReturn(currentWeatherRequestBuilder);
        when(currentWeatherRequestBuilder.response()).thenReturn(currentWeatherDTO);

        OpenWeatherTools openWeatherTools = new OpenWeatherTools(openWeatherMapClient);
        String result = openWeatherTools.getCurrentWeather("Mock City");

        assertTrue(result.contains("Forecast for Mock City [CC]"));
        assertTrue(result.contains("Temperature: 25.0 °C"));
        assertTrue(result.contains("Feels Like: 26.0 °C"));
        assertTrue(result.contains("Temperature Min: 24.0 °C"));
        assertTrue(result.contains("Temperature Max: 27.0 °C"));
        assertTrue(result.contains("Temperature KF: 0.0 °C"));
        assertTrue(result.contains("Pressure: 1013 hPa"));
        assertTrue(result.contains("Humidity: 60%"));
        assertTrue(result.contains("Wind Speed: 5.0 m/s"));
        assertTrue(result.contains("Wind Direction: 180°"));
        assertTrue(result.contains("- Clear: clear sky"));
    }

    @Test
    void getCurrentWeather_returns_errorMessage() throws RestClientException {
        OpenWeatherMapClient openWeatherMapClient = mock(OpenWeatherMapClient.class);
        CurrentWeatherRequest currentWeatherRequest = mock(CurrentWeatherRequest.class);
        CurrentWeatherRequest.Builder currentWeatherRequestBuilder = mock(CurrentWeatherRequest.Builder.class);

        when(openWeatherMapClient.currentWeather()).thenReturn(currentWeatherRequest);
        when(currentWeatherRequest.cityName("Mock City", null, null)).thenReturn(currentWeatherRequestBuilder);
        when(currentWeatherRequestBuilder.units(any())).thenReturn(currentWeatherRequestBuilder);
        when(currentWeatherRequestBuilder.response()).thenThrow(new RestClientException("Error fetching weather"));

        OpenWeatherTools openWeatherTools = new OpenWeatherTools(openWeatherMapClient);
        String result = openWeatherTools.getCurrentWeather("Mock City");
        assertTrue(result.contains("Error fetching weather"));
    }

    @Test
    void getForecast_returns_validData() throws RestClientException {
        FiveDaysWeatherForecastDTO forecastDTO = createFiveDaysWeatherDTO();
        OpenWeatherMapClient openWeatherMapClient = mock(OpenWeatherMapClient.class);
        FiveDaysWeatherForecastRequest fiveDaysWeatherForecastRequest = mock(FiveDaysWeatherForecastRequest.class);
        FiveDaysWeatherForecastRequest.Builder fiveDaysWeatherForecastRequestBuilder =
            mock(FiveDaysWeatherForecastRequest.Builder.class);

        when(openWeatherMapClient.fiveDaysWeatherForecast()).thenReturn(fiveDaysWeatherForecastRequest);
        when(fiveDaysWeatherForecastRequest.cityName("Mock City", null, null))
            .thenReturn(fiveDaysWeatherForecastRequestBuilder);
        when(fiveDaysWeatherForecastRequestBuilder.units(any())).thenReturn(fiveDaysWeatherForecastRequestBuilder);
        when(fiveDaysWeatherForecastRequestBuilder.response()).thenReturn(forecastDTO);

        OpenWeatherTools openWeatherTools = new OpenWeatherTools(openWeatherMapClient);
        String result = openWeatherTools.getForecast("Mock City");

        assertTrue(result.contains("Forecast for Mock City [CC]"));
        assertTrue(result.contains("Temperature: 25.0 °C"));
        assertTrue(result.contains("Feels Like: 26.0 °C"));
        assertTrue(result.contains("Temperature Min: 24.0 °C"));
        assertTrue(result.contains("Temperature Max: 27.0 °C"));
        assertTrue(result.contains("Temperature KF: 0.0 °C"));
        assertTrue(result.contains("Pressure: 1013 hPa"));
        assertTrue(result.contains("Humidity: 60%"));
        assertTrue(result.contains("Wind Speed: 5.0 m/s"));
        assertTrue(result.contains("Wind Direction: 180°"));
        assertTrue(result.contains("- Clear: clear sky"));
    }

    @Test
    void getForecast_returns_errorMessage() throws RestClientException {
        OpenWeatherMapClient openWeatherMapClient = mock(OpenWeatherMapClient.class);
        FiveDaysWeatherForecastRequest fiveDaysWeatherForecastRequest = mock(FiveDaysWeatherForecastRequest.class);
        FiveDaysWeatherForecastRequest.Builder fiveDaysWeatherForecastRequestBuilder =
            mock(FiveDaysWeatherForecastRequest.Builder.class);

        when(openWeatherMapClient.fiveDaysWeatherForecast()).thenReturn(fiveDaysWeatherForecastRequest);
        when(fiveDaysWeatherForecastRequest.cityName("Mock City", null, null))
            .thenReturn(fiveDaysWeatherForecastRequestBuilder);
        when(fiveDaysWeatherForecastRequestBuilder.units(any())).thenReturn(fiveDaysWeatherForecastRequestBuilder);
        when(fiveDaysWeatherForecastRequestBuilder.response()).thenThrow(new RestClientException("Error fetching forecast"));

        OpenWeatherTools openWeatherTools = new OpenWeatherTools(openWeatherMapClient);
        String result = openWeatherTools.getForecast("Mock City");

        assertTrue(result.contains("Error fetching forecast"));
    }

    private CurrentWeatherDTO createCurrentWeatherDTO() {
        CurrentWeatherDTO currentWeatherDTO = mock(CurrentWeatherDTO.class);
        Main main = createMain();
        Wind wind = createWind();
        Sys sys = createSys();
        List<Weather> weatherList = List.of(createWeather());
        when(currentWeatherDTO.getName()).thenReturn("Mock City");
        when(currentWeatherDTO.getSys()).thenReturn(sys);
        when(currentWeatherDTO.getMain()).thenReturn(main);
        when(currentWeatherDTO.getWind()).thenReturn(wind);
        when(currentWeatherDTO.getWeather()).thenReturn(weatherList);
        return currentWeatherDTO;
    }

    private FiveDaysWeatherForecastDTO createFiveDaysWeatherDTO() {
        FiveDaysWeatherForecastDTO forecastDTO = mock(FiveDaysWeatherForecastDTO.class);
        City city = createCity();
        List<ForecastList> forecastList = List.of(createForecastList());
        when(forecastDTO.getList()).thenReturn(forecastList);
        when(forecastDTO.getCity()).thenReturn(city);
        return forecastDTO;
    }

    private City createCity() {
        City city = mock(City.class);
        when(city.getName()).thenReturn("Mock City");
        when(city.getCountry()).thenReturn("CC");
        return city;
    }

    private ForecastList createForecastList() {
        ForecastList forecastList = mock(ForecastList.class);
        Main main = createMain();
        Wind wind = createWind();
        List<Weather> weatherList = List.of(createWeather());
        when(forecastList.getDtTxt()).thenReturn("2023-10-01 12:00:00");
        when(forecastList.getMain()).thenReturn(main);
        when(forecastList.getWind()).thenReturn(wind);
        when(forecastList.getWeather()).thenReturn(weatherList);
        return forecastList;
    }

    private Main createMain() {
        Main main = mock(Main.class);
        when(main.getTemp()).thenReturn(25.0);
        when(main.getFeelsLike()).thenReturn(26.0);
        when(main.getTempMin()).thenReturn(24.0);
        when(main.getTempMax()).thenReturn(27.0);
        when(main.getTempKf()).thenReturn(0.0);
        when(main.getPressure()).thenReturn(1013);
        when(main.getHumidity()).thenReturn(60);
        return main;
    }

    private Sys createSys() {
        Sys sys = mock(Sys.class);
        when(sys.getCountry()).thenReturn("CC");
        return sys;
    }

    private Wind createWind() {
        Wind wind = mock(Wind.class);
        when(wind.getSpeed()).thenReturn(5.0);
        when(wind.getDeg()).thenReturn(180);
        return wind;
    }

    private Weather createWeather() {
        Weather weather = mock(Weather.class);
        when(weather.getMain()).thenReturn("Clear");
        when(weather.getDescription()).thenReturn("clear sky");
        return weather;
    }
}

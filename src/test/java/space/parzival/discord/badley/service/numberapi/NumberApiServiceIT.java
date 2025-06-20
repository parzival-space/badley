package space.parzival.discord.badley.service.numberapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import space.parzival.discord.badley.configuration.properties.tools.IpApiProperties;
import space.parzival.discord.badley.configuration.properties.tools.NumberApiProperties;
import space.parzival.discord.badley.service.ipapi.IpApiService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@EnableConfigurationProperties(NumberApiProperties.class)
@RestClientTest(value = NumberApiService.class, properties = "badley.ai.tools.number-api.enabled=true")
class NumberApiServiceIT {
    @Autowired
    private NumberApiService service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void getTrivia() {
        server.expect(request -> request.getMethod().equals("GET"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/numberapi/trivia/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        var response = service.getTrivia(187);

        assertNotNull(response);
        assertEquals("187 is 187th Street in the Washington Heights section of Manhattan.", response.getText());
        assertTrue(response.isFound());
    }

    @Test
    void getMath() {
        server.expect(request -> request.getMethod().equals("GET"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/numberapi/math/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        var response = service.getMath(187);

        assertNotNull(response);
        assertEquals("187 is a square-free number.", response.getText());
        assertTrue(response.isFound());
    }

    @Test
    void getDate() {
        server.expect(request -> request.getMethod().equals("GET"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/numberapi/date/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        var response = service.getDate(47);

        assertNotNull(response);
        assertEquals(1985, response.getYear());
        assertEquals("February 16th is the day in 1985 that Hezbollah is founded.", response.getText());
        assertTrue(response.isFound());
    }

    @Test
    void getYear() {
        server.expect(request -> request.getMethod().equals("GET"))
            .andRespond(withSuccess(
                resourceLoader.getResource("classpath:mock/numberapi/year/valid-response.json"),
                MediaType.APPLICATION_JSON
            ));

        var response = service.getYear(2002);

        assertNotNull(response);
        assertEquals(2002, response.getNumber());
        assertEquals("2002 is the year that the eruption of Mount Nyiragongo in the Democratic Republic of the Congo displaces an estimated 400,000 people on January 17th.", response.getText());
        assertTrue(response.isFound());
    }
}

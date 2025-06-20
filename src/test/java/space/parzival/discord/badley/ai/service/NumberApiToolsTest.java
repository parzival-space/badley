package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.numberapi.NumberApiService;
import space.parzival.discord.badley.service.numberapi.model.NumberApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NumberApiToolsTest {
    private NumberApiTools numberApiTools;
    private NumberApiService numberApiService;

    @BeforeEach
    void setUp() {
        numberApiService = mock(NumberApiService.class);
        numberApiTools = new NumberApiTools(numberApiService);
    }

    @Test
    void getTrivia() {
        String mockResult = "Hello World!";
        when(numberApiService.getTrivia(187)).thenReturn(NumberApiResponse.builder()
            .found(true)
            .text(mockResult)
            .build());

        String result = numberApiTools.getTrivia(187);

        assertNotNull(result);
        assertEquals(mockResult, result);
    }

    @Test
    void getMath() {
        String mockResult = "Math fact about 42.";
        when(numberApiService.getMath(42)).thenReturn(NumberApiResponse.builder()
            .found(true)
            .text(mockResult)
            .build());

        String result = numberApiTools.getMath(42);

        assertNotNull(result);
        assertEquals(mockResult, result);
    }

    @Test
    void getDate() {
        String mockResult = "Date information for 47.";
        when(numberApiService.getDate(47)).thenReturn(NumberApiResponse.builder()
            .found(true)
            .text(mockResult)
            .build());

        String result = numberApiTools.getDate(47);

        assertNotNull(result);
        assertEquals(mockResult, result);
    }

    @Test
    void getYear() {
        String mockResult = "Year information for 2023.";
        when(numberApiService.getYear(2023)).thenReturn(NumberApiResponse.builder()
            .found(true)
            .text(mockResult)
            .build());

        String result = numberApiTools.getYear(2023);

        assertNotNull(result);
        assertEquals(mockResult, result);
    }
}

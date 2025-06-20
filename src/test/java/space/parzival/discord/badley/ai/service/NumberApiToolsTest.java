package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.service.numberapi.NumberApiService;
import space.parzival.discord.badley.service.numberapi.model.NumberApiResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void getTrivia_returns_validData() {
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
    void getTrivia_returns_notFound() {
        when(numberApiService.getTrivia(999)).thenReturn(NumberApiResponse.builder()
            .found(false)
            .text("No trivia found for the number 999.")
            .build());

        String result = numberApiTools.getTrivia(999);

        assertNotNull(result);
        assertEquals("No trivia found for the number 999.", result);
    }

    @Test
    void getTrivia_handles_exception() {
        when(numberApiService.getTrivia(42)).thenThrow(new RuntimeException("API error"));

        String result = numberApiTools.getTrivia(42);

        assertNotNull(result);
        assertEquals("An error occurred while fetching trivia for the number 42.", result);
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
    void getMath_handles_notFound() {
        when(numberApiService.getMath(999)).thenReturn(NumberApiResponse.builder()
            .found(false)
            .text("No math fact found for the number 999.")
            .build());

        String result = numberApiTools.getMath(999);

        assertNotNull(result);
        assertEquals("No math fact found for the number 999.", result);
    }

    @Test
    void getMath_handles_exception() {
        when(numberApiService.getMath(42)).thenThrow(new RuntimeException("API error"));

        String result = numberApiTools.getMath(42);

        assertNotNull(result);
        assertEquals("An error occurred while fetching math fact for the number 42.", result);
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
    void getDate_handles_notFound() {
        when(numberApiService.getDate(999)).thenReturn(NumberApiResponse.builder()
            .found(false)
            .text("No date information found for the number 999.")
            .build());

        String result = numberApiTools.getDate(999);

        assertNotNull(result);
        assertEquals("No date information found for the number 999.", result);
    }

    @Test
    void getDate_handles_exception() {
        when(numberApiService.getDate(42)).thenThrow(new RuntimeException("API error"));

        String result = numberApiTools.getDate(42);

        assertNotNull(result);
        assertEquals("An error occurred while fetching date information for the number 42.", result);
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

    @Test
    void getYear_handles_notFound() {
        when(numberApiService.getYear(999)).thenReturn(NumberApiResponse.builder()
            .found(false)
            .text("No year information found for the number 999.")
            .build());

        String result = numberApiTools.getYear(999);

        assertNotNull(result);
        assertEquals("No year information found for the number 999.", result);
    }

    @Test
    void getYear_handles_exception() {
        when(numberApiService.getYear(42)).thenThrow(new RuntimeException("API error"));

        String result = numberApiTools.getYear(42);

        assertNotNull(result);
        assertEquals("An error occurred while fetching year information for the number 42.", result);
    }
}

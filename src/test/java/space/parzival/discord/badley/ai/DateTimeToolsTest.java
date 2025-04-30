package space.parzival.discord.badley.ai;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.ai.generic.DateTimeTools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeToolsTest {

    @Test
    void getCurrentDateTime_shouldReturn_currentDateTime() {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        DateTimeTools dateTimeTools = new DateTimeTools();
        String result = dateTimeTools.getCurrentDateTime();

        // ignore milliseconds
        assertEquals(currentDateTime.substring(0, 19), result.substring(0, 19));
    }

    @Test
    void getCurrentDate_shouldReturn_currentDate() {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        DateTimeTools dateTimeTools = new DateTimeTools();
        String result = dateTimeTools.getCurrentDate();

        // ignore milliseconds
        assertEquals(currentDate, result);
    }

    @Test
    void getCurrentTime_shouldReturn_currentTime() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);

        DateTimeTools dateTimeTools = new DateTimeTools();
        String result = dateTimeTools.getCurrentTime();

        // ignore milliseconds
        assertEquals(currentTime.substring(1, 5), result.substring(1, 5));
    }
}

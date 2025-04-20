package space.parzival.discord.badley.ai;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.ai.generic.DateTimeTools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeToolsTest {

    @Test
    void getCurrentDateTime_shouldReturnCurrentDateTime() {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        DateTimeTools dateTimeTools = new DateTimeTools();
        String result = dateTimeTools.getCurrentDateTime();

        // ignore milliseconds
        assertEquals(currentDateTime.substring(0, 19), result.substring(0, 19));
    }
}
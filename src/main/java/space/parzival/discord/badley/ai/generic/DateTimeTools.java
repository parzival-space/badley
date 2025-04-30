package space.parzival.discord.badley.ai.generic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.brave.datetime", havingValue = "true")
public class DateTimeTools implements AiTools {
    @Tool(description = "Get the current date and time in ISO datetime format.")
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Tool(description = "Get the current date in ISO date format.")
    public String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Tool(description = "Get the current time in ISO time format.")
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
    }
}

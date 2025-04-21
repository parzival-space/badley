package space.parzival.discord.badley.ai.generic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DateTimeTools implements AiToolsService {
    @Tool(description = "Get the current date and time in ISO datetime format.")
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}

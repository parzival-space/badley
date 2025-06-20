package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.service.numberapi.NumberApiService;
import space.parzival.discord.badley.service.numberapi.model.NumberApiResponse;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.number-api.enabled", havingValue = "true")
@AllArgsConstructor
public class NumberApiTools implements AiTools {
    private final NumberApiService numberApiService;

    @Tool(description = "Get trivia or a fact about a number.")
    public String getTrivia(int number) {
        log.info("AI is requesting trivia for number: {}", number);

        try {
            NumberApiResponse response = numberApiService.getTrivia(number);
            if (response.isFound()) {
                return response.getText();
            } else {
                return "No trivia found for the number " + number + ".";
            }
        } catch (Exception e) {
            log.error("Error while fetching trivia for number {}: {}", number, e.getMessage(), e);
            return "An error occurred while fetching trivia for the number " + number + ".";
        }
    }

    @Tool(description = "Get a math fact about a number.")
    public String getMath(int number) {
        log.info("AI is requesting math fact for number: {}", number);

        try {
            NumberApiResponse response = numberApiService.getMath(number);
            if (response.isFound()) {
                return response.getText();
            } else {
                return "No math fact found for the number " + number + ".";
            }
        } catch (Exception e) {
            log.error("Error while fetching math fact for number {}: {}", number, e.getMessage(), e);
            return "An error occurred while fetching math fact for the number " + number + ".";
        }
    }

    @Tool(description = "Get date information about a number.")
    public String getDate(int number) {
        log.info("AI is requesting date information for number: {}", number);

        try {
            NumberApiResponse response = numberApiService.getDate(number);
            if (response.isFound()) {
                return response.getText();
            } else {
                return "No date information found for the number " + number + ".";
            }
        } catch (Exception e) {
            log.error("Error while fetching date information for number {}: {}", number, e.getMessage(), e);
            return "An error occurred while fetching date information for the number " + number + ".";
        }
    }

    @Tool(description = "Get year information about a number.")
    public String getYear(int number) {
        log.info("AI is requesting year information for number: {}", number);

        try {
            NumberApiResponse response = numberApiService.getYear(number);
            if (response.isFound()) {
                return response.getText();
            } else {
                return "No year information found for the number " + number + ".";
            }
        } catch (Exception e) {
            log.error("Error while fetching year information for number {}: {}", number, e.getMessage(), e);
            return "An error occurred while fetching year information for the number " + number + ".";
        }
    }
}

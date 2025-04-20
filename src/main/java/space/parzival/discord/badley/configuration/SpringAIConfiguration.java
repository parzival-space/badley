package space.parzival.discord.badley.configuration;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.ai.DateTimeTools;
import space.parzival.discord.badley.configuration.properties.AiProperties;

@Configuration
@AllArgsConstructor
public class SpringAIConfiguration {
    private ChatClient.Builder chatClientBuilder;
    private ChatMemory chatMemory;
    private AiProperties aiProperties;

    // ai tools
    private final DateTimeTools aiDateTimeTools;

    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultSystem(aiProperties.getPersonality())
                .defaultTools(aiDateTimeTools)
                .build();
    }
}

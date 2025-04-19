package space.parzival.discord.badley.configuration;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.ai.DateTimeTools;

@Configuration
@AllArgsConstructor
public class SpringAIConfiguration {
    private ChatClient.Builder chatClientBuilder;
    private ChatMemory chatMemory;

    // ai tools
    private final DateTimeTools dateTimeTools;

    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultTools(dateTimeTools)
                .build();
    }
}

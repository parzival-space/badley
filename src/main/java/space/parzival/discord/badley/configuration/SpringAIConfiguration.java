package space.parzival.discord.badley.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.configuration.properties.AiProperties;

import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
public class SpringAIConfiguration {
    // ai tools
    private final List<? extends AiTools> aiToolsServices;
    private ChatClient.Builder chatClientBuilder;
    private ChatMemory chatMemory;
    private AiProperties aiProperties;

    @Bean
    public ChatClient chatClient() {
        log.info("Registering {} AI tools", aiToolsServices.size());
        return chatClientBuilder
            .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
            .defaultSystem(aiProperties.getPersonality())
            .defaultTools(aiToolsServices.toArray())
            .build();
    }
}

package space.parzival.discord.badley.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import space.parzival.discord.badley.ai.generic.DateTimeTools;
import space.parzival.discord.badley.configuration.properties.AiProperties;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SpringAIConfigurationTest {

    @Test
    void chatClient_doesNotThrow() {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(mock(ChatModel.class));
        ChatMemory chatMemory = mock(ChatMemory.class);
        AiProperties aiProperties = new AiProperties("TestAI", "Test personality");

        SpringAIConfiguration springAIConfiguration = new SpringAIConfiguration(chatClientBuilder, chatMemory, aiProperties, List.of());

        assertDoesNotThrow(() -> {
            ChatClient chatClient = springAIConfiguration.chatClient();
            assertNotNull(chatClient);
        });
    }
}
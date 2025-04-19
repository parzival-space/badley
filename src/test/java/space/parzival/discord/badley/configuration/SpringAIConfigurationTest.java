package space.parzival.discord.badley.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import space.parzival.discord.badley.ai.DateTimeTools;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SpringAIConfigurationTest {

    @Test
    void chatClient_doesNotThrow() {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(mock(ChatModel.class));
        ChatMemory chatMemory = mock(ChatMemory.class);

        // ai tools
        DateTimeTools dateTimeTools = mock(DateTimeTools.class);

        SpringAIConfiguration springAIConfiguration = new SpringAIConfiguration(chatClientBuilder, chatMemory, dateTimeTools);

        assertDoesNotThrow(() -> {
            ChatClient chatClient = springAIConfiguration.chatClient();
            assertNotNull(chatClient);
        });
    }
}
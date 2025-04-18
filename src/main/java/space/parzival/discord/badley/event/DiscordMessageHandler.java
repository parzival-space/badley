package space.parzival.discord.badley.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import space.parzival.discord.badley.adapter.DiscordConversationPersistenceAdapter;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class DiscordMessageHandler extends ListenerAdapter {
    private ChatClient chatClient;
    private DiscordConversationPersistenceAdapter conversationPersistenceAdapter;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (shouldIgnoreMessage(event.getMessage())) return;

        UUID conversationId = event.getMessage().getMessageReference() != null
                ? conversationPersistenceAdapter.getConversationByDiscordId(
                        event.getMessage().getMessageReference().getMessageId())
                : UUID.randomUUID();

        // Handle the message received event
        event.getChannel().sendTyping().queue();
        String response = this.chatClient.prompt()
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId.toString()))
                .messages(
                        new UserMessage(
                                event.getMessage().getContentRaw(),
                                event.getMessage().getAttachments().stream().map(attachment -> {
                                    try {
                                        return Media.builder()
                                                .id(attachment.getId())
                                                .data(new URI(attachment.getUrl()).toURL())
                                                .mimeType(MimeType.valueOf(attachment.getContentType() != null ?
                                                        attachment.getContentType() : "application/octet-stream"))
                                                .build();
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }).toList()
                        )
                )
                .call()
                .content();

        if (response == null || response.isEmpty()) {
            log.error("Received empty response from chat client. Request: {}", event.getMessage().getContentRaw());
            event.getMessage().reply("Sorry, I couldn't process your request.").queue();
            return;
        }

        event.getMessage().reply(response).queue(respMessage -> {
            // Save the conversation ID to the database
            conversationPersistenceAdapter.assignDiscordIdToConversationId(respMessage.getId(), conversationId);
        });
    }

    private boolean shouldIgnoreMessage(Message message) {
        return message.getAuthor().isBot() || // self or other bots
                message.getAuthor().isSystem() || // system messages
                message.getMentions().getUsers().stream().noneMatch(
                        u -> message.getJDA().getSelfUser().getId().equals(u.getId()));
    }
}

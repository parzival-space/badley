package space.parzival.discord.badley.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.mapper.DiscordAttachmentMapper;
import space.parzival.discord.badley.persistence.DiscordConversationPersistenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class DiscordMessageHandler extends ListenerAdapter {
    private final ChatClient chatClient;
    private final DiscordConversationPersistenceService discordPersistence;
    private final DiscordAttachmentMapper discordAttachmentMapper;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot() || event.getMessage().getAuthor().isSystem()) return;

        boolean isDirectMessage = event.getChannelType() == ChannelType.PRIVATE ||
            event.getChannelType() == ChannelType.GROUP;

        if (!isDirectMessage && event.getMessage().getMentions().getUsers().stream()
            .noneMatch(user -> user.getId().equals(event.getJDA().getSelfUser().getId()))) return;

        // send typing indicator to the channel
        event.getChannel().sendTyping().queue();

        // determine conversation reference based on channel type
        String conversationReference =
            (isDirectMessage) ? event.getChannel().getId() :
                Optional.ofNullable(event.getMessage().getMessageReference())
                    .map(MessageReference::getMessageId)
                    .orElse(event.getMessageId());

        UUID conversationId = Optional
            .ofNullable(discordPersistence.getConversationIdByDiscordId(conversationReference))
            .orElse(UUID.randomUUID());

        String aiResponse = chatClient.prompt()
            .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", conversationId.toString()))
            .messages(new UserMessage(
                String.join("\n\n",
                    String.format("%s: %s",
                        event.getMessage().getAuthor().getEffectiveName(),
                        event.getMessage().getContentDisplay()),
                    event.getMessage().getAttachments().stream()
                        .filter(attachment -> !attachment.isImage())
                        .map(discordAttachmentMapper::mapToInvalidMediaString)
                        .collect(Collectors.joining("\n"))),
                event.getMessage().getAttachments().stream()
                    .filter(Message.Attachment::isImage)
                    .map(discordAttachmentMapper::mapToMedia)
                    .filter(Objects::nonNull)
                    .toList()
            ))
            .call()
            .content();

        if (aiResponse != null) {
            // split messages by words into 2000 character chunks (Discord's message limit)
            List<String> messageChunks = getDiscordCompliantMessageChunks(aiResponse);
            log.debug("AI response was split into {} chunks for conversation ID: {}",
                messageChunks.size(), conversationId);

            // send each chunk after each other
            queueChunkedResponse(event, messageChunks, conversationReference, conversationId, 0);
        } else {
            log.error("AI response was null for message: {}", event.getMessage().getContentRaw());
            event.getMessage().reply("Nope! Something hasn't worked here.").queue();
        }
    }

    private void queueChunkedResponse(MessageReceivedEvent event, List<String> remainingChunks,
                                      String conversationReference, UUID conversationId, int iteration) {
        if (remainingChunks.isEmpty()) {
            log.debug("No more chunks to send for conversation ID: {}", conversationId);
            return;
        }

        if (iteration == 0) {
            event.getMessage().reply(remainingChunks.getFirst()).queue(discordResp -> {
                discordPersistence.assignDiscordIdToConversationId(
                    conversationReference != null ? conversationReference : discordResp.getId(),
                    conversationId
                );
                queueChunkedResponse(event, remainingChunks.subList(1, remainingChunks.size()),
                    conversationReference, conversationId, iteration + 1);
            });
        } else {
            event.getChannel().sendMessage(remainingChunks.getFirst()).queue(discordResp -> {
                discordPersistence.assignDiscordIdToConversationId(
                    conversationReference != null ? conversationReference : discordResp.getId(),
                    conversationId
                );
                queueChunkedResponse(event, remainingChunks.subList(1, remainingChunks.size()),
                    conversationReference, conversationId, iteration + 1);
            });
        }
    }

    private static List<String> getDiscordCompliantMessageChunks(String aiResponse) {
        List<String> messageChunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (int i = 0; i < aiResponse.length(); i++) {
            currentChunk.append(aiResponse.charAt(i));
            if (currentChunk.length() == 2000) {
                messageChunks.add(currentChunk.toString());
                currentChunk.setLength(0);
            }
        }

        // Add any remaining characters as a final chunk
        if (!currentChunk.isEmpty()) {
            messageChunks.add(currentChunk.toString());
        }
        return messageChunks;
    }
}

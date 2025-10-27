package space.parzival.discord.badley.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SignalType;
import space.parzival.discord.badley.configuration.properties.behavior.RandomReplyProperties;
import space.parzival.discord.badley.mapper.DiscordAttachmentMapper;
import space.parzival.discord.badley.persistence.DiscordConversationPersistenceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class DiscordMessageHandler extends ListenerAdapter {
    private final ChatClient chatClient;
    private final DiscordConversationPersistenceService discordPersistence;
    private final DiscordAttachmentMapper discordAttachmentMapper;

    private static final int TYPING_INDICATOR_INTERVAL_SECONDS = 5;

    private RandomReplyProperties randomReplyProperties;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot() || event.getMessage().getAuthor().isSystem()) return;

        boolean isDirectMessage = event.getChannelType() == ChannelType.PRIVATE ||
            event.getChannelType() == ChannelType.GROUP;

        boolean isMessageDirectedAtAi = event.getMessage().getMentions().getUsers().stream()
            .anyMatch(user -> user.getId().equals(event.getJDA().getSelfUser().getId()));

        double replyChance = Math.random();
        boolean isRandomReply = randomReplyProperties.isEnabled() && replyChance <= randomReplyProperties.getChance();

        // ignore message if is not a DM, mention or random reply
        if (!isDirectMessage && !isMessageDirectedAtAi && !isRandomReply) return;

        // send typing indicator to the channel
        log.debug("Begin processing ai response for message {} in channel: {}." +
                "Message is DM: {}, is Mention: {}, is random reply: {}",
            event.getMessage().getId(), event.getChannel().getName(), isDirectMessage,
            isMessageDirectedAtAi, isRandomReply);
        event.getChannel().sendTyping().queue();

        // determine conversation reference based on channel type
        String conversationReference = isDirectMessage
            ? event.getChannel().getId()
            : Optional.ofNullable(event.getMessage().getMessageReference())
                    .map(MessageReference::getMessageId)
                    .orElse(event.getMessageId());

        UUID conversationId = Optional
            .ofNullable(discordPersistence.getConversationIdByDiscordId(conversationReference))
            .orElse(UUID.randomUUID());

        ChatClient.StreamResponseSpec responseStream = chatClient.prompt()
            .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", conversationId.toString()))
            .messages(new UserMessage(
                String.join("\n\n",
                    String.format("%s%s wrote: %s",
                        isRandomReply ? "[SYSTEM: you are inserting yourself into this conversation unasked]": "",
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
            .stream();

        AtomicReference<LocalDateTime> lastTypingTimestamp =
            new AtomicReference<>(LocalDateTime.now().minusSeconds(TYPING_INDICATOR_INTERVAL_SECONDS));
        List<ChatResponse> allResponses = new ArrayList<>();

        responseStream.chatResponse()
            .doOnNext(aiResponse -> {
                // collect all responses
                allResponses.add(aiResponse);

                // send typing indicator if last typing update was more than 30 seconds ago
                LocalDateTime now = LocalDateTime.now();
                if (lastTypingTimestamp.get().isBefore(now.minusSeconds(TYPING_INDICATOR_INTERVAL_SECONDS))) {
                    event.getChannel().sendTyping().queue();
                    lastTypingTimestamp.set(LocalDateTime.now());
                }
            })
            .doOnComplete(() -> {
                // collect the final AI response
                String fullAiResponseText = allResponses.stream()
                    .map(ChatResponse::getResult)
                    .map(Generation::getOutput)
                    .map(AssistantMessage::getText)
                    .collect(Collectors.joining());

                long generatedMediaCount = allResponses.stream()
                    .map(ChatResponse::getResult)
                    .map(Generation::getOutput)
                    .mapToLong(assistantMessage -> assistantMessage.getMedia().size())
                    .sum();
                if (generatedMediaCount > 0) {
                    log.warn("AI response contains {} media attachments, which is currently not supported. " +
                            "It is recommended to use text-only models for now to reduce costs. " +
                            "The attachments in this response will be discarded.", generatedMediaCount);
                }

                // split messages by words into 2000 character chunks (Discord's message limit)
                List<String> messageChunks = getDiscordCompliantMessageChunks(fullAiResponseText);
                log.debug("AI response was split into {} chunks for conversation ID: {}",
                    messageChunks.size(), conversationId);

                // send each chunk after each other
                queueChunkedResponse(
                    event,
                    messageChunks,
                    // if this is a direct message, use the conversation reference, otherwise null to instruct the send
                    // function to store the response message ids as references
                    isDirectMessage ? conversationReference : null,
                    conversationId,
                    0);
            })
            .doOnError(throwable -> log.error("Error processing AI response for message: {}",
                event.getMessage().getContentRaw(), throwable))
            .doFinally(signal -> {
                if (signal == SignalType.ON_ERROR || signal == SignalType.CANCEL) {
                    log.error("Stream ended with error or cancellation for message: {}", event.getMessage().getContentRaw());

                    event.getMessage()
                        .reply("Sorry, I am experiencing some issues. Please try again later.")
                        .queue();
                }
            })
            .blockLast();

        log.debug("Finished processing AI response for message: {} in channel: {}",
            event.getMessage().getId(), event.getChannel().getName());
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

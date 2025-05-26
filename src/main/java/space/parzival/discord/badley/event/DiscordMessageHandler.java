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
            event.getMessage().reply(aiResponse).queue(discordResp ->
                discordPersistence.assignDiscordIdToConversationId(isDirectMessage ?
                    conversationReference : discordResp.getId(), conversationId));
        } else {
            log.error("AI response was null for message: {}", event.getMessage().getContentRaw());
            event.getMessage().reply("Nope! Something hasn't worked here.").queue();
        }
    }
}

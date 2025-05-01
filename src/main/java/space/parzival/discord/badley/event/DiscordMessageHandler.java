package space.parzival.discord.badley.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.internal.ConversationContext;
import space.parzival.discord.badley.ai.internal.ToolContextField;
import space.parzival.discord.badley.mapper.DiscordAttachmentMapper;
import space.parzival.discord.badley.persistence.DiscordConversationPersistenceService;

import java.util.Map;
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
        if (botShouldIgnoreMessage(event.getMessage())) return;
        event.getChannel().sendTyping().queue();

        UUID conversationId = Optional
            .ofNullable(discordPersistence.getConversationIdByDiscordId(
                event.getMessage().getMessageReference() != null ?
                    event.getMessage().getMessageReference().getMessageId() :
                    event.getMessage().getId()))
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
            .toolContext(Map.of(
                ToolContextField.CONVERSATION_CONTEXT.toString(), ConversationContext.DISCORD_GUILD_CHANNEL,
                ToolContextField.DISCORD_EVENT.toString(), event
            ))
            .call()
            .content();

        if (aiResponse != null) {
            event.getMessage().reply(aiResponse).queue(discordResp ->
                discordPersistence.assignDiscordIdToConversationId(discordResp.getId(), conversationId));
        } else {
            log.error("AI response was null for message: {}", event.getMessage().getContentRaw());
            event.getMessage().reply("Nope! Something hasn't worked here.").queue();
        }
    }

    private boolean botShouldIgnoreMessage(Message message) {
        return message.getAuthor().isBot() ||
            message.getAuthor().isSystem() ||
            message.getMentions().getUsers().stream().noneMatch(u ->
                u.getId().equals(message.getJDA().getSelfUser().getId()));
    }
}

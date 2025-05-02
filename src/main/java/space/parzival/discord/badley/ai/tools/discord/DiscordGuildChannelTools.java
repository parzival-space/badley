package space.parzival.discord.badley.ai.tools.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.internal.ConversationContext;
import space.parzival.discord.badley.ai.internal.ToolContextField;
import space.parzival.discord.badley.ai.tools.AiTools;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DiscordGuildChannelTools implements AiTools {
    private static final String VOICE_CHANNEL_INFO = """
        ${channel.id}:
        Name: ${channel.name}
        Users currently in channel:
        ${channel.users}
        """.stripIndent();

    @Tool(description = "Disconnects a user identified by its ID from a voice channel.")
    public String disconnectUserFromChannel(
        @ToolParam(description = "Discord Snowflake User ID. Has to be fetched before calling.") String userId,
        ToolContext context) {
        log.debug("AI is requesting to disconnecting user {} from voice channel", userId);

        if (!context.getContext().get(ToolContextField.CONVERSATION_CONTEXT)
            .equals(ConversationContext.DISCORD_GUILD_CHANNEL)) {
            return "Disconnecting users from voice channels is only available in Discord guilds.";
        }

        try {
            MessageReceivedEvent event = (MessageReceivedEvent) context.getContext().get(ToolContextField.DISCORD_EVENT);

            Member member = event.getGuild().getMemberById(userId);
            if (member == null) {
                return "User not found.";
            }

            event.getGuild().kickVoiceMember(member).queue();
            return "User disconnected.";
        } catch (Exception e) {
            log.error("Error disconnecting user from voice channel: {}", e.getMessage(), e);
            return "Error disconnecting user from voice channel: " + e.getMessage();
        }
    }

    @Tool(description = "Lists the Voice Channels of the current server and all connected users.")
    public String inspectVoiceChannels(ToolContext context) {
        log.debug("AI is requesting to inspect voice channels");

        if (!context.getContext().get(ToolContextField.CONVERSATION_CONTEXT)
            .equals(ConversationContext.DISCORD_GUILD_CHANNEL)) {
            return "Inspecting voice channels is only available in Discord guilds.";
        }

        try {
            MessageReceivedEvent event = (MessageReceivedEvent) context.getContext().get(ToolContextField.DISCORD_EVENT);

            String result = event.getGuild().getVoiceChannels().stream()
                .map(channel -> StringSubstitutor.replace(VOICE_CHANNEL_INFO, Map.of(
                    "channel.id", channel.getId(),
                    "channel.name", channel.getName(),
                    "channel.users", channel.getMembers().stream()
                        .map(m -> String.format("- %s (%s) [%s]",
                            m.getEffectiveName(),
                            m.getUser().getName(),
                            m.getId()))
                        .collect(Collectors.joining("\n"))
                ))).collect(Collectors.joining("\n\n"));
            log.info("Voice channels: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error inspecting voice channels: {}", e.getMessage(), e);
            return "Error inspecting voice channels: " + e.getMessage();
        }
    }
}

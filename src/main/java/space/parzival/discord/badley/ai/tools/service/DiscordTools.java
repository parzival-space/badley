package space.parzival.discord.badley.ai.tools.service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.internal.ConversationContext;
import space.parzival.discord.badley.ai.internal.ToolContextField;
import space.parzival.discord.badley.ai.tools.AiTools;

import java.util.List;

@Slf4j
@Component
public class DiscordTools implements AiTools {

    @Tool(description = "Disconnects a user from a voice channel.")
    public String disconnectUserFromChannel(String userId, ToolContext context) {
        log.info("Get information about USER {}", userId);

        if (!context.getContext().get(ToolContextField.CONVERSATION_CONTEXT.toString())
            .equals(ConversationContext.DISCORD_GUILD_CHANNEL.toString())) {
            return "This tool is only available in Discord guilds.";
        }

        try {
            MessageReceivedEvent event = (MessageReceivedEvent) context.getContext()
                .get(ToolContextField.DISCORD_EVENT.toString());

            List<Member> memberList = event.getGuild().findMembers(m -> m.getId().equals(userId)).get();
            if (memberList.isEmpty()) {
                return "User not found.";
            }

            event.getGuild().kickVoiceMember(memberList.getFirst()).queue();
            return "User disconnected.";
        } catch (Exception e) {
            log.error("Error disconnecting user from voice channel: {}", e.getMessage(), e);
            return "Error disconnecting user from voice channel: " + e.getMessage();
        }
    }
}

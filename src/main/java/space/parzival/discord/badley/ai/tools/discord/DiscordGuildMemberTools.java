package space.parzival.discord.badley.ai.tools.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.internal.ConversationContext;
import space.parzival.discord.badley.ai.internal.ToolContextField;
import space.parzival.discord.badley.ai.tools.AiTools;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class DiscordGuildMemberTools implements AiTools {
    private static final String MEMBER_INFO_TEMPLATE = """
        ${member.id}:
        Name: ${member.name}
        Effective Name: ${member.effectiveName}
        Joined: ${member.joinedAt}
        Is Boosting: ${member.boosting}
        In Timeout: ${member.timeout}
        In Voice Channel: ${member.voiceChannel}
        Is Owner: ${member.owner}
        Is Pending: ${member.pending}
        Is Bot: ${member.bot}
        Roles: ${member.roles}
        """.stripIndent();

    @Tool(description = "Lists all currently online members of the Discord guild/server.")
    public String getGuildMembers(ToolContext context) {
        log.debug("AI is requesting to get guild members of conversation server");

        // this tool only works in guild channels
        if (!context.getContext().get(ToolContextField.CONVERSATION_CONTEXT)
            .equals(ConversationContext.DISCORD_GUILD_CHANNEL)) {
            return "Getting guild members is only available in Discord guilds.";
        }

        try {
            MessageReceivedEvent event = (MessageReceivedEvent) context.getContext().get(ToolContextField.DISCORD_EVENT);

            return
                "Current online Members: \n" +
                event.getGuild().getMembers().stream().map(m -> {
                Map<String, Object> vars = new HashMap<>();
                vars.put("member.id", m.getId());
                vars.put("member.name", m.getUser().getName());
                vars.put("member.effectiveName", m.getEffectiveName());
                vars.put("member.joinedAt", m.getTimeJoined().format(DateTimeFormatter.ISO_DATE_TIME));
                vars.put("member.boosting", m.isBoosting() ?
                    "Yes, since " + Optional.ofNullable(m.getTimeBoosted())
                        .map(t -> t.format(DateTimeFormatter.ISO_DATE_TIME)).orElse("N/A") :
                    "No");
                vars.put("member.timeout", m.isTimedOut() ?
                    "Yes, until " + Optional.ofNullable(m.getTimeOutEnd())
                        .map(t -> t.format(DateTimeFormatter.ISO_DATE_TIME)).orElse("N/A") :
                    "No");
                vars.put("member.voiceChannel", Optional.ofNullable(m.getVoiceState())
                    .map(v -> String.join(": ",
                        v.getChannel() != null ? "Yes" : "No",
                        Stream.of(
                                v.isDeafened() ? "Deafened" : null,
                                v.isMuted() ? "Muted" : null,
                                v.isSelfDeafened() ? "Self Deafened" : null,
                                v.isSelfMuted() ? "Self Muted" : null,
                                v.isGuildDeafened() ? "Guild Deafened" : null,
                                v.isGuildMuted() ? "Guild Muted" : null,
                                v.isSendingVideo() ? "Sending Video" : null)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(", "))))
                    .orElse("N/A"));
                vars.put("member.owner", m.isOwner() ? "Yes" : "No");
                vars.put("member.pending", m.isPending() ? "Yes" : "No");
                vars.put("member.bot", m.getUser().isBot() ? "Yes" : "No");
                vars.put("member.roles", m.getRoles().stream().map(Role::getName)
                    .collect(Collectors.joining(", ")));
                log.debug(StringSubstitutor.replace(MEMBER_INFO_TEMPLATE, vars));
                return StringSubstitutor.replace(MEMBER_INFO_TEMPLATE, vars);
            }).collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            log.error("Error getting guild members: {}", e.getMessage(), e);
            return "Error getting guild members: " + e.getMessage();
        }
    }

}

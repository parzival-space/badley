package space.parzival.discord.badley.ai.tools.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.internal.ConversationContext;
import space.parzival.discord.badley.ai.internal.ToolContextField;
import space.parzival.discord.badley.ai.tools.AiTools;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class DiscordGuildInfoTools implements AiTools {
    private static final String GUILD_INFO_TEMPLATE = """
        ${guild.id}:
        - Name: ${guild.name}
        - Description: ${guild.description}
        - Owner: ${guild.owner}
        - AFK Timeout: ${guild.afkTimeout}
        - AFK Channel: ${guild.afkChannel}
        - Verification Level: ${guild.verificationLevel}
        - Explicit Content Filter: ${guild.explicitContentFilter}
        - Max Members: ${guild.maxMembers}
        - Vanity Invite URL: ${guild.vanityUrlCode}
        - Boost Level: ${guild.premiumTier}
        - Boost Count: ${guild.premiumSubscriptionCount}
        - Preferred Locale: ${guild.preferredLocale}
        - Approximate Member Count: ${guild.approximateMemberCount}
        - NSFW Level: ${guild.nsfwLevel}
        """.stripIndent();

    @Tool(description = "Gets information about the current Discord guild/server.")
    public String getCurrentGuildInfo(ToolContext context) {
        log.debug("AI is requesting to get current guild info");

        // this tool only works in guild channels
        if (!context.getContext().get(ToolContextField.CONVERSATION_CONTEXT)
            .equals(ConversationContext.DISCORD_GUILD_CHANNEL)) {
            return "Getting guild info is only available in Discord guilds.";
        }

        try {
            MessageReceivedEvent event = (MessageReceivedEvent) context.getContext().get(ToolContextField.DISCORD_EVENT);

            Guild guild = event.getGuild();

            Map<String, Object> guildInfoMap = new HashMap<>();
            guildInfoMap.put("guild.id", guild.getId());
            guildInfoMap.put("guild.name", guild.getName());
            guildInfoMap.put("guild.description", Optional.ofNullable(guild.getDescription()).orElse("N/A"));
            guildInfoMap.put("guild.owner", Optional.ofNullable(guild.getOwner())
                .map(m -> String.format("%s (%s) [%s]",
                    m.getEffectiveName(),
                    m.getUser().getName(),
                    m.getId()))
                .orElse("N/A"));
            guildInfoMap.put("guild.afkTimeout", guild.getAfkTimeout().getSeconds() + " seconds");
            guildInfoMap.put("guild.afkChannel", Optional.ofNullable(guild.getAfkChannel())
                .map(c -> String.format("%s (%s)",
                    c.getName(),
                    c.getId()))
                .orElse("N/A"));
            guildInfoMap.put("guild.verificationLevel", guild.getVerificationLevel().name());
            guildInfoMap.put("guild.explicitContentFilter", guild.getExplicitContentLevel().name());
            guildInfoMap.put("guild.maxMembers", guild.getMaxMembers());
            guildInfoMap.put("guild.vanityUrlCode", Optional.ofNullable(guild.getVanityUrl()).orElse("N/A"));
            guildInfoMap.put("guild.premiumTier", guild.getBoostTier().name());
            guildInfoMap.put("guild.premiumSubscriptionCount", guild.getBoostCount());
            guildInfoMap.put("guild.preferredLocale", String.format("%s (%s)",
                guild.getLocale().getNativeName(),
                guild.getLocale().getLanguageName()));
            guildInfoMap.put("guild.approximateMemberCount", guild.getMemberCount());
            guildInfoMap.put("guild.nsfwLevel", guild.getNSFWLevel().name());

            return StringSubstitutor.replace(GUILD_INFO_TEMPLATE, guildInfoMap);
        } catch (Exception e) {
            log.error("Error getting guild info: {}", e.getMessage(), e);
            return "Error getting guild info: " + e.getMessage();
        }
    }
}

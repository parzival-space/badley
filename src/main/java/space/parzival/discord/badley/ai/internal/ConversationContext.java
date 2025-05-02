package space.parzival.discord.badley.ai.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConversationContext {
    public static final String DISCORD_DIRECT_MESSAGE = "discord_direct_message";
    public static final String DISCORD_GUILD_CHANNEL = "discord_guild_channel";
}

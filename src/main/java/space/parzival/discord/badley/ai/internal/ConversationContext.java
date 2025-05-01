package space.parzival.discord.badley.ai.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConversationContext {
    DISCORD_DIRECT_MESSAGE("discord_direct_message"),
    DISCORD_GUILD_CHANNEL("discord_guild_channel");

    private final String contextString;

    @Override
    public String toString() {
        return contextString;
    }
}

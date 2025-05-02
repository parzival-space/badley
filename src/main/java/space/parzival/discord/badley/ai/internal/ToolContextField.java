package space.parzival.discord.badley.ai.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolContextField {
    public static final String CONVERSATION_CONTEXT = "conversation_context";
    public static final String DISCORD_EVENT = "discord_event";
}

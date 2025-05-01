package space.parzival.discord.badley.ai.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ToolContextField {
    CONVERSATION_CONTEXT("conversation_context"),
    DISCORD_EVENT("discord_event");

    private final String fieldName;

    @Override
    public String toString() {
        return fieldName;
    }
}

package space.parzival.discord.badley.adapter;

import java.util.UUID;

public interface DiscordConversationPersistenceAdapter {
    public UUID getConversationIdByDiscordId(String discordId);
    public void assignDiscordIdToConversationId(String discordId, UUID conversationId);
}

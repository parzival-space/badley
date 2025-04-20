package space.parzival.discord.badley.persistence;

import java.util.UUID;

public interface DiscordConversationPersistenceService {
    public UUID getConversationIdByDiscordId(String discordId);
    public void assignDiscordIdToConversationId(String discordId, UUID conversationId);
}

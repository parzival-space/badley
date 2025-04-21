package space.parzival.discord.badley.persistence;

import java.util.UUID;

public interface DiscordConversationPersistenceService {
    UUID getConversationIdByDiscordId(String discordId);

    void assignDiscordIdToConversationId(String discordId, UUID conversationId);
}

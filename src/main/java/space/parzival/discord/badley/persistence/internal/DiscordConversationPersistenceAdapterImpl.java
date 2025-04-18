package space.parzival.discord.badley.persistence.internal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import space.parzival.discord.badley.adapter.DiscordConversationPersistenceAdapter;
import space.parzival.discord.badley.persistence.model.DiscordConversationEntity;
import space.parzival.discord.badley.persistence.repository.DiscordConversationRepository;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DiscordConversationPersistenceAdapterImpl implements DiscordConversationPersistenceAdapter {
    private DiscordConversationRepository repository;

    @Override
    public UUID getConversationByDiscordId(String discordId) {
        Optional<DiscordConversationEntity> entity = repository.findById(discordId);
        return entity
                .map(DiscordConversationEntity::getConversationId)
                .orElse(null);
    }

    @Override
    public void assignDiscordIdToConversationId(String discordId, UUID conversationId) {
        repository.save(DiscordConversationEntity.builder()
                .discordId(discordId)
                .conversationId(conversationId)
                .build());
    }
}

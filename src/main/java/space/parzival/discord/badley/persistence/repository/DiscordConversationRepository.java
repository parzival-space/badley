package space.parzival.discord.badley.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import space.parzival.discord.badley.persistence.model.DiscordConversationEntity;

public interface DiscordConversationRepository extends CrudRepository<DiscordConversationEntity, String> {
}

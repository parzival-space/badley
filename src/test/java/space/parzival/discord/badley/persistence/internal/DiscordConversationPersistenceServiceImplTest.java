package space.parzival.discord.badley.persistence.internal;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.persistence.model.DiscordConversationEntity;
import space.parzival.discord.badley.persistence.repository.DiscordConversationRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscordConversationPersistenceServiceImplTest {
    private final DiscordConversationRepository repo = mock(DiscordConversationRepository.class);

    @Test
    void getConversationIdByDiscordId_shouldReturn_id_ifDataFound() {
        String fakeDiscordId = "fakeDiscordId";
        UUID fakeConversationId = UUID.randomUUID();

        when(repo.findById(fakeDiscordId)).thenReturn(Optional.of(
            DiscordConversationEntity.builder()
                .discordId(fakeDiscordId)
                .conversationId(fakeConversationId)
                .build()
        ));

        DiscordConversationPersistenceServiceImpl adapter = new DiscordConversationPersistenceServiceImpl(repo);

        assertEquals(adapter.getConversationIdByDiscordId(fakeDiscordId), fakeConversationId);
    }

    @Test
    void getConversationIdByDiscordId_shouldReturn_null_ifDataNotFound() {
        String fakeDiscordId = "fakeDiscordId";

        when(repo.findById(fakeDiscordId)).thenReturn(Optional.empty());

        DiscordConversationPersistenceServiceImpl adapter = new DiscordConversationPersistenceServiceImpl(repo);

        assertNull(adapter.getConversationIdByDiscordId(fakeDiscordId));
    }

    @Test
    void assignDiscordIdToConversationId_shouldCall_repositorySave() {
        String fakeDiscordId = "fakeDiscordId";
        UUID fakeConversationId = UUID.randomUUID();

        DiscordConversationPersistenceServiceImpl adapter = new DiscordConversationPersistenceServiceImpl(repo);

        adapter.assignDiscordIdToConversationId(fakeDiscordId, fakeConversationId);

        DiscordConversationEntity expectedEntity = DiscordConversationEntity.builder()
            .discordId(fakeDiscordId)
            .conversationId(fakeConversationId)
            .build();

        // Verify that the repository's save method was called with the expected entity
        verify(repo, times(1)).save(expectedEntity);
    }
}

package space.parzival.discord.badley.persistence.internal;

import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.persistence.model.DiscordConversationEntity;
import space.parzival.discord.badley.persistence.repository.DiscordConversationRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiscordConversationPersistenceAdapterImplTest {
    private DiscordConversationRepository repo = mock(DiscordConversationRepository.class);

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

        DiscordConversationPersistenceAdapterImpl adapter = new DiscordConversationPersistenceAdapterImpl(repo);

        assertEquals(adapter.getConversationIdByDiscordId(fakeDiscordId), fakeConversationId);
    }

    @Test
    void getConversationIdByDiscordId_shouldReturn_null_ifDataNotFound() {
        String fakeDiscordId = "fakeDiscordId";

        when(repo.findById(fakeDiscordId)).thenReturn(Optional.empty());

        DiscordConversationPersistenceAdapterImpl adapter = new DiscordConversationPersistenceAdapterImpl(repo);

        assertNull(adapter.getConversationIdByDiscordId(fakeDiscordId));
    }

    @Test
    void assignDiscordIdToConversationId_shouldCall_repositorySave() {
        String fakeDiscordId = "fakeDiscordId";
        UUID fakeConversationId = UUID.randomUUID();

        DiscordConversationPersistenceAdapterImpl adapter = new DiscordConversationPersistenceAdapterImpl(repo);

        adapter.assignDiscordIdToConversationId(fakeDiscordId, fakeConversationId);

        DiscordConversationEntity expectedEntity = DiscordConversationEntity.builder()
                .discordId(fakeDiscordId)
                .conversationId(fakeConversationId)
                .build();

        // Verify that the repository's save method was called with the expected entity
        verify(repo, times(1)).save(expectedEntity);
    }
}
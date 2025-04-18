package space.parzival.discord.badley.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity(name = "discord_conversation")
@Table(name = "discord_conversation")
@AllArgsConstructor
@NoArgsConstructor
public class DiscordConversationEntity {
    @Id
    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "conversation_id")
    private UUID conversationId;
}

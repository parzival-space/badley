package space.parzival.discord.badley.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import space.parzival.discord.badley.persistence.DiscordConversationPersistenceService;
import space.parzival.discord.badley.mapper.DiscordAttachmentMapper;
import space.parzival.discord.badley.mapper.DiscordAttachmentMapperImpl;

import java.util.List;

import static org.mockito.Mockito.*;

class DiscordMessageHandlerTest {
    private final ChatClient chatClient = mock(ChatClient.class);
    private final DiscordConversationPersistenceService adapter = mock(DiscordConversationPersistenceService.class);

    private final DiscordAttachmentMapper attachmentMapper = new DiscordAttachmentMapperImpl();

    @Test
    void onMessageReceived_shouldIgnore_ownMessages() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User author = mock(User.class);

        when(event.getMessage()).thenReturn(message);
        when(message.getAuthor()).thenReturn(author);
        when(author.isBot()).thenReturn(true);

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter, attachmentMapper);
        handler.onMessageReceived(event);

        // Verify that the event was ignored
        verify(chatClient, never()).prompt();
        verify(message, never()).getJDA();
    }

    @Test
    void onMessageReceived_shouldIgnore_systemMessages() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User author = mock(User.class);

        when(event.getMessage()).thenReturn(message);
        when(message.getAuthor()).thenReturn(author);
        when(author.isSystem()).thenReturn(true);

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter, attachmentMapper);
        handler.onMessageReceived(event);

        // Verify that the event was ignored
        verify(chatClient, never()).prompt();
        verify(message, never()).getJDA();
    }

    @Test
    void onMessageReceived_shouldIgnore_invalidMentions() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        Mentions mentions = mock(Mentions.class);
        User invalidUser = mock(User.class);
        User author = mock(User.class);
        JDA jda = mock(JDA.class);
        SelfUser selfUser = mock(SelfUser.class);

        when(event.getMessage()).thenReturn(message);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAuthor()).thenReturn(author);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);
        when(mentions.getUsers()).thenReturn(List.of(invalidUser));
        when(invalidUser.getId()).thenReturn("invalidUserId");
        when(message.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(selfUser.getId()).thenReturn("selfUserId");

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter, attachmentMapper);
        handler.onMessageReceived(event);

        // Verify that the event was ignored
        verify(chatClient, never()).prompt();
    }
}
package space.parzival.discord.badley.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AbstractMessage;
import space.parzival.discord.badley.adapter.DiscordConversationPersistenceAdapter;

import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

class DiscordMessageHandlerTest {
    private final ChatClient chatClient = mock(ChatClient.class);
    private final DiscordConversationPersistenceAdapter adapter = mock(DiscordConversationPersistenceAdapter.class);

    @Test
    void onMessageReceived_shouldIgnore_ownMessages() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User author = mock(User.class);

        when(event.getMessage()).thenReturn(message);
        when(message.getAuthor()).thenReturn(author);
        when(author.isBot()).thenReturn(true);

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter);
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

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter);
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

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter);
        handler.onMessageReceived(event);

        // Verify that the event was ignored
        verify(chatClient, never()).prompt();
    }

    @Test
    void onMessageReceived_shouldRespond_withValidResponse() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        Mentions mentions = mock(Mentions.class);
        User mentionedUser = mock(User.class);
        User author = mock(User.class);
        JDA jda = mock(JDA.class);
        SelfUser selfUser = mock(SelfUser.class);
        Message.Attachment attachment = mock(Message.Attachment.class);

        MessageChannelUnion channel = mock(MessageChannelUnion.class);
        when(event.getChannel()).thenReturn(channel);
        when(channel.sendTyping()).thenReturn(mock(RestAction.class));

        when(event.getMessage()).thenReturn(message);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAuthor()).thenReturn(author);
        when(message.getAttachments()).thenReturn(List.of(attachment));
        when(attachment.getId()).thenReturn("attachmentId");
        when(attachment.getUrl()).thenReturn("http://attachmentUrl");
        when(attachment.getContentType()).thenReturn("image/png");
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);
        when(mentions.getUsers()).thenReturn(List.of(mentionedUser));
        when(mentionedUser.getId()).thenReturn("selfUserId");
        when(message.getJDA()).thenReturn(jda);
        when(message.getContentRaw()).thenReturn("content");
        when(message.getAttachments()).thenReturn(List.of());
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(selfUser.getId()).thenReturn("selfUserId");

        DiscordMessageHandler handler = new DiscordMessageHandler(chatClient, adapter);

        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.messages(any(AbstractMessage.class))).thenReturn(requestSpec);

        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        when(callResponseSpec.content()).thenReturn("Valid response");
        when(requestSpec.call()).thenReturn(callResponseSpec);


        // Simulate a valid response from the chat client
        MessageCreateAction messageCreateAction = mock(MessageCreateAction.class);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(message.reply(anyString())).thenReturn(messageCreateAction);

        handler.onMessageReceived(event);

        // Verify that the event was processed and a response was sent
        verify(message).reply("Valid response");

        // do the same but this time with invalid response
        when(callResponseSpec.content()).thenReturn("");

        handler.onMessageReceived(event);

        verify(message).reply("Sorry, I couldn't process your request.");
    }
}
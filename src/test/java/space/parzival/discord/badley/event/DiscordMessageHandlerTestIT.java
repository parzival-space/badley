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
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import space.parzival.discord.badley.adapter.DiscordConversationPersistenceAdapter;
import space.parzival.discord.badley.mapper.DiscordAttachmentMapper;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
class DiscordMessageHandlerTestIT {
    private static final String AUTHOR_ID = "123456789";
    private static final String BOT_ID = "987654321";

    @MockitoBean
    private DiscordAttachmentMapper mapper;

    @MockitoBean
    private DiscordConversationPersistenceAdapter adapter;

    @MockitoBean
    private ChatMemory chatMemory;

    @MockitoBean
    private JDA jda;

    @MockitoSpyBean
    private ChatClient chatClient;

    @Autowired
    private DiscordMessageHandler handler;

    @Test
    public void onMessageReceived_respondsWith_validResponse() {
        final String message = "Hello, world!";

        MessageReceivedEvent mockEvent = mock(MessageReceivedEvent.class);
        Message mockMessage = mockMessage(message);
        MessageChannelUnion mockChannelUnion = mockMessageChannelUnion();
        SelfUser mockSelfUser = mockSelfUser();

        when(jda.getSelfUser()).thenReturn(mockSelfUser);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockEvent.getChannel()).thenReturn(mockChannelUnion);

        // mock ai stuff
        ChatClient.ChatClientRequestSpec mockRequestSpec = mockRequestSpec(message);
        when(chatClient.prompt()).thenReturn(mockRequestSpec);

        assertDoesNotThrow(() -> handler.onMessageReceived(mockEvent));
        verify(mockMessage).reply(message);
    }

    @Test
    void onMessageReceived_respondsWith_errorResponse() {
        final String errorResponse = "Nope! Something hasn't worked here.";
        final String aiResponse = null;

        MessageReceivedEvent mockEvent = mock(MessageReceivedEvent.class);
        Message mockMessage = mockMessage(aiResponse);
        MessageChannelUnion mockChannelUnion = mockMessageChannelUnion();
        SelfUser mockSelfUser = mockSelfUser();

        when(jda.getSelfUser()).thenReturn(mockSelfUser);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockEvent.getChannel()).thenReturn(mockChannelUnion);

        // mock ai stuff
        ChatClient.ChatClientRequestSpec mockRequestSpec = mockRequestSpec(aiResponse);
        when(chatClient.prompt()).thenReturn(mockRequestSpec);

        assertDoesNotThrow(() -> handler.onMessageReceived(mockEvent));
        verify(mockMessage).reply(errorResponse);
    }

    private Message mockMessage(String content) {
        MessageCreateAction mockCreateAction = mock(MessageCreateAction.class);
        Message message = mock(Message.class);
        User mockUser = mockUser(AUTHOR_ID);
        Mentions mockMentions = mockMentions();
        when(message.getAuthor()).thenReturn(mockUser);
        when(message.getMentions()).thenReturn(mockMentions);
        when(message.getContentDisplay()).thenReturn(content);
        when(message.getJDA()).thenReturn(jda);
        when(message.reply(anyString())).thenReturn(mockCreateAction);
        return message;
    }

    private User mockUser(String id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.isBot()).thenReturn(false);
        when(user.isSystem()).thenReturn(false);
        return user;
    }

    private Mentions mockMentions() {
        Mentions mentions = mock(Mentions.class);
        User mentionedUser = mockUser(BOT_ID);
        when(mentions.getUsers()).thenReturn(List.of(mentionedUser));
        return mentions;
    }

    private SelfUser mockSelfUser() {
        SelfUser selfUser = mock(SelfUser.class);
        when(selfUser.getId()).thenReturn(BOT_ID);
        when(selfUser.isBot()).thenReturn(true);
        return selfUser;
    }

    private MessageChannelUnion mockMessageChannelUnion() {
        MessageChannelUnion channel = mock(MessageChannelUnion.class);
        RestAction<Void> sendTypingAction = mock(RestAction.class);
        when(channel.sendTyping()).thenReturn(sendTypingAction);
        return channel;
    }

    private ChatClient.ChatClientRequestSpec mockRequestSpec(String aiResponse) {
        ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec mockResponseSpec = mockResponseSpec(aiResponse);

        when(mockRequestSpec.advisors(any(Consumer.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.messages(any(UserMessage.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.call()).thenReturn(mockResponseSpec);
        return mockRequestSpec;
    }

    private ChatClient.CallResponseSpec mockResponseSpec(String aiResponse) {
        ChatClient.CallResponseSpec mockResponseSpec = mock(ChatClient.CallResponseSpec.class);
        when(mockResponseSpec.content()).thenReturn(aiResponse);
        return mockResponseSpec;
    }
}

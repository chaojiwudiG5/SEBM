package group5.sebm.notifiation.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WebSocket处理器测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationWebSocketHandlerTest {

    @Mock
    private WebSocketSession session;

    private NotificationWebSocketHandler handler;

    @BeforeEach
    void setUp() throws Exception {
        handler = new NotificationWebSocketHandler();
        when(session.getId()).thenReturn("test-session-id");
        when(session.isOpen()).thenReturn(true);
    }

    @Test
    void testSupportsPartialMessages() {
        assertFalse(handler.supportsPartialMessages());
    }

    @Test
    void testAfterConnectionEstablished_WithValidUserId() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));

        // When
        handler.afterConnectionEstablished(session);

        // Then
        assertTrue(handler.isUserOnline("123"));
        assertEquals(1, handler.getOnlineUserCount());
    }

    @Test
    void testAfterConnectionEstablished_WithoutUserId() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws"));

        // When
        handler.afterConnectionEstablished(session);

        // Then
        verify(session).close();
        assertEquals(0, handler.getOnlineUserCount());
    }

    @Test
    void testAfterConnectionClosed() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Then
        assertFalse(handler.isUserOnline("123"));
        assertEquals(0, handler.getOnlineUserCount());
    }

    @Test
    void testHandleTransportError() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        handler.handleTransportError(session, new RuntimeException("Test error"));

        // Then
        assertFalse(handler.isUserOnline("123"));
        assertEquals(0, handler.getOnlineUserCount());
    }

    @Test
    void testHandleMessage_TextMessage() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);
        String payload = "{\"type\":\"ping\"}";
        TextMessage message = new TextMessage(payload);

        // When
        handler.handleMessage(session, message);

        // Then
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendMessageToUser_Success() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        boolean result = handler.sendMessageToUser("123", "Test message");

        // Then
        assertTrue(result);
        verify(session).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendMessageToUser_UserNotOnline() {
        // When
        boolean result = handler.sendMessageToUser("999", "Test message");

        // Then
        assertFalse(result);
    }

    @Test
    void testSendMessageToUser_SessionClosed() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        when(session.isOpen()).thenReturn(false);
        handler.afterConnectionEstablished(session);

        // When
        boolean result = handler.sendMessageToUser("123", "Test message");

        // Then
        assertFalse(result);
    }

    @Test
    void testSendMessageToUser_IOException() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);
        doThrow(new IOException("Send error")).when(session).sendMessage(any(TextMessage.class));

        // When
        boolean result = handler.sendMessageToUser("123", "Test message");

        // Then
        assertFalse(result);
        assertFalse(handler.isUserOnline("123")); // Session should be removed
    }

    @Test
    void testSendNotificationToUser_Success() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        boolean result = handler.sendNotificationToUser("123", "Subject", "Content", "internal");

        // Then
        assertTrue(result);
        verify(session).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendNotificationToUser_UserNotOnline() {
        // When
        boolean result = handler.sendNotificationToUser("999", "Subject", "Content", "internal");

        // Then
        assertFalse(result);
    }

    @Test
    void testBroadcastMessage() throws Exception {
        // Given - Add 3 users
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);
        WebSocketSession session3 = mock(WebSocketSession.class);
        
        when(session1.getUri()).thenReturn(new URI("ws://localhost/ws?userId=1"));
        when(session2.getUri()).thenReturn(new URI("ws://localhost/ws?userId=2"));
        when(session3.getUri()).thenReturn(new URI("ws://localhost/ws?userId=3"));
        when(session1.getId()).thenReturn("session1");
        when(session2.getId()).thenReturn("session2");
        when(session3.getId()).thenReturn("session3");
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        when(session3.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);
        handler.afterConnectionEstablished(session3);

        // When
        int result = handler.broadcastMessage("Broadcast message");

        // Then
        assertEquals(3, result);
        verify(session1).sendMessage(any(TextMessage.class));
        verify(session2).sendMessage(any(TextMessage.class));
        verify(session3).sendMessage(any(TextMessage.class));
    }

    @Test
    void testBroadcastMessage_WithFailure() throws Exception {
        // Given
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);
        
        when(session1.getUri()).thenReturn(new URI("ws://localhost/ws?userId=1"));
        when(session2.getUri()).thenReturn(new URI("ws://localhost/ws?userId=2"));
        when(session1.getId()).thenReturn("session1");
        when(session2.getId()).thenReturn("session2");
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        doThrow(new IOException("Send error")).when(session1).sendMessage(any(TextMessage.class));

        // When
        int result = handler.broadcastMessage("Broadcast message");

        // Then
        assertEquals(1, result); // Only session2 succeeds
    }

    @Test
    void testGetOnlineUserCount() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        int count = handler.getOnlineUserCount();

        // Then
        assertEquals(1, count);
    }

    @Test
    void testGetOnlineUsers() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        String[] users = handler.getOnlineUsers();

        // Then
        assertEquals(1, users.length);
        assertEquals("123", users[0]);
    }

    @Test
    void testIsUserOnline_True() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);

        // When
        boolean isOnline = handler.isUserOnline("123");

        // Then
        assertTrue(isOnline);
    }

    @Test
    void testIsUserOnline_False() {
        // When
        boolean isOnline = handler.isUserOnline("999");

        // Then
        assertFalse(isOnline);
    }

    @Test
    void testIsUserOnline_SessionClosed() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        when(session.isOpen()).thenReturn(false);
        handler.afterConnectionEstablished(session);

        // When
        boolean isOnline = handler.isUserOnline("123");

        // Then
        assertFalse(isOnline);
    }

    @Test
    void testHandleMessage_StatusMessage() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);
        String payload = "{\"type\":\"status\",\"data\":\"active\"}";
        TextMessage message = new TextMessage(payload);

        // When
        handler.handleMessage(session, message);

        // Then
        // Should not throw exception - just verify it was called
        verify(session, atLeastOnce()).getUri();
    }

    @Test
    void testHandleMessage_UnknownType() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);
        String payload = "{\"type\":\"unknown\"}";
        TextMessage message = new TextMessage(payload);

        // When
        handler.handleMessage(session, message);

        // Then
        // Should not throw exception - just verify it was called
        verify(session, atLeastOnce()).getUri();
    }

    @Test
    void testHandleMessage_InvalidJson() throws Exception {
        // Given
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws?userId=123"));
        handler.afterConnectionEstablished(session);
        String payload = "invalid json";
        TextMessage message = new TextMessage(payload);

        // When
        handler.handleMessage(session, message);

        // Then
        // Should not throw exception, just log error
        verify(session, atLeastOnce()).getUri();
    }
}


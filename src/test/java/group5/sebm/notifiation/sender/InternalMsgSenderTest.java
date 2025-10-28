package group5.sebm.notifiation.sender;

import group5.sebm.User.service.UserServiceInterface.UserService;
import group5.sebm.common.dto.UserDto;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.websocket.NotificationWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 内部消息发送器测试
 */
@ExtendWith(MockitoExtension.class)
class InternalMsgSenderTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationWebSocketHandler webSocketHandler;

    @InjectMocks
    private InternalMsgSender internalMsgSender;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
    }

    @Test
    void testGetChannelType() {
        // When & Then
        assertEquals(NotificationMethodEnum.INTERNAL_MSG, internalMsgSender.getChannelType());
    }

    @Test
    void testSendNotification_NullUserId() {
        // When
        boolean result = internalMsgSender.sendNotification(null, "Test", "Content");

        // Then
        assertFalse(result);
        verify(userService, never()).getCurrentUserDtoFromID(anyLong());
    }

    @Test
    void testSendNotification_UserNotExists() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(null);

        // When
        boolean result = internalMsgSender.sendNotification(1L, "Test", "Content");

        // Then
        assertFalse(result);
        verify(userService).getCurrentUserDtoFromID(1L);
    }

    @Test
    void testSendNotification_Success_WithWebSocket() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(webSocketHandler.sendNotificationToUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        // When
        boolean result = internalMsgSender.sendNotification(1L, "Test Subject", "Test Content");

        // Then
        assertTrue(result);
        verify(userService, times(2)).getCurrentUserDtoFromID(1L); // Called twice in the method
        verify(webSocketHandler).sendNotificationToUser("1", "Test Subject", "Test Content", "internal");
    }

    @Test
    void testSendNotification_Success_WebSocketFailed() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(webSocketHandler.sendNotificationToUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(false);

        // When
        boolean result = internalMsgSender.sendNotification(1L, "Test Subject", "Test Content");

        // Then
        assertTrue(result); // Still returns true because message is stored
        verify(userService, times(2)).getCurrentUserDtoFromID(1L);
        verify(webSocketHandler).sendNotificationToUser("1", "Test Subject", "Test Content", "internal");
    }

    @Test
    void testSendNotification_WebSocketException() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(webSocketHandler.sendNotificationToUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("WebSocket error"));

        // When
        boolean result = internalMsgSender.sendNotification(1L, "Test Subject", "Test Content");

        // Then
        assertTrue(result); // Still returns true because exception is caught
        verify(userService, times(2)).getCurrentUserDtoFromID(1L);
    }

    @Test
    void testSendNotification_Exception() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenThrow(new RuntimeException("Service error"));

        // When
        boolean result = internalMsgSender.sendNotification(1L, "Test", "Content");

        // Then
        assertFalse(result);
        verify(userService).getCurrentUserDtoFromID(1L);
    }

    @Test
    void testBroadcastMessage_Success() {
        // Given
        when(webSocketHandler.broadcastMessage(anyString())).thenReturn(5);

        // When
        int result = internalMsgSender.broadcastMessage("Test Subject", "Test Content");

        // Then
        assertEquals(5, result);
        verify(webSocketHandler).broadcastMessage(anyString());
    }

    @Test
    void testBroadcastMessage_Exception() {
        // Given
        when(webSocketHandler.broadcastMessage(anyString()))
                .thenThrow(new RuntimeException("Broadcast error"));

        // When
        int result = internalMsgSender.broadcastMessage("Test Subject", "Test Content");

        // Then
        assertEquals(0, result);
        verify(webSocketHandler).broadcastMessage(anyString());
    }

    @Test
    void testGetOnlineUserCount() {
        // Given
        when(webSocketHandler.getOnlineUserCount()).thenReturn(10);

        // When
        int count = internalMsgSender.getOnlineUserCount();

        // Then
        assertEquals(10, count);
        verify(webSocketHandler).getOnlineUserCount();
    }

    @Test
    void testIsUserOnline() {
        // Given
        when(webSocketHandler.isUserOnline("1")).thenReturn(true);

        // When
        boolean isOnline = internalMsgSender.isUserOnline("1");

        // Then
        assertTrue(isOnline);
        verify(webSocketHandler).isUserOnline("1");
    }

    @Test
    void testGetUserMessages_WithMessages() {
        // Given - Send a message first
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(webSocketHandler.sendNotificationToUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);
        internalMsgSender.sendNotification(1L, "Test", "Content");

        // When
        String messages = internalMsgSender.getUserMessages("1");

        // Then
        assertNotNull(messages);
        assertTrue(messages.contains("Test"));
        assertTrue(messages.contains("Content"));
    }

    @Test
    void testGetUserMessages_NoMessages() {
        // When
        String messages = internalMsgSender.getUserMessages("999");

        // Then
        assertEquals("暂无消息", messages);
    }

    @Test
    void testClearUserMessages() {
        // Given - Send a message first
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(webSocketHandler.sendNotificationToUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);
        internalMsgSender.sendNotification(1L, "Test", "Content");

        // When
        internalMsgSender.clearUserMessages("1");
        String messages = internalMsgSender.getUserMessages("1");

        // Then
        assertEquals("暂无消息", messages);
    }

    @Test
    void testGetMessageStats() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(webSocketHandler.sendNotificationToUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);
        when(webSocketHandler.getOnlineUserCount()).thenReturn(3);
        String[] onlineUsers = new String[]{"1", "2", "3"};
        when(webSocketHandler.getOnlineUsers()).thenReturn(onlineUsers);
        
        // Send a message
        internalMsgSender.sendNotification(1L, "Test", "Content");

        // When
        Map<String, Object> stats = internalMsgSender.getMessageStats();

        // Then
        assertNotNull(stats);
        assertEquals(1, stats.get("totalUsers"));
        assertEquals(3, stats.get("onlineUsers"));
        assertNotNull(stats.get("onlineUserList"));
        assertNotNull(stats.get("timestamp"));
    }
}


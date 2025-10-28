package group5.sebm.notifiation.service;

import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.sender.ChannelMsgSender;
import group5.sebm.notifiation.sender.EmailSender;
import group5.sebm.notifiation.sender.InternalMsgSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageSenderService单元测试
 */
@ExtendWith(MockitoExtension.class)
class MessageSenderServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private InternalMsgSender internalMsgSender;

    private MessageSenderService messageSenderService;

    @BeforeEach
    void setUp() {
        // 模拟发送器返回其支持的渠道类型
        when(emailSender.getChannelType()).thenReturn(NotificationMethodEnum.EMAIL);
        when(internalMsgSender.getChannelType()).thenReturn(NotificationMethodEnum.INTERNAL_MSG);

        // 创建发送器列表并初始化服务
        List<ChannelMsgSender> senders = Arrays.asList(emailSender, internalMsgSender);
        messageSenderService = new MessageSenderService(senders);
    }

    @Test
    void testConstructor_RegistersSenders() {
        // Arrange & Act - 在setUp中已完成
        
        // Assert
        assertNotNull(messageSenderService.getSender(NotificationMethodEnum.EMAIL));
        assertNotNull(messageSenderService.getSender(NotificationMethodEnum.INTERNAL_MSG));
        assertNull(messageSenderService.getSender(NotificationMethodEnum.SMS));
    }

    @Test
    void testSendNotification_Email_Success() {
        // Arrange
        Long userId = 100L;
        String subject = "测试邮件";
        String content = "测试内容";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertTrue(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_Email_Failure() {
        // Arrange
        Long userId = 100L;
        String subject = "测试邮件";
        String content = "测试内容";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(false);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertFalse(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_Internal_Success() {
        // Arrange
        Long userId = 100L;
        String subject = "测试站内信";
        String content = "测试内容";
        
        when(internalMsgSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.INTERNAL_MSG, userId, subject, content);

        // Assert
        assertTrue(result);
        verify(internalMsgSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_UnsupportedChannel() {
        // Arrange
        Long userId = 100L;
        String subject = "测试短信";
        String content = "测试内容";

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.SMS, userId, subject, content);

        // Assert
        assertFalse(result);
        // 验证没有调用任何发送器
        verify(emailSender, never()).sendNotification(anyLong(), anyString(), anyString());
        verify(internalMsgSender, never()).sendNotification(anyLong(), anyString(), anyString());
    }

    @Test
    void testSendNotification_SenderThrowsException() {
        // Arrange
        Long userId = 100L;
        String subject = "测试邮件";
        String content = "测试内容";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenThrow(new RuntimeException("发送失败"));

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertFalse(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_NullUserId() {
        // Arrange
        String subject = "测试邮件";
        String content = "测试内容";
        
        when(emailSender.sendNotification(null, subject, content))
                .thenReturn(false);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, null, subject, content);

        // Assert
        assertFalse(result);
        verify(emailSender, times(1)).sendNotification(null, subject, content);
    }

    @Test
    void testSendNotification_EmptySubject() {
        // Arrange
        Long userId = 100L;
        String subject = "";
        String content = "测试内容";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertTrue(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_EmptyContent() {
        // Arrange
        Long userId = 100L;
        String subject = "测试邮件";
        String content = "";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertTrue(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testGetSender_Email() {
        // Act
        ChannelMsgSender sender = messageSenderService.getSender(NotificationMethodEnum.EMAIL);

        // Assert
        assertNotNull(sender);
        assertEquals(emailSender, sender);
    }

    @Test
    void testGetSender_Internal() {
        // Act
        ChannelMsgSender sender = messageSenderService.getSender(NotificationMethodEnum.INTERNAL_MSG);

        // Assert
        assertNotNull(sender);
        assertEquals(internalMsgSender, sender);
    }

    @Test
    void testGetSender_Unsupported() {
        // Act
        ChannelMsgSender sender = messageSenderService.getSender(NotificationMethodEnum.SMS);

        // Assert
        assertNull(sender);
    }

    @Test
    void testSendNotification_LongContent() {
        // Arrange
        Long userId = 100L;
        String subject = "测试邮件";
        String content = "很长的内容".repeat(1000); // 创建一个很长的内容
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertTrue(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_SpecialCharactersInContent() {
        // Arrange
        Long userId = 100L;
        String subject = "测试邮件";
        String content = "特殊字符: <html>&nbsp;\"'@#$%^&*()";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean result = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);

        // Assert
        assertTrue(result);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
    }

    @Test
    void testSendNotification_MultipleCallsSameChannel() {
        // Arrange
        Long userId1 = 100L;
        Long userId2 = 101L;
        String subject = "测试邮件";
        String content = "测试内容";
        
        when(emailSender.sendNotification(anyLong(), anyString(), anyString()))
                .thenReturn(true);

        // Act
        boolean result1 = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId1, subject, content);
        boolean result2 = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId2, subject, content);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(emailSender, times(2)).sendNotification(anyLong(), anyString(), anyString());
    }

    @Test
    void testSendNotification_MultipleDifferentChannels() {
        // Arrange
        Long userId = 100L;
        String subject = "测试通知";
        String content = "测试内容";
        
        when(emailSender.sendNotification(userId, subject, content))
                .thenReturn(true);
        when(internalMsgSender.sendNotification(userId, subject, content))
                .thenReturn(true);

        // Act
        boolean emailResult = messageSenderService.sendNotification(
                NotificationMethodEnum.EMAIL, userId, subject, content);
        boolean internalResult = messageSenderService.sendNotification(
                NotificationMethodEnum.INTERNAL_MSG, userId, subject, content);

        // Assert
        assertTrue(emailResult);
        assertTrue(internalResult);
        verify(emailSender, times(1)).sendNotification(userId, subject, content);
        verify(internalMsgSender, times(1)).sendNotification(userId, subject, content);
    }
}


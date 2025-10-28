package group5.sebm.notifiation.mq;

import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.service.NotificationRateLimiter;
import group5.sebm.notifiation.service.NotificationTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageProducer单元测试
 */
@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private NotificationTaskService notificationTaskService;

    @Mock
    private NotificationRateLimiter rateLimiter;

    @InjectMocks
    private MessageProducer messageProducer;

    private NotificationMessage mockMessage;
    private TemplatePo mockTemplate;

    @BeforeEach
    void setUp() {
        // 初始化模板
        mockTemplate = new TemplatePo();
        mockTemplate.setId(1L);
        mockTemplate.setTemplateTitle("测试通知");
        mockTemplate.setTemplateContent("测试内容");
        mockTemplate.setNotificationMethod(Arrays.asList(1, 3));
        mockTemplate.setNotificationRole(1);

        // 初始化消息
        mockMessage = new NotificationMessage();
        mockMessage.setMessageId("test-message-id");
        mockMessage.setUserId(100L);
        mockMessage.setTemplate(mockTemplate);
        mockMessage.setCreateTime(LocalDateTime.now());
        mockMessage.setRetryCount(0);
        mockMessage.setMaxRetryCount(3);

        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("userName", "张三");
        mockMessage.setTemplateVars(templateVars);
    }

    @Test
    void testSendImmediateMessage_Success() {
        // Arrange
        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));

        // Act
        boolean result = messageProducer.sendImmediateMessage(mockMessage);

        // Assert
        assertTrue(result);
        verify(rateLimiter, times(1)).allowNotification(100L);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification-topic"),
                eq("notification.immediate"),
                any(NotificationMessage.class));
        verify(notificationTaskService, never()).createTask(anyString(), anyString(), anyInt());
    }

    @Test
    void testSendImmediateMessage_RateLimited() {
        // Arrange
        when(rateLimiter.allowNotification(100L))
                .thenReturn(false);

        // Act
        boolean result = messageProducer.sendImmediateMessage(mockMessage);

        // Assert
        assertFalse(result);
        verify(rateLimiter, times(1)).allowNotification(100L);
        verify(rabbitTemplate, never()).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));
    }

    @Test
    void testSendImmediateMessage_WithoutMessageId() {
        // Arrange
        mockMessage.setMessageId(null);
        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));

        // Act
        boolean result = messageProducer.sendImmediateMessage(mockMessage);

        // Assert
        assertTrue(result);
        assertNotNull(mockMessage.getMessageId()); // 应该自动生成
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));
    }

    @Test
    void testSendImmediateMessage_WithoutCreateTime() {
        // Arrange
        mockMessage.setCreateTime(null);
        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));

        // Act
        boolean result = messageProducer.sendImmediateMessage(mockMessage);

        // Assert
        assertTrue(result);
        assertNotNull(mockMessage.getCreateTime()); // 应该自动设置
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));
    }

    @Test
    void testSendImmediateMessage_RabbitMQException() {
        // Arrange
        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        doThrow(new RuntimeException("RabbitMQ error"))
                .when(rabbitTemplate).convertAndSend(
                        anyString(), anyString(), any(NotificationMessage.class));

        // Act
        boolean result = messageProducer.sendImmediateMessage(mockMessage);

        // Assert
        assertFalse(result);
        verify(rateLimiter, times(1)).allowNotification(100L);
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));
    }

    @Test
    void testSendDelayMessage_Success() {
        // Arrange
        long delaySeconds = 3600L; // 1小时延迟
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertTrue(result);
        assertEquals(taskId, mockMessage.getTaskId());
        verify(rateLimiter, times(1)).allowNotification(100L);
        verify(notificationTaskService, times(1)).createTask(
                eq("测试通知"),
                eq("测试内容"),
                eq(1));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification.delay.exchange"),
                eq("notification.delay"),
                any(NotificationMessage.class),
                any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_RateLimited() {
        // Arrange
        long delaySeconds = 3600L;
        when(rateLimiter.allowNotification(100L))
                .thenReturn(false);

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertFalse(result);
        verify(rateLimiter, times(1)).allowNotification(100L);
        verify(notificationTaskService, never()).createTask(anyString(), anyString(), anyInt());
        verify(rabbitTemplate, never()).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_TaskCreationFailed() {
        // Arrange
        long delaySeconds = 3600L;
        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(null);

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertFalse(result);
        verify(rateLimiter, times(1)).allowNotification(100L);
        verify(notificationTaskService, times(1)).createTask(anyString(), anyString(), anyInt());
        verify(rabbitTemplate, never()).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_WithoutMessageId() {
        // Arrange
        mockMessage.setMessageId(null);
        long delaySeconds = 3600L;
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertTrue(result);
        assertNotNull(mockMessage.getMessageId()); // 应该自动生成
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_ShortDelay() {
        // Arrange
        long delaySeconds = 10L; // 10秒延迟
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertTrue(result);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification.delay.exchange"),
                eq("notification.delay"),
                any(NotificationMessage.class),
                any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_LongDelay() {
        // Arrange
        long delaySeconds = 86400L; // 1天延迟
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertTrue(result);
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_RabbitMQException() {
        // Arrange
        long delaySeconds = 3600L;
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doThrow(new RuntimeException("RabbitMQ error"))
                .when(rabbitTemplate).convertAndSend(
                        anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertFalse(result);
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    void testSendImmediateMessage_MultipleUsers() {
        // Arrange
        when(rateLimiter.allowNotification(anyLong()))
                .thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));

        // Act - 发送给多个用户
        mockMessage.setUserId(100L);
        boolean result1 = messageProducer.sendImmediateMessage(mockMessage);

        mockMessage.setUserId(101L);
        boolean result2 = messageProducer.sendImmediateMessage(mockMessage);

        mockMessage.setUserId(102L);
        boolean result3 = messageProducer.sendImmediateMessage(mockMessage);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(rabbitTemplate, times(3)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class));
    }

    @Test
    void testSendDelayMessage_ZeroDelay() {
        // Arrange
        long delaySeconds = 0L; // 0秒延迟
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertTrue(result);
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    void testSendDelayMessage_NegativeDelay() {
        // Arrange - 虽然不应该传负数，但测试处理逻辑
        long delaySeconds = -100L;
        Long taskId = 100L;

        when(rateLimiter.allowNotification(100L))
                .thenReturn(true);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(taskId);
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));

        // Act
        boolean result = messageProducer.sendDelayMessage(mockMessage, delaySeconds);

        // Assert
        assertTrue(result);
        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(NotificationMessage.class), any(MessagePostProcessor.class));
    }
}


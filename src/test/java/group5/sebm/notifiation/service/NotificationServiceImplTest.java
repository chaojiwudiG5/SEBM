package group5.sebm.notifiation.service;

import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.converter.NotificationConverter;
import group5.sebm.notifiation.enums.NotificationTypeEnum;
import group5.sebm.notifiation.mq.MessageProducer;
import group5.sebm.notifiation.mq.NotificationMessage;
import group5.sebm.notifiation.service.dto.TemplateDto;
import group5.sebm.notifiation.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NotificationService单元测试
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private NotificationConverter notificationConverter;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private SendNotificationDto mockSendDto;
    private TemplateDto mockTemplateDto;
    private NotificationMessage mockMessage;

    @BeforeEach
    void setUp() {
        // 初始化发送通知DTO
        mockSendDto = new SendNotificationDto();
        mockSendDto.setUserId(100L);
        mockSendDto.setNotificationEvent(1001);
        mockSendDto.setNodeTimestamp(System.currentTimeMillis() / 1000);
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("userName", "张三");
        templateVars.put("deviceName", "笔记本电脑");
        mockSendDto.setTemplateVars(templateVars);

        // 初始化模板DTO
        mockTemplateDto = new TemplateDto();
        mockTemplateDto.setId(1L);
        mockTemplateDto.setTemplateTitle("测试模板");
        mockTemplateDto.setTemplateContent("测试内容");
        mockTemplateDto.setNotificationType(0); // 即时通知
        mockTemplateDto.setRelateTimeOffset(0L);

        // 初始化通知消息
        mockMessage = new NotificationMessage();
        mockMessage.setMessageId("test-message-id");
        mockMessage.setUserId(100L);
    }

    @Test
    void testSendNotification_ImmediateNotification_Success() {
        // Arrange
        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendImmediateMessage(any(NotificationMessage.class)))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        verify(templateService, times(1)).findTemplateByParams(1001);
        verify(notificationConverter, times(1)).buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class));
        verify(messageProducer, times(1)).sendImmediateMessage(any(NotificationMessage.class));
        verify(messageProducer, never()).sendDelayMessage(any(NotificationMessage.class), anyLong());
    }

    @Test
    void testSendNotification_DelayNotification_Success() {
        // Arrange
        mockTemplateDto.setNotificationType(1); // 延迟通知（提前）
        mockTemplateDto.setRelateTimeOffset(3600L); // 提前1小时
        mockSendDto.setNodeTimestamp(System.currentTimeMillis() / 1000 + 7200); // 节点时间是2小时后

        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendDelayMessage(any(NotificationMessage.class), anyLong()))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        verify(templateService, times(1)).findTemplateByParams(1001);
        verify(messageProducer, times(1)).sendDelayMessage(any(NotificationMessage.class), anyLong());
        verify(messageProducer, never()).sendImmediateMessage(any(NotificationMessage.class));
    }

    @Test
    void testSendNotification_TemplateNotFound() {
        // Arrange
        when(templateService.findTemplateByParams(1001))
                .thenReturn(null);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertFalse(result);
        verify(templateService, times(1)).findTemplateByParams(1001);
        verify(notificationConverter, never()).buildNotificationMessage(any(), any());
        verify(messageProducer, never()).sendImmediateMessage(any());
        verify(messageProducer, never()).sendDelayMessage(any(), anyLong());
    }

    @Test
    void testSendNotification_MessageProducerFails() {
        // Arrange
        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendImmediateMessage(any(NotificationMessage.class)))
                .thenReturn(false);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertFalse(result);
        verify(messageProducer, times(1)).sendImmediateMessage(any(NotificationMessage.class));
    }

    @Test
    void testSendNotification_WithPastNodeTimestamp() {
        // Arrange - 节点时间已过
        mockTemplateDto.setNotificationType(1); // 延迟通知
        mockTemplateDto.setRelateTimeOffset(3600L);
        mockSendDto.setNodeTimestamp(System.currentTimeMillis() / 1000 - 7200); // 节点时间是2小时前

        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendImmediateMessage(any(NotificationMessage.class)))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        // 延迟时间计算为负数时，应该立即发送
        verify(messageProducer, times(1)).sendImmediateMessage(any(NotificationMessage.class));
    }

    @Test
    void testSendNotification_PostponeNotification() {
        // Arrange - 延后通知(提前通知)
        // 对于提前通知，节点时间应该在未来，这样 nodeTime - offset 才会大于当前时间
        mockTemplateDto.setNotificationType(-1); // 延迟通知（提前通知）
        mockTemplateDto.setRelateTimeOffset(1800L); // 提前30分钟
        // 设置节点时间为未来2小时，这样 节点时间 - 30分钟 = 未来1.5小时，仍然大于当前时间
        mockSendDto.setNodeTimestamp(System.currentTimeMillis() / 1000 + 7200); // 节点时间是2小时后

        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendDelayMessage(any(NotificationMessage.class), anyLong()))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        verify(messageProducer, times(1)).sendDelayMessage(any(NotificationMessage.class), anyLong());
    }

    @Test
    void testSendNotification_NullNodeTimestamp() {
        // Arrange
        mockSendDto.setNodeTimestamp(null);
        
        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendImmediateMessage(any(NotificationMessage.class)))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        // 当节点时间戳为空时，应该立即发送
        verify(messageProducer, times(1)).sendImmediateMessage(any(NotificationMessage.class));
    }

    @Test
    void testSendNotification_NullRelateTimeOffset() {
        // Arrange
        mockTemplateDto.setNotificationType(1);
        mockTemplateDto.setRelateTimeOffset(null);
        mockSendDto.setNodeTimestamp(System.currentTimeMillis() / 1000);

        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendImmediateMessage(any(NotificationMessage.class)))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        // 当时间偏移为空时，默认为0，应该立即发送
        verify(messageProducer, times(1)).sendImmediateMessage(any(NotificationMessage.class));
    }

    @Test
    void testSendNotification_ExceptionHandling() {
        // Arrange
        when(templateService.findTemplateByParams(1001))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert - 期望抛出异常
        assertThrows(RuntimeException.class, () -> {
            notificationService.sendNotification(mockSendDto);
        });
        
        verify(templateService, times(1)).findTemplateByParams(1001);
    }

    @Test
    void testSendNotification_WithEmptyTemplateVars() {
        // Arrange
        mockSendDto.setTemplateVars(new HashMap<>());

        when(templateService.findTemplateByParams(1001))
                .thenReturn(mockTemplateDto);
        when(notificationConverter.buildNotificationMessage(any(SendNotificationDto.class), any(TemplateDto.class)))
                .thenReturn(mockMessage);
        when(messageProducer.sendImmediateMessage(any(NotificationMessage.class)))
                .thenReturn(true);

        // Act
        Boolean result = notificationService.sendNotification(mockSendDto);

        // Assert
        assertTrue(result);
        verify(messageProducer, times(1)).sendImmediateMessage(any(NotificationMessage.class));
    }
}


package group5.sebm.notifiation.mq;

import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.enums.NotificationRecordStatusEnum;
import group5.sebm.notifiation.service.MessageSenderService;
import group5.sebm.notifiation.service.NotificationRecordService;
import group5.sebm.notifiation.service.NotificationTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageProcessor单元测试
 */
@ExtendWith(MockitoExtension.class)
class MessageProcessorTest {

    @Mock
    private MessageSenderService messageSenderService;

    @Mock
    private NotificationTaskService notificationTaskService;

    @Mock
    private NotificationRecordService notificationRecordService;

    @InjectMocks
    private MessageProcessor messageProcessor;

    private NotificationMessage mockMessage;
    private TemplatePo mockTemplate;

    @BeforeEach
    void setUp() {
        mockTemplate = new TemplatePo();
        mockTemplate.setId(1L);
        mockTemplate.setTemplateTitle("测试通知");
        mockTemplate.setTemplateContent("这是测试内容");
        mockTemplate.setNotificationMethod(Arrays.asList(
                NotificationMethodEnum.EMAIL.getCode(),
                NotificationMethodEnum.INTERNAL_MSG.getCode()
        ));
        mockTemplate.setNotificationRole(1);

        mockMessage = new NotificationMessage();
        mockMessage.setMessageId("test-message-id");
        mockMessage.setUserId(100L);
        mockMessage.setTemplate(mockTemplate);
        mockMessage.setTaskId(1000L);
    }

    @Test
    void testProcessNotification_Success_WithTaskId() {
        // Arrange
        when(messageSenderService.sendNotification(any(), anyLong(), anyString(), anyString()))
                .thenReturn(true);
        when(notificationRecordService.createRecord(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(true);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert
        // 验证发送了2次通知（邮件 + 站内信）
        verify(messageSenderService, times(2))
                .sendNotification(any(NotificationMethodEnum.class), eq(100L), 
                        eq("测试通知"), eq("这是测试内容"));
        
        // 验证创建了2条记录
        verify(notificationRecordService, times(2))
                .createRecord(eq(1000L), eq(100L), anyInt(), 
                        eq(NotificationRecordStatusEnum.SUCCESS.getCode()));
        
        // 不应该创建任务（因为已经有taskId）
        verify(notificationTaskService, never()).createTask(anyString(), anyString(), anyInt());
    }

    @Test
    void testProcessNotification_Success_WithoutTaskId() {
        // Arrange
        mockMessage.setTaskId(null); // 没有taskId，需要创建
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(2000L);
        when(messageSenderService.sendNotification(any(), anyLong(), anyString(), anyString()))
                .thenReturn(true);
        when(notificationRecordService.createRecord(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(true);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert
        // 验证创建了任务
        verify(notificationTaskService, times(1))
                .createTask("测试通知", "这是测试内容", 1);
        
        // 验证使用新创建的taskId发送通知
        verify(messageSenderService, times(2))
                .sendNotification(any(NotificationMethodEnum.class), eq(100L), 
                        anyString(), anyString());
        
        // 验证创建记录时使用了新的taskId
        verify(notificationRecordService, times(2))
                .createRecord(eq(2000L), eq(100L), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_SendFailed() {
        // Arrange - 第一个方式成功，第二个方式失败
        when(messageSenderService.sendNotification(eq(NotificationMethodEnum.EMAIL), anyLong(), 
                anyString(), anyString()))
                .thenReturn(true);
        when(messageSenderService.sendNotification(eq(NotificationMethodEnum.INTERNAL_MSG), anyLong(), 
                anyString(), anyString()))
                .thenThrow(new RuntimeException("发送失败"));
        when(notificationRecordService.createRecord(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(true);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert
        // 验证发送了2次
        verify(messageSenderService, times(2))
                .sendNotification(any(NotificationMethodEnum.class), eq(100L), 
                        anyString(), anyString());
        
        // 验证创建了2条记录（一条成功，一条失败）
        verify(notificationRecordService, times(1))
                .createRecord(eq(1000L), eq(100L), anyInt(), 
                        eq(NotificationRecordStatusEnum.SUCCESS.getCode()));
        verify(notificationRecordService, times(1))
                .createRecord(eq(1000L), eq(100L), anyInt(), 
                        eq(NotificationRecordStatusEnum.FAILED.getCode()));
    }

    @Test
    void testProcessNotification_NullMessage() {
        // Act & Assert - 会抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            messageProcessor.processNotification(null);
        });

        // 不应该调用任何服务
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
        verify(notificationTaskService, never()).createTask(anyString(), anyString(), anyInt());
    }

    @Test
    void testProcessNotification_NullUserId() {
        // Arrange
        mockMessage.setUserId(null);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 验证失败，不应该发送
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_NullTemplate() {
        // Arrange
        mockMessage.setTemplate(null);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 验证失败，不应该发送
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_EmptyNotificationMethods() {
        // Arrange
        mockTemplate.setNotificationMethod(Collections.emptyList());

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 验证失败，不应该发送
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_NullNotificationMethods() {
        // Arrange
        mockTemplate.setNotificationMethod(null);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 验证失败，不应该发送
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_InvalidNotificationMethod() {
        // Arrange - 使用无效的通知方式代码
        mockTemplate.setNotificationMethod(Arrays.asList(999)); // 无效的代码

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 验证失败，不应该发送
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_MixedValidInvalidMethods() {
        // Arrange - 混合有效和无效的通知方式
        mockTemplate.setNotificationMethod(Arrays.asList(
                NotificationMethodEnum.EMAIL.getCode(),
                999, // 无效
                NotificationMethodEnum.INTERNAL_MSG.getCode()
        ));

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 包含无效代码的列表会导致验证失败，不发送任何通知
        verify(messageSenderService, never())
                .sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never())
                .createRecord(anyLong(), anyLong(), anyInt(), anyInt());
        verify(notificationTaskService, never())
                .createTask(anyString(), anyString(), anyInt());
    }

    @Test
    void testProcessNotification_CreateTaskFailed() {
        // Arrange
        mockMessage.setTaskId(null);
        when(notificationTaskService.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(null); // 创建任务失败

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert
        verify(notificationTaskService, times(1))
                .createTask(anyString(), anyString(), anyInt());
        // 任务创建失败后，不应该发送通知
        verify(messageSenderService, never()).sendNotification(any(), anyLong(), anyString(), anyString());
        verify(notificationRecordService, never()).createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_CreateRecordFailed() {
        // Arrange
        when(messageSenderService.sendNotification(any(), anyLong(), anyString(), anyString()))
                .thenReturn(true);
        when(notificationRecordService.createRecord(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(false); // 创建记录失败

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 仍然应该尝试发送和创建记录
        verify(messageSenderService, times(2))
                .sendNotification(any(NotificationMethodEnum.class), eq(100L), 
                        anyString(), anyString());
        verify(notificationRecordService, times(2))
                .createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_SingleMethod() {
        // Arrange - 只有一个通知方式
        mockTemplate.setNotificationMethod(Arrays.asList(NotificationMethodEnum.EMAIL.getCode()));
        when(messageSenderService.sendNotification(any(), anyLong(), anyString(), anyString()))
                .thenReturn(true);
        when(notificationRecordService.createRecord(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(true);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert - 只发送一次
        verify(messageSenderService, times(1))
                .sendNotification(eq(NotificationMethodEnum.EMAIL), eq(100L), 
                        anyString(), anyString());
        verify(notificationRecordService, times(1))
                .createRecord(anyLong(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testProcessNotification_AllMethodsFailed() {
        // Arrange - 所有方式都发送失败
        when(messageSenderService.sendNotification(any(), anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("发送失败"));
        when(notificationRecordService.createRecord(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(true);

        // Act
        messageProcessor.processNotification(mockMessage);

        // Assert
        verify(messageSenderService, times(2))
                .sendNotification(any(NotificationMethodEnum.class), eq(100L), 
                        anyString(), anyString());
        // 所有记录都应该标记为失败
        verify(notificationRecordService, times(2))
                .createRecord(eq(1000L), eq(100L), anyInt(), 
                        eq(NotificationRecordStatusEnum.FAILED.getCode()));
    }
}


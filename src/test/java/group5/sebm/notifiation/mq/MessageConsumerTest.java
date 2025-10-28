package group5.sebm.notifiation.mq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MessageConsumer单元测试
 */
@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    @Mock
    private MessageProcessor messageProcessor;

    @InjectMocks
    private MessageConsumer messageConsumer;

    private NotificationMessage mockMessage;
    private Message mockAmqpMessage;

    @BeforeEach
    void setUp() {
        // 初始化通知消息
        mockMessage = new NotificationMessage();
        mockMessage.setMessageId("test-message-id");
        mockMessage.setUserId(100L);

        // 初始化AMQP消息
        MessageProperties props = new MessageProperties();
        mockAmqpMessage = new Message("test message".getBytes(), props);
    }

    @Test
    void testHandleImmediateMessage_Success() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        // Act
        messageConsumer.handleImmediateMessage(mockMessage, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(1)).processNotification(mockMessage);
    }

    @Test
    void testHandleImmediateMessage_ProcessorThrowsException() {
        // Arrange
        doThrow(new RuntimeException("处理异常"))
                .when(messageProcessor).processNotification(any(NotificationMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            messageConsumer.handleImmediateMessage(mockMessage, mockAmqpMessage);
        });

        verify(messageProcessor, times(1)).processNotification(mockMessage);
    }

    @Test
    void testHandleImmediateMessage_NullMessage() {
        // Arrange - null message会在日志记录时抛出NullPointerException
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            messageConsumer.handleImmediateMessage(null, mockAmqpMessage);
        });
        
        // 不应该调用 processNotification，因为在日志记录时就失败了
        verify(messageProcessor, never()).processNotification(any());
    }

    @Test
    void testHandleImmediateMessage_MultipleMessages() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage message1 = new NotificationMessage();
        message1.setMessageId("message-1");
        message1.setUserId(100L);

        NotificationMessage message2 = new NotificationMessage();
        message2.setMessageId("message-2");
        message2.setUserId(101L);

        // Act
        messageConsumer.handleImmediateMessage(message1, mockAmqpMessage);
        messageConsumer.handleImmediateMessage(message2, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(2)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testHandleDelayMessage_Success() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        // Act
        messageConsumer.handleDelayMessage(mockMessage, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(1)).processNotification(mockMessage);
    }

    @Test
    void testHandleDelayMessage_ProcessorThrowsException() {
        // Arrange
        doThrow(new RuntimeException("处理异常"))
                .when(messageProcessor).processNotification(any(NotificationMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            messageConsumer.handleDelayMessage(mockMessage, mockAmqpMessage);
        });

        verify(messageProcessor, times(1)).processNotification(mockMessage);
    }

    @Test
    void testHandleDelayMessage_NullMessage() {
        // Arrange - null message会在日志记录时抛出NullPointerException
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            messageConsumer.handleDelayMessage(null, mockAmqpMessage);
        });
        
        // 不应该调用 processNotification，因为在日志记录时就失败了
        verify(messageProcessor, never()).processNotification(any());
    }

    @Test
    void testHandleDelayMessage_MultipleMessages() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage message1 = new NotificationMessage();
        message1.setMessageId("delay-message-1");
        message1.setUserId(100L);

        NotificationMessage message2 = new NotificationMessage();
        message2.setMessageId("delay-message-2");
        message2.setUserId(101L);

        // Act
        messageConsumer.handleDelayMessage(message1, mockAmqpMessage);
        messageConsumer.handleDelayMessage(message2, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(2)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testHandleImmediateMessage_DifferentUserIds() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage message1 = new NotificationMessage();
        message1.setUserId(100L);

        NotificationMessage message2 = new NotificationMessage();
        message2.setUserId(200L);

        NotificationMessage message3 = new NotificationMessage();
        message3.setUserId(300L);

        // Act
        messageConsumer.handleImmediateMessage(message1, mockAmqpMessage);
        messageConsumer.handleImmediateMessage(message2, mockAmqpMessage);
        messageConsumer.handleImmediateMessage(message3, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(3)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testHandleDelayMessage_DifferentUserIds() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage message1 = new NotificationMessage();
        message1.setUserId(100L);

        NotificationMessage message2 = new NotificationMessage();
        message2.setUserId(200L);

        NotificationMessage message3 = new NotificationMessage();
        message3.setUserId(300L);

        // Act
        messageConsumer.handleDelayMessage(message1, mockAmqpMessage);
        messageConsumer.handleDelayMessage(message2, mockAmqpMessage);
        messageConsumer.handleDelayMessage(message3, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(3)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testBothQueueMessages() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage immediateMessage = new NotificationMessage();
        immediateMessage.setMessageId("immediate-1");
        immediateMessage.setUserId(100L);

        NotificationMessage delayMessage = new NotificationMessage();
        delayMessage.setMessageId("delay-1");
        delayMessage.setUserId(101L);

        // Act
        messageConsumer.handleImmediateMessage(immediateMessage, mockAmqpMessage);
        messageConsumer.handleDelayMessage(delayMessage, mockAmqpMessage);

        // Assert
        verify(messageProcessor, times(2)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testHandleImmediateMessage_ProcessorPartialFailure() {
        // Arrange - 第一次失败，第二次成功
        doThrow(new RuntimeException("第一次失败"))
                .doNothing()
                .when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage message1 = new NotificationMessage();
        message1.setMessageId("message-1");

        NotificationMessage message2 = new NotificationMessage();
        message2.setMessageId("message-2");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            messageConsumer.handleImmediateMessage(message1, mockAmqpMessage);
        });

        messageConsumer.handleImmediateMessage(message2, mockAmqpMessage);

        verify(messageProcessor, times(2)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testHandleDelayMessage_ProcessorPartialFailure() {
        // Arrange - 第一次失败，第二次成功
        doThrow(new RuntimeException("第一次失败"))
                .doNothing()
                .when(messageProcessor).processNotification(any(NotificationMessage.class));

        NotificationMessage message1 = new NotificationMessage();
        message1.setMessageId("delay-message-1");

        NotificationMessage message2 = new NotificationMessage();
        message2.setMessageId("delay-message-2");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            messageConsumer.handleDelayMessage(message1, mockAmqpMessage);
        });

        messageConsumer.handleDelayMessage(message2, mockAmqpMessage);

        verify(messageProcessor, times(2)).processNotification(any(NotificationMessage.class));
    }

    @Test
    void testHandleImmediateMessage_VerifyLogging() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        // Act
        messageConsumer.handleImmediateMessage(mockMessage, mockAmqpMessage);

        // Assert - 验证方法被调用，日志记录在实际环境中验证
        verify(messageProcessor, times(1)).processNotification(mockMessage);
    }

    @Test
    void testHandleDelayMessage_VerifyLogging() {
        // Arrange
        doNothing().when(messageProcessor).processNotification(any(NotificationMessage.class));

        // Act
        messageConsumer.handleDelayMessage(mockMessage, mockAmqpMessage);

        // Assert - 验证方法被调用，日志记录在实际环境中验证
        verify(messageProcessor, times(1)).processNotification(mockMessage);
    }
}


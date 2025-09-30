package group5.sebm.notifiation.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RocketMQ 消息消费者服务
 */
@Slf4j
@Service
@RocketMQMessageListener(
        topic = "notification-topic", consumerGroup = "notification-consumer-group"
)
public class MessageConsumer implements RocketMQListener<NotificationMessage> {

    @Autowired
    private MessageProcessor messageProcessor;

    /**
     * 消费即时通知消息
     */
    @Override
    public void onMessage(NotificationMessage message) {
        log.info("接收到即时通知消息: messageId={}, userId={}",
                message.getMessageId(), message.getUserId());

        try {
            // 处理通知消息
             messageProcessor.processNotification(message);
        } catch (Exception e) {
            log.error("即时通知消息处理异常: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            handleRetry(message);
        }

    }
    
    
    /**
     * 处理重试逻辑
     * @param message 消息
     */
    private void handleRetry(NotificationMessage message) {
        if (message.getRetryCount() < message.getMaxRetryCount()) {
            message.setRetryCount(message.getRetryCount() + 1);
            
            log.info("准备重试通知消息: messageId={}, retryCount={}", 
                    message.getMessageId(), message.getRetryCount());
            
            // 这里可以实现重试发送逻辑
            // 例如：发送到死信队列或者重新发送
        } else {
            log.error("通知消息重试次数已达上限，丢弃消息: messageId={}", message.getMessageId());
            // 这里可以实现死信处理逻辑
        }
    }
}

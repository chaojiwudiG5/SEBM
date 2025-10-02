package group5.sebm.notifiation.mq;

import com.alibaba.fastjson.JSON;
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
public class MessageConsumer {

    @Autowired
    private MessageProcessor messageProcessor;

    /**
     * 消费即时通知消息
     */
    @RocketMQMessageListener(
            topic = "notification-topic",
            selectorExpression = "immediate",
            consumerGroup = "notification-consumer-group"
    )
    public class ImmediateMessageConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String message) {
            try {
                log.info("接收到即时通知消息: {}", message);

                // 解析消息
                NotificationMessage notificationMessage = JSON.parseObject(message, NotificationMessage.class);

                // 处理通知消息
                messageProcessor.processNotification(notificationMessage);

                log.info("即时通知消息处理完成: messageId={}", notificationMessage.getMessageId());

            } catch (Exception e) {
                log.error("即时通知消息处理异常: error={}", e.getMessage(), e);
            }
        }
    }

    /**
     * 消费延迟通知消息
     */
    @RocketMQMessageListener(
            topic = "notification-topic",
            selectorExpression = "delay",
            consumerGroup = "notification-consumer-group"
    )
    public class DelayMessageConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String message) {
            try {
                log.info("接收到延迟通知消息: {}", message);

                // 解析消息
                NotificationMessage notificationMessage = JSON.parseObject(message, NotificationMessage.class);

                // 处理通知消息
                messageProcessor.processNotification(notificationMessage);

                log.info("延迟通知消息处理完成: messageId={}", notificationMessage.getMessageId());

            } catch (Exception e) {
                log.error("延迟通知消息处理异常: error={}", e.getMessage(), e);
            }
        }
    }
}
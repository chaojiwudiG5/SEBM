package group5.sebm.notifiation.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static group5.sebm.common.constant.NotificationConstant.NOTIFICATION_TOPIC;

/**
 * RabbitMQ 消息生产者服务 - 使用延迟消息插件实现高精度延迟
 */
@Slf4j
@Service
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送即时通知消息
     * @param message 通知消息
     * @return 是否发送成功
     */
    public boolean sendImmediateMessage(NotificationMessage message) {
        try {
            // 设置消息ID和创建时间
            if (message.getMessageId() == null) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            if (message.getCreateTime() == null) {
                message.setCreateTime(LocalDateTime.now());
            }

            // 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    NOTIFICATION_TOPIC,
                    "notification.immediate",
                    message
            );

            log.info("即时通知消息发送成功: messageId={}, userId={}",
                    message.getMessageId(), message.getUserId());
            return true;

        } catch (Exception e) {
            log.error("即时通知消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * 发送延迟通知消息 - 使用延迟消息插件实现高精度延迟
     * @param message 通知消息
     * @param delaySeconds 延迟时间（秒）
     * @return 是否发送成功
     */
    public boolean sendDelayMessage(NotificationMessage message, long delaySeconds) {
        try {
            // 设置消息ID和创建时间
            if (message.getMessageId() == null) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            if (message.getCreateTime() == null) {
                message.setCreateTime(LocalDateTime.now());
            }

            // 使用延迟消息插件发送消息
            // 延迟时间以毫秒为单位
            long delayMillis = delaySeconds * 1000;
            
            rabbitTemplate.convertAndSend(
                    "notification.delay.exchange",
                    "notification.delay",
                    message,
                    new MessagePostProcessor() {
                        @Override
                        public org.springframework.amqp.core.Message postProcessMessage(
                                org.springframework.amqp.core.Message message) {
                            // 设置延迟时间（毫秒）- 使用延迟消息插件的header
                            message.getMessageProperties().setHeader("x-delay", delayMillis);
                            return message;
                        }
                    }
            );

            log.info("延迟通知消息发送成功: messageId={}, userId={}, delaySeconds={}, delayMillis={}",
                    message.getMessageId(), message.getUserId(), delaySeconds, delayMillis);
            return true;

        } catch (Exception e) {
            log.error("延迟通知消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * 发送延迟通知消息 - 使用延迟消息插件API（更简洁的方式）
     * @param message 通知消息
     * @param delaySeconds 延迟时间（秒）
     * @return 是否发送成功
     */
    public boolean sendDelayMessageV2(NotificationMessage message, long delaySeconds) {
        try {
            // 设置消息ID和创建时间
            if (message.getMessageId() == null) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            if (message.getCreateTime() == null) {
                message.setCreateTime(LocalDateTime.now());
            }

            // 使用延迟消息插件发送消息（Lambda方式）
            rabbitTemplate.convertAndSend(
                    "notification.delay.exchange",
                    "notification.delay",
                    message,
                    msg -> {
                        // 设置延迟时间（毫秒）- 使用延迟消息插件的header
                        msg.getMessageProperties().setHeader("x-delay", delaySeconds * 1000);
                        return msg;
                    }
            );

            log.info("延迟通知消息发送成功(V2): messageId={}, userId={}, delaySeconds={}",
                    message.getMessageId(), message.getUserId(), delaySeconds);
            return true;

        } catch (Exception e) {
            log.error("延迟通知消息发送失败(V2): messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * 发送延迟通知消息 - 支持毫秒级精度
     * @param message 通知消息
     * @param delayMillis 延迟时间（毫秒）
     * @return 是否发送成功
     */
    public boolean sendDelayMessageMillis(NotificationMessage message, long delayMillis) {
        try {
            // 设置消息ID和创建时间
            if (message.getMessageId() == null) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            if (message.getCreateTime() == null) {
                message.setCreateTime(LocalDateTime.now());
            }

            // 使用延迟消息插件发送消息，支持毫秒级精度
            rabbitTemplate.convertAndSend(
                    "notification.delay.exchange",
                    "notification.delay",
                    message,
                    msg -> {
                        // 设置延迟时间（毫秒）- 使用延迟消息插件的header
                        msg.getMessageProperties().setHeader("x-delay", delayMillis);
                        return msg;
                    }
            );

            log.info("延迟通知消息发送成功(毫秒级): messageId={}, userId={}, delayMillis={}",
                    message.getMessageId(), message.getUserId(), delayMillis);
            return true;

        } catch (Exception e) {
            log.error("延迟通知消息发送失败(毫秒级): messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }
}
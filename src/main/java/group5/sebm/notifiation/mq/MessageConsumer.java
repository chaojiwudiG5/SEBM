package group5.sebm.notifiation.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * RabbitMQ 消息消费者服务
 */
@Slf4j
@Service
public class MessageConsumer {

    @Autowired
    private MessageProcessor messageProcessor;

    /**
     * 消费即时通知消息
     */
    @RabbitListener(queues = "notification.immediate.queue")
    public void handleImmediateMessage(NotificationMessage message, Message amqpMessage, Channel channel) {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("接收到即时通知消息: messageId={}, userId={}, deliveryTag={}",
                    message.getMessageId(), message.getUserId(), deliveryTag);

            // 处理通知消息
            messageProcessor.processNotification(message);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("即时通知消息处理完成并确认: messageId={}, deliveryTag={}", 
                    message.getMessageId(), deliveryTag);

        } catch (Exception e) {
            log.error("即时通知消息处理异常: messageId={}, deliveryTag={}, error={}",
                    message.getMessageId(), deliveryTag, e.getMessage(), e);
            
            try {
                // 处理失败，拒绝消息并重新入队（最多重试3次）
                boolean requeue = message.getRetryCount() < message.getMaxRetryCount();
                if (requeue) {
                    message.setRetryCount(message.getRetryCount() + 1);
                    log.warn("消息处理失败，重新入队重试: messageId={}, retryCount={}", 
                            message.getMessageId(), message.getRetryCount());
                } else {
                    log.error("消息处理失败次数超过最大重试次数，拒绝消息: messageId={}", 
                            message.getMessageId());
                }
                channel.basicNack(deliveryTag, false, requeue);
            } catch (IOException ioException) {
                log.error("消息确认失败: deliveryTag={}, error={}", 
                        deliveryTag, ioException.getMessage(), ioException);
            }
        }
    }

    /**
     * 消费延迟通知消息
     */
    @RabbitListener(queues = "notification.delay.queue")
    public void handleDelayMessage(NotificationMessage message, Message amqpMessage, Channel channel) {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("接收到延迟通知消息: messageId={}, userId={}, deliveryTag={}",
                    message.getMessageId(), message.getUserId(), deliveryTag);

            // 处理通知消息
            messageProcessor.processNotification(message);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("延迟通知消息处理完成并确认: messageId={}, deliveryTag={}", 
                    message.getMessageId(), deliveryTag);

        } catch (Exception e) {
            log.error("延迟通知消息处理异常: messageId={}, deliveryTag={}, error={}",
                    message.getMessageId(), deliveryTag, e.getMessage(), e);
            
            try {
                // 处理失败，拒绝消息并重新入队（最多重试3次）
                boolean requeue = message.getRetryCount() < message.getMaxRetryCount();
                if (requeue) {
                    message.setRetryCount(message.getRetryCount() + 1);
                    log.warn("消息处理失败，重新入队重试: messageId={}, retryCount={}", 
                            message.getMessageId(), message.getRetryCount());
                } else {
                    log.error("消息处理失败次数超过最大重试次数，拒绝消息: messageId={}", 
                            message.getMessageId());
                }
                channel.basicNack(deliveryTag, false, requeue);
            } catch (IOException ioException) {
                log.error("消息确认失败: deliveryTag={}, error={}", 
                        deliveryTag, ioException.getMessage(), ioException);
            }
        }
    }

}
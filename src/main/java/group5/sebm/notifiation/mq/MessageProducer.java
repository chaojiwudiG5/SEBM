package group5.sebm.notifiation.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RocketMQ 消息生产者服务
 */
@Slf4j
@Service
public class MessageProducer {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    /**
     * 通知消息主题
     */
    private static final String NOTIFICATION_TOPIC = "notification-topic";
    
    /**
     * 延迟消息标签
     */
    private static final String DELAY_TAG = "delay";
    
    /**
     * 即时消息标签
     */
    private static final String IMMEDIATE_TAG = "immediate";
    
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
            
            // 发送消息
            rocketMQTemplate.syncSend(
                NOTIFICATION_TOPIC + ":" + IMMEDIATE_TAG,
                MessageBuilder.withPayload(message).build()
            );
            
            log.info("即时通知消息发送成功: messageId={}, userId={}",
                    message.getMessageId(), message.getUserId());
            return true;
            
        } catch (Exception e) {
            log.error("即时通知消息发送失败: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 发送延迟通知消息
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
            message.setDelaySeconds(delaySeconds);
            
            // 发送延迟消息
            rocketMQTemplate.syncSend(
                NOTIFICATION_TOPIC + ":" + DELAY_TAG,
                MessageBuilder.withPayload(message).build(),
                3000, // 发送超时时间
                getDelayLevel(delaySeconds) // 延迟级别
            );
            
            log.info("延迟通知消息发送成功: messageId={}, recipient={}, delaySeconds={}", 
                    message.getMessageId(), message.getUserId(), delaySeconds);
            return true;
            
        } catch (Exception e) {
            log.error("延迟通知消息发送失败: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            return false;
        }
    }


    /**
     * 根据延迟秒数获取 RocketMQ 延迟级别
     * RocketMQ 支持的延迟级别：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @param delaySeconds 延迟秒数
     * @return 延迟级别
     */
    private int getDelayLevel(long delaySeconds) {
        if (delaySeconds <= 1) return 1;
        if (delaySeconds <= 5) return 2;
        if (delaySeconds <= 10) return 3;
        if (delaySeconds <= 30) return 4;
        if (delaySeconds <= 60) return 5;
        if (delaySeconds <= 120) return 6;
        if (delaySeconds <= 180) return 7;
        if (delaySeconds <= 240) return 8;
        if (delaySeconds <= 300) return 9;
        if (delaySeconds <= 360) return 10;
        if (delaySeconds <= 420) return 11;
        if (delaySeconds <= 480) return 12;
        if (delaySeconds <= 540) return 13;
        if (delaySeconds <= 600) return 14;
        if (delaySeconds <= 1200) return 15;
        if (delaySeconds <= 1800) return 16;
        if (delaySeconds <= 3600) return 17;
        return 18; // 2小时
    }
}

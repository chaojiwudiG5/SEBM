package group5.sebm.notifiation.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static group5.sebm.common.constant.NotificationConstant.*;

/**
 * RocketMQ 消息生产者服务
 */
@Slf4j
@Service
public class MessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

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

            // 发送消息到RocketMQ
            rocketMQTemplate.syncSend(
                    NOTIFICATION_TOPIC + ":" + IMMEDIATE_TAG,
                    JSON.toJSONString(message)
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

            // 发送延迟消息到RocketMQ
            rocketMQTemplate.syncSendDelayTimeSeconds(
                    NOTIFICATION_TOPIC + ":" + DELAY_TAG,
                    JSON.toJSONString(message), delaySeconds // 超时时间3秒
            );

            log.info("延迟通知消息发送成功: messageId={}, userId={}, delaySeconds={}",
                    message.getMessageId(), message.getUserId(), delaySeconds);
            return true;

        } catch (Exception e) {
            log.error("延迟通知消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
        }
        return false;
    }
}
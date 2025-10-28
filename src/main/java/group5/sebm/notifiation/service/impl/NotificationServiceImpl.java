package group5.sebm.notifiation.service.impl;

import com.alibaba.fastjson.JSON;
import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.converter.NotificationConverter;
import group5.sebm.notifiation.enums.NotificationTypeEnum;
import group5.sebm.notifiation.mq.MessageProducer;
import group5.sebm.notifiation.mq.NotificationMessage;
import group5.sebm.notifiation.service.NotificationService;
import group5.sebm.notifiation.service.TemplateService;
import group5.sebm.notifiation.service.UnsubscribeService;
import group5.sebm.notifiation.service.dto.TemplateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private UnsubscribeService unsubscribeService;
    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private NotificationConverter notificationConverter;

    @Override
    public Boolean sendNotification(SendNotificationDto sendNotificationDto) {
        // 如果用户已退订该事件，则跳过发送
        try {
            if (sendNotificationDto.getUserId() != null && sendNotificationDto.getNotificationEvent() != null) {
                boolean unsub = unsubscribeService.isUnsubscribed(sendNotificationDto.getUserId(), sendNotificationDto.getNotificationEvent());
                if (unsub) {
                    log.info("用户已退订该事件，跳过发送: userId={}, event={}", sendNotificationDto.getUserId(), sendNotificationDto.getNotificationEvent());
                    return true; // 返回 true 表示处理成功但未发送
                }
            }
        } catch (Exception e) {
            log.warn("检查退订状态失败，继续发送: error={}", e.getMessage());
        }
        TemplateDto templateDto = templateService.findTemplateByParams(sendNotificationDto.getNotificationEvent());
        if (templateDto == null) {
            log.info("Template not found, request:{}", JSON.toJSONString(sendNotificationDto));
            return false;
        }
        // 构建消息
        NotificationMessage message = notificationConverter.buildNotificationMessage(sendNotificationDto, templateDto);
        // 计算延迟时间
        Long delaySeconds = calDelaySeconds(templateDto, sendNotificationDto.getNodeTimestamp());
        // 发送消息
        return sendMessage(message, delaySeconds);
    }

    /**
     * 发送消息
     */
    private boolean sendMessage(NotificationMessage message, Long delaySeconds) {
        if (delaySeconds != null && delaySeconds > 0) {
            return messageProducer.sendDelayMessage(message, delaySeconds);
        }
        return messageProducer.sendImmediateMessage(message);
    }

    /**
     * 计算通知延迟时间（秒）
     * 
     * @param templateDto 通知模板DTO
     * @param nodeTimestamp 节点时间戳（秒）
     * @return 延迟时间（秒），如果计算结果为负数则返回0
     */
    private Long calDelaySeconds(TemplateDto templateDto, Long nodeTimestamp) {
        // 参数校验
        if (templateDto == null || nodeTimestamp == null) {
            log.warn("计算延迟时间失败：模板或时间戳为空");
            return 0L;
        }
        
        // 获取当前时间戳（毫秒）
        long currentTimestamp = System.currentTimeMillis();
        
        // 获取通知类型和时间偏移
        Integer notificationType = templateDto.getNotificationType();
        Long relateTimeOffset = templateDto.getRelateTimeOffset();
        
        // 如果时间偏移为空，默认为0
        if (relateTimeOffset == null) {
            relateTimeOffset = 0L;
        }
        
        // 解析通知类型，这里转一道是为了防止不合法数值出现
        NotificationTypeEnum typeEnum = NotificationTypeEnum.parseType(notificationType);
        
        // 计算目标时间戳（毫秒）
        // 公式：目标时间 = 节点时间戳 + (通知类型系数 * 时间偏移)
        long targetTimestamp = nodeTimestamp + typeEnum.getTypeCode() * relateTimeOffset;
        
        // 计算延迟时间（秒）
        long delaySeconds = targetTimestamp - currentTimestamp/1000;
        
        // 如果延迟时间为负数，说明目标时间已过，返回0（立即发送）
        return Math.max(delaySeconds, 0L);
    }

}

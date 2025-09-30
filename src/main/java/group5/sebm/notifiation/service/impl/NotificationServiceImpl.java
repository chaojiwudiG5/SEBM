package group5.sebm.notifiation.service.impl;

import com.alibaba.fastjson.JSON;
import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.mq.MessageProducer;
import group5.sebm.notifiation.mq.NotificationMessage;
import group5.sebm.notifiation.service.NotificationService;
import group5.sebm.notifiation.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private MessageProducer messageProducer;

    @Override
    public Boolean sendNotification(SendNotificationDto sendNotificationDto) {
        TemplatePo templatePo = getTemplateIdByNode(sendNotificationDto.getNotificationNode(), sendNotificationDto.getNotificationRole());
        if (templatePo == null) {
            log.info("Template not found, request:{}", JSON.toJSONString(sendNotificationDto));
            return false;
        }
        // 2. 构建消息
        NotificationMessage message = buildNotificationMessage(sendNotificationDto, templatePo);

        // 3. 发送消息
        return sendMessage(message, sendNotificationDto.getDelaySeconds());
    }

    /**
     * 构建通知消息
     */
    private NotificationMessage buildNotificationMessage(SendNotificationDto request, TemplatePo templatePo) {
        NotificationMessage message = new NotificationMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setTemplate(templatePo);
        message.setTemplateVars(request.getTemplateVars());
        message.setDelaySeconds(request.getDelaySeconds());
        message.setCreateTime(LocalDateTime.now());

        return message;
    }

    /**
     * 发送消息
     */
    private boolean sendMessage(NotificationMessage message, Long delaySeconds) {
        if (delaySeconds != null && delaySeconds > 0) {
            return messageProducer.sendDelayMessage(message, delaySeconds);
        } else {
            return messageProducer.sendImmediateMessage(message);
        }
    }

    /**
     * 根据通知节点获取模板ID
     */
    private TemplatePo getTemplateIdByNode(Integer notificationNode, Integer notificationRole) {
        TemplatePo templatePo = templateService.findTemplateByNode(notificationNode, notificationRole);
        if (templatePo == null) {
            return null;
        }
        return templatePo; // 临时返回默认值
    }
}

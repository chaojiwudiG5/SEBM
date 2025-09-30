package group5.sebm.notifiation.mq;

import cn.hutool.core.collection.CollectionUtil;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.service.MessageSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知消息处理器
 */
@Slf4j
@Service
public class MessageProcessor {

    @Autowired
    private MessageSenderService messageSenderService;

    
    /**
     * 处理通知消息
     * @param message 通知消息
     * @return 是否处理成功
     */
    public void processNotification(NotificationMessage message) {
        try {
            log.info("开始处理通知消息: messageId={}, recipient={}", message.getMessageId(), message.getUserId());
            
            // 1. 验证消息
            if (!validateMessage(message)) {
                log.error("通知消息验证失败: messageId={}", message.getMessageId());
                return;
            }
            
            // 2. 根据通知方式处理消息
            processByMethod(message);
        } catch (Exception e) {
            log.error("处理通知消息时发生异常: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
        }
    }
    
    /**
     * 验证消息
     * @param message 消息
     * @return 是否有效
     */
    private boolean validateMessage(NotificationMessage message) {
        if (message == null) {
            log.error("消息为空");
            return false;
        }

        TemplatePo template = message.getTemplate();
        
        if (template == null || template.getUserId() == null) {
            log.error("接收者为空: messageId={}", message.getMessageId());
            return false;
        }
        
        if (CollectionUtil.isEmpty(template.getNotificationMethod())) {
            log.error("通知方式为空: messageId={}", message.getMessageId());
            return false;
        }
        
        if (!NotificationMethodEnum.isValidCode(template.getNotificationMethod())) {
            log.error("无效的通知方式: messageId={}, method={}", message.getMessageId(), template.getNotificationMethod());
            return false;
        }
        
        return true;
    }
    
    /**
     * 根据通知方式处理消息
     * @param message 消息
     * @return 是否处理成功
     */
    private void processByMethod(NotificationMessage message) {
        TemplatePo template = message.getTemplate();
        List<Integer> notificationMethods = template.getNotificationMethod();
        if(CollectionUtil.isEmpty(notificationMethods)) {
            return;
        }
        for (Integer notificationMethod : notificationMethods) {
            NotificationMethodEnum method = NotificationMethodEnum.parseMethod(notificationMethod);
            if (method == null) {
                continue;
            }
            messageSenderService.sendNotification(method, message.getUserId(), template.getTemplateTitle(), template.getContent());
        }
    }
    
    /**
     * 处理邮件通知
     * @param message 消息
     * @return 是否处理成功
     */
    private boolean processEmailNotification(NotificationMessage message) {
        try {
            log.info("处理邮件通知: messageId={}, userId={}",
                    message.getMessageId(), message.getUserId());
            
            // TODO: 实现邮件发送逻辑
            // return emailSender.send(message.getRecipient(), message.getSubject(), message.getContent());
            
            // 临时模拟成功
            log.info("邮件通知发送成功（模拟）: messageId={}", message.getMessageId());
            return true;
            
        } catch (Exception e) {
            log.error("邮件通知发送失败: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 处理短信通知
     * @param message 消息
     * @return 是否处理成功
     */
    private boolean processSmsNotification(NotificationMessage message) {
        try {
            log.info("处理短信通知: messageId={}, userId={}",
                    message.getMessageId(), message.getUserId());
            
            // TODO: 实现短信发送逻辑
            // return smsSender.send(message.getRecipient(), message.getContent());
            
            // 临时模拟成功
            log.info("短信通知发送成功（模拟）: messageId={}", message.getMessageId());
            return true;
            
        } catch (Exception e) {
            log.error("短信通知发送失败: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 处理内部消息通知
     * @param message 消息
     * @return 是否处理成功
     */
    private boolean processInternalNotification(NotificationMessage message) {
        try {
            log.info("处理内部消息通知: messageId={}, userId={}",
                    message.getMessageId(), message.getUserId());
            
            // TODO: 实现内部消息发送逻辑
            // 例如：保存到数据库、推送到WebSocket等
            
            // 临时模拟成功
            log.info("内部消息通知发送成功（模拟）: messageId={}", message.getMessageId());
            return true;
            
        } catch (Exception e) {
            log.error("内部消息通知发送失败: messageId={}, error={}", 
                    message.getMessageId(), e.getMessage(), e);
            return false;
        }
    }
}

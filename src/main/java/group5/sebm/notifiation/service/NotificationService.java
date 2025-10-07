package group5.sebm.notifiation.service;

import group5.sebm.notifiation.controller.dto.SendNotificationDto;


/**
 * 通知相关接口
 */
public interface NotificationService {
    /**
     * 发送通知
     * @param sendNotificationDto
     * @return
     */
     Boolean sendNotification(SendNotificationDto sendNotificationDto);
}

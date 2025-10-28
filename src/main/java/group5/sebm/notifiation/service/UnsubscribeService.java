package group5.sebm.notifiation.service;

/**
 * 退订服务接口
 */
public interface UnsubscribeService {
    /**
     * 退订某个事件（用户对某 notificationEvent 退订）
     * @param userId 用户ID
     * @param notificationEvent 通知事件编码
     * @return 是否成功
     */
    boolean unsubscribe(Long userId, Integer notificationEvent);

    /**
     * 判断用户是否退订某个事件
     * @param userId 用户ID
     * @param notificationEvent 通知事件编码
     * @return true 已退订
     */
    boolean isUnsubscribed(Long userId, Integer notificationEvent);
}


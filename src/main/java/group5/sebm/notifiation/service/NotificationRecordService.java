package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.notifiation.entity.NotificationRecordPo;

/**
 * 通知记录服务接口
 */
public interface NotificationRecordService extends IService<NotificationRecordPo> {
    
    /**
     * 保存通知记录
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param status 发送状态
     * @return 是否保存成功
     */
    boolean saveNotificationRecord(Long userId, String title, String content, Integer status);
    
    /**
     * 创建通知记录并返回记录ID
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param status 发送状态
     * @return 记录ID，失败返回null
     */
    Long createNotificationRecord(Long userId, String title, String content, Integer status);
    
    /**
     * 更新通知记录状态
     * @param recordId 记录ID
     * @param status 发送状态
     * @return 是否更新成功
     */
    boolean updateRecordStatus(Long recordId, Integer status);
}


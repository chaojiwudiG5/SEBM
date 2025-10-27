package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.notifiation.entity.NotificationTaskPo;

/**
 * 通知任务服务接口
 */
public interface NotificationTaskService extends IService<NotificationTaskPo> {
    
    /**
     * 创建通知任务
     * @param title 通知标题
     * @param content 通知内容
     * @param notificationRole 通知角色
     * @return 任务ID
     */
    Long createTask(String title, String content, Integer notificationRole);
}




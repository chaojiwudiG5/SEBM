package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.notifiation.controller.dto.AdminNotificationQueryDto;
import group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.entity.NotificationRecordPo;

import java.util.List;

/**
 * 通知记录服务接口
 */
public interface NotificationRecordService extends IService<NotificationRecordPo> {
    
    /**
     * 创建通知记录
     * @param notificationTaskId 通知任务ID
     * @param userId 用户ID
     * @param notificationMethod 通知方式
     * @param status 发送状态
     * @return 是否创建成功
     */
    boolean createRecord(Long notificationTaskId, Long userId, Integer notificationMethod, Integer status);
    
    /**
     * 更新通知记录状态
     * @param notificationTaskId 通知任务ID
     * @param userId 用户ID
     * @param notificationMethod 通知方式
     * @param status 发送状态
     * @param errorMsg 错误信息（可选）
     * @return 是否更新成功
     */
    boolean updateRecordStatus(Long notificationTaskId, Long userId, Integer notificationMethod, Integer status, String errorMsg);
    
    /**
     * 查询通知任务的所有记录
     * @param notificationTaskId 通知任务ID
     * @return 记录列表
     */
    List<NotificationRecordPo> getRecordsByTaskId(Long notificationTaskId);
    
    /**
     * 查询用户的某通知任务的某种方式记录
     * @param notificationTaskId 通知任务ID
     * @param userId 用户ID
     * @param notificationMethod 通知方式
     * @return 记录
     */
    NotificationRecordPo getRecord(Long notificationTaskId, Long userId, Integer notificationMethod);
    
    /**
     * 批量创建通知记录
     * @param notificationTaskId 通知任务ID
     * @param userIds 用户ID列表
     * @param notificationMethods 通知方式列表
     * @param status 初始状态
     * @return 是否创建成功
     */
    boolean batchCreateRecords(Long notificationTaskId, List<Long> userIds, List<Integer> notificationMethods, Integer status);
    
    /**
     * 标记记录为已读
     * @param recordId 记录ID
     * @return 是否标记成功
     */
    boolean markAsRead(Long recordId);
    
    /**
     * 标记用户所有未读记录为已读
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return 是否标记成功
     */
    boolean markAllAsRead(Long userId, Integer userRole);
    
    /**
     * 删除记录（软删除）
     * @param recordId 记录ID
     * @return 是否删除成功
     */
    boolean deleteRecord(Long recordId);
    
    /**
     * 批量删除记录（软删除）
     * @param recordIds 记录ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteRecords(List<Long> recordIds);
    
    /**
     * 清空用户所有已读记录（软删除）
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearUserReadRecords(Long userId);
    
    /**
     * 查询用户的未读记录数量
     * @param userId 用户ID
     * @return 未读数量
     */
    long countUnreadByUserId(Long userId);
    
    /**
     * 分页查询通知记录
     * @param queryDto 查询条件
     * @return 通知记录分页数据
     */
    Page<NotificationRecordVo> queryNotificationRecords(NotificationRecordQueryDto queryDto);
    
    /**
     * 管理员查询所有已发送的通知记录（不受用户删除状态影响）
     * @param queryDto 查询条件
     * @return 通知记录分页数据
     */
    Page<NotificationRecordVo> queryAllSentNotifications(AdminNotificationQueryDto queryDto);
    
    /**
     * 批量标记消息为已读
     * @param ids 记录ID列表
     * @return 是否标记成功
     */
    boolean batchMarkAsRead(List<Long> ids);
    
    /**
     * 删除单个通知记录（软删除）
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean deleteNotificationRecord(Long id);
    
    /**
     * 批量删除通知记录（软删除）
     * @param ids 记录ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteNotificationRecords(List<Long> ids);
    
    /**
     * 清空用户所有已读消息（软删除）
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearUserNotifications(Long userId);
}

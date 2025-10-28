package group5.sebm.notifiation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.notifiation.controller.dto.AdminNotificationQueryDto;
import group5.sebm.notifiation.controller.dto.BatchDeleteDto;
import group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.service.NotificationRecordService;
import group5.sebm.notifiation.service.UnsubscribeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 通知记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/notification/record")
@AllArgsConstructor
public class NotificationRecordController {
    
    private final NotificationRecordService notificationRecordService;
    private final UnsubscribeService unsubscribeService;

    /**
     * 分页查询通知记录
     * @param queryDto 查询条件
     * @return 通知记录分页数据
     */
    @PostMapping("/list")
    public BaseResponse<Page<NotificationRecordVo>> queryNotificationRecords(
            @RequestBody @Valid NotificationRecordQueryDto queryDto) {
        
        try {
            Page<NotificationRecordVo> result = notificationRecordService.queryNotificationRecords(queryDto);
            log.info("查询通知记录: queryDto={}, total={}", queryDto, result.getTotal());
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("查询通知记录失败: queryDto={}, error={}", queryDto, e.getMessage(), e);
        }
        return ResultUtils.success(null);
    }
    
    /**
     * 删除单个通知记录（软删除）
     * @param deleteDto 删除参数
     * @return 删除结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteNotificationRecord(@RequestBody @Valid DeleteDto deleteDto) {
        try {
            boolean result = notificationRecordService.deleteNotificationRecord(deleteDto.getId());
            log.info("删除通知记录: id={}, result={}", deleteDto.getId(), result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("删除通知记录失败: id={}, error={}", deleteDto.getId(), e.getMessage(), e);
        }
        return ResultUtils.success(false);
    }
    
    /**
     * 批量删除通知记录（软删除）
     * @param batchDeleteDto 批量删除参数
     * @return 删除结果
     */
    @PostMapping("/batchDelete")
    public BaseResponse<Boolean> batchDeleteNotificationRecords(
            @RequestBody @Valid BatchDeleteDto batchDeleteDto) {
        
        try {
            boolean result = notificationRecordService.batchDeleteNotificationRecords(batchDeleteDto.getIds());
            log.info("批量删除通知记录: count={}, result={}", batchDeleteDto.getIds().size(), result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("批量删除通知记录失败: ids={}, error={}", 
                    batchDeleteDto.getIds(), e.getMessage(), e);
        }
        return ResultUtils.success(false);
    }
    
    /**
     * 清空用户所有已读消息（软删除）
     * @param userId 用户ID
     * @return 清空结果
     */
    @PostMapping("/clear")
    public BaseResponse<Boolean> clearUserNotifications(@RequestParam Long userId) {
        try {
            boolean result = notificationRecordService.clearUserNotifications(userId);
            log.info("清空用户已读消息: userId={}, result={}", userId, result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("清空用户已读消息失败: userId={}, error={}", userId, e.getMessage(), e);
        }
        return ResultUtils.success(false);
    }
    
    /**
     * 统计用户通知数量
     * @param userId 用户ID
     * @param status 状态（可选）
     * @return 通知数量
     */
    @PostMapping("/count")
    public BaseResponse<Long> countNotifications(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        
        try {
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId)
                    .eq("isDelete", 0);
            
            if (status != null) {
                queryWrapper.eq("status", status);
            }
            
            long count = notificationRecordService.count(queryWrapper);
            
            log.info("统计用户通知数量: userId={}, status={}, count={}", userId, status, count);
            return ResultUtils.success(count);
        } catch (Exception e) {
            log.error("统计用户通知数量失败: userId={}, status={}, error={}", 
                    userId, status, e.getMessage(), e);
        }
        return ResultUtils.success(0L);
    }
    
    /**
     * 获取用户未读通知数量（发送成功）
     * @param userId 用户ID
     * @return 未读通知数量
     */
    @PostMapping("/unreadCount")
    public BaseResponse<Long> getUnreadCount(@RequestParam Long userId) {
        try {
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId)
                    .eq("status", 1)
                    .eq("readStatus", 0)
                    .eq("isDelete", 0);
            
            long count = notificationRecordService.count(queryWrapper);
            
            log.info("获取用户未读通知数量: userId={}, count={}", userId, count);
            return ResultUtils.success(count);
        } catch (Exception e) {
            log.error("获取用户未读通知数量失败: userId={}, error={}", userId, e.getMessage(), e);
        }
        return ResultUtils.success(0L);
    }

    /**
     * 标记单条消息为已读
     * @param deleteDto 删除参数（复用，包含id）
     * @return 标记结果
     */
    @PostMapping("/markAsRead")
    public BaseResponse<Boolean> markAsRead(@RequestBody @Valid DeleteDto deleteDto) {
        try {
            boolean result = notificationRecordService.markAsRead(deleteDto.getId());
            log.info("标记单条消息为已读: id={}, result={}", deleteDto.getId(), result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("标记单条消息为已读失败: id={}, error={}", 
                    deleteDto.getId(), e.getMessage(), e);
        }
        return ResultUtils.success(false);
    }

    /**
     * 批量标记消息为已读
     * @param batchDeleteDto 批量操作参数（复用，包含ids）
     * @return 标记结果
     */
    @PostMapping("/batchMarkAsRead")
    public BaseResponse<Boolean> batchMarkAsRead(@RequestBody @Valid BatchDeleteDto batchDeleteDto) {
        try {
            boolean result = notificationRecordService.batchMarkAsRead(batchDeleteDto.getIds());
            log.info("批量标记消息为已读: count={}, result={}", batchDeleteDto.getIds().size(), result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("批量标记消息为已读失败: ids={}, error={}", 
                    batchDeleteDto.getIds(), e.getMessage(), e);
        }
        return ResultUtils.success(false);
    }

    /**
     * 标记用户所有未读消息为已读
     * @param userId 用户ID
     * @param userRole 用户角色（0-管理员，1-用户，2-技工）
     * @return 标记结果
     * 
     * 注意：
     * - 如果是管理员（userRole=0），则标记所有发给管理员角色的通知为已读（不限于该userId）
     * - 如果是普通用户或技工，则只标记该用户自己的所有未读消息为已读
     */
    @PostMapping("/markAllAsRead")
    public BaseResponse<Boolean> markAllAsRead(
            @RequestParam Long userId,
            @RequestParam Integer userRole) {
        try {
            boolean result = notificationRecordService.markAllAsRead(userId, userRole);
            log.info("标记用户所有未读消息为已读: userId={}, userRole={}, result={}", userId, userRole, result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("标记用户所有未读消息为已读失败: userId={}, userRole={}, error={}", userId, userRole, e.getMessage(), e);
        }
        return ResultUtils.success(false);
    }

    /**
     * 管理员查询所有已发送的通知记录（不受用户删除状态影响）
     * @param queryDto 查询条件
     * @return 通知记录分页数据
     */
    @PostMapping("/admin/listAll")
    public BaseResponse<Page<NotificationRecordVo>> queryAllSentNotifications(
            @RequestBody @Valid AdminNotificationQueryDto queryDto) {
        
        try {
            Page<NotificationRecordVo> result = notificationRecordService.queryAllSentNotifications(queryDto);
            log.info("管理员查询所有已发送通知: userId={}, isDelete={}, total={}", 
                    queryDto.getUserId(), queryDto.getIsDelete(), result.getTotal());
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("管理员查询所有已发送通知失败: queryDto={}, error={}", queryDto, e.getMessage(), e);
        }
        return ResultUtils.success(null);
    }

    /**
     * 一键退订（针对某个 notificationEvent）
     * 前端可以在消息详情中展示一个退订链接，调用此接口即可退订该事件
     * @param userId 用户ID
     * @param notificationEvent 通知事件编码
     * @return 退订结果
     */
    @PostMapping("/unsubscribe")
    public BaseResponse<Boolean> unsubscribe(
            @RequestParam Long userId,
            @RequestParam Integer notificationEvent) {
        try {
            boolean result = unsubscribeService.unsubscribe(userId, notificationEvent);
            log.info("用户退订: userId={}, event={}, result={}", userId, notificationEvent, result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("用户退订失败: userId={}, event={}, error={}", userId, notificationEvent, e.getMessage(), e);
            return ResultUtils.success(false);
        }
    }
}

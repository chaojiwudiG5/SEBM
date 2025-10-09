package group5.sebm.notifiation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.notifiation.controller.dto.BatchDeleteDto;
import group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.service.NotificationRecordService;
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
     * 清空用户所有通知记录
     * @param userId 用户ID
     * @return 清空结果
     */
    @PostMapping("/clear")
    public BaseResponse<Boolean> clearUserNotifications(@RequestParam Long userId) {
        try {
            boolean result = notificationRecordService.clearUserNotifications(userId);
            log.info("清空用户通知记录: userId={}, result={}", userId, result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("清空用户通知记录失败: userId={}, error={}", userId, e.getMessage(), e);
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
                    .eq("isDelete", 0);
            
            long count = notificationRecordService.count(queryWrapper);
            
            log.info("获取用户未读通知数量: userId={}, count={}", userId, count);
            return ResultUtils.success(count);
        } catch (Exception e) {
            log.error("获取用户未读通知数量失败: userId={}, error={}", userId, e.getMessage(), e);
        }
        return ResultUtils.success(0L);
    }
}

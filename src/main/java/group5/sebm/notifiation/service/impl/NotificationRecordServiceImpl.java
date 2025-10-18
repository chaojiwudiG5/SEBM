package group5.sebm.notifiation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.notifiation.controller.dto.AdminNotificationQueryDto;
import group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.dao.NotificationRecordMapper;
import group5.sebm.notifiation.dao.NotificationTaskMapper;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.entity.NotificationTaskPo;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.enums.NotificationRecordStatusEnum;
import group5.sebm.notifiation.service.NotificationRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知记录服务实现类
 */
@Slf4j
@Service
@AllArgsConstructor
public class NotificationRecordServiceImpl extends ServiceImpl<NotificationRecordMapper, NotificationRecordPo>
        implements NotificationRecordService {
    
    private final NotificationTaskMapper notificationTaskMapper;
    
    @Override
    public boolean createRecord(Long notificationTaskId, Long userId, Integer notificationMethod, Integer status) {
        try {
            NotificationRecordPo record = NotificationRecordPo.builder()
                    .notificationTaskId(notificationTaskId)
                    .userId(userId)
                    .notificationMethod(notificationMethod)
                    .status(status)
                    .readStatus(0)
                    .isDelete(0)
                    .sendTime(null)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            boolean result = this.save(record);
            
            if (result) {
                log.info("创建通知记录成功: taskId={}, userId={}, method={}, status={}", 
                        notificationTaskId, userId, notificationMethod, status);
            } else {
                log.error("创建通知记录失败: taskId={}, userId={}, method={}", 
                        notificationTaskId, userId, notificationMethod);
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建通知记录时发生异常: taskId={}, userId={}, method={}, error={}", 
                    notificationTaskId, userId, notificationMethod, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateRecordStatus(Long notificationTaskId, Long userId, Integer notificationMethod, 
                                       Integer status, String errorMsg) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("notificationTaskId", notificationTaskId)
                    .eq("userId", userId)
                    .eq("notificationMethod", notificationMethod)
                    .set("status", status)
                    .set("sendTime", LocalDateTime.now())
                    .set("updateTime", LocalDateTime.now());
            
            if (errorMsg != null) {
                updateWrapper.set("errorMsg", errorMsg);
            }
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("更新通知记录状态成功: taskId={}, userId={}, method={}, status={}", 
                        notificationTaskId, userId, notificationMethod, status);
            } else {
                log.warn("更新通知记录状态失败或记录不存在: taskId={}, userId={}, method={}", 
                        notificationTaskId, userId, notificationMethod);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新通知记录状态时发生异常: taskId={}, userId={}, method={}, error={}", 
                    notificationTaskId, userId, notificationMethod, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<NotificationRecordPo> getRecordsByTaskId(Long notificationTaskId) {
        try {
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("notificationTaskId", notificationTaskId)
                    .eq("isDelete", 0)
                    .orderByAsc("userId");
            
            return this.list(queryWrapper);
        } catch (Exception e) {
            log.error("查询通知记录时发生异常: taskId={}, error={}", 
                    notificationTaskId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public NotificationRecordPo getRecord(Long notificationTaskId, Long userId, Integer notificationMethod) {
        try {
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("notificationTaskId", notificationTaskId)
                    .eq("userId", userId)
                    .eq("notificationMethod", notificationMethod)
                    .eq("isDelete", 0)
                    .last("LIMIT 1");
            
            return this.getOne(queryWrapper);
        } catch (Exception e) {
            log.error("查询通知记录时发生异常: taskId={}, userId={}, method={}, error={}", 
                    notificationTaskId, userId, notificationMethod, e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateRecords(Long notificationTaskId, List<Long> userIds, 
                                       List<Integer> notificationMethods, Integer status) {
        try {
            if (userIds == null || userIds.isEmpty() || notificationMethods == null || notificationMethods.isEmpty()) {
                log.warn("用户列表或通知方式列表为空: taskId={}", notificationTaskId);
                return false;
            }
            
            List<NotificationRecordPo> records = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            for (Long userId : userIds) {
                for (Integer method : notificationMethods) {
                    NotificationRecordPo record = NotificationRecordPo.builder()
                            .notificationTaskId(notificationTaskId)
                            .userId(userId)
                            .notificationMethod(method)
                            .status(status)
                            .readStatus(0)
                            .isDelete(0)
                            .sendTime(null)
                            .createTime(now)
                            .updateTime(now)
                            .build();
                    records.add(record);
                }
            }
            
            boolean result = this.saveBatch(records);
            
            if (result) {
                log.info("批量创建通知记录成功: taskId={}, userCount={}, methodCount={}, totalRecords={}", 
                        notificationTaskId, userIds.size(), notificationMethods.size(), records.size());
            } else {
                log.error("批量创建通知记录失败: taskId={}", notificationTaskId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量创建通知记录时发生异常: taskId={}, error={}", 
                    notificationTaskId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean markAsRead(Long recordId) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", recordId)
                    .eq("isDelete", 0)
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("标记记录为已读成功: recordId={}", recordId);
            } else {
                log.warn("标记记录为已读失败或记录不存在: recordId={}", recordId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("标记记录为已读时发生异常: recordId={}, error={}", recordId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean markAllAsRead(Long userId, Integer userRole) {
        try {
            if (userId == null) {
                log.error("用户ID不能为空");
                return false;
            }
            
            if (userRole == null) {
                // 如果前端没有传入，则默认普通用户
                userRole = 0;
            }
            
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            
            // 如果是管理员（userRole=1），则标记所有管理员角色的通知为已读
            if (userRole == 1) {
                // 需要关联查询任务表来判断角色，这里简化处理：标记该用户的所有未读
                updateWrapper.eq("userId", userId);
                log.info("管理员标记所有未读消息为已读: userId={}", userId);
            } else {
                // 普通用户或技工：按userId标记
                updateWrapper.eq("userId", userId);
                log.info("用户标记自己的未读消息为已读: userId={}, userRole={}", userId, userRole);
            }
            
            updateWrapper.eq("isDelete", 0)
                    .and(wrapper -> wrapper.eq("readStatus", 0).or().isNull("readStatus"))
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("标记所有未读消息为已读成功: userId={}, userRole={}", userId, userRole);
            } else {
                log.warn("标记所有未读消息为已读失败或无未读消息: userId={}, userRole={}", userId, userRole);
            }
            
            return result;
        } catch (Exception e) {
            log.error("标记所有未读消息为已读时发生异常: userId={}, userRole={}, error={}", 
                    userId, userRole, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteRecord(Long recordId) {
        try {
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", recordId)
                    .eq("isDelete", 0)
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("删除记录成功: recordId={}", recordId);
            } else {
                log.warn("删除记录失败或记录不存在: recordId={}", recordId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除记录时发生异常: recordId={}, error={}", recordId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean batchDeleteRecords(List<Long> recordIds) {
        try {
            if (recordIds == null || recordIds.isEmpty()) {
                log.warn("记录ID列表为空");
                return false;
            }
            
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", recordIds)
                    .eq("isDelete", 0)
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("批量删除记录成功: count={}", recordIds.size());
            } else {
                log.warn("批量删除记录失败: ids={}", recordIds);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量删除记录时发生异常: ids={}, error={}", recordIds, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean clearUserReadRecords(Long userId) {
        try {
            if (userId == null) {
                log.error("用户ID不能为空");
                return false;
            }
            
            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("userId", userId)
                    .eq("isDelete", 0)
                    .eq("readStatus", 1) // 只删除已读消息
                    .set("isDelete", 1)
                    .set("updateTime", LocalDateTime.now());
            
            boolean result = this.update(updateWrapper);
            
            if (result) {
                log.info("清空用户已读消息成功: userId={}", userId);
            } else {
                log.warn("清空用户已读消息失败或无已读消息: userId={}", userId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("清空用户已读消息时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public long countUnreadByUserId(Long userId) {
        try {
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId)
                    .eq("status", 1) // 发送成功
                    .eq("readStatus", 0) // 未读
                    .eq("isDelete", 0);
            
            return this.count(queryWrapper);
        } catch (Exception e) {
            log.error("统计用户未读消息数量时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public Page<NotificationRecordVo> queryNotificationRecords(NotificationRecordQueryDto queryDto) {
        try {
            // 验证用户ID（必填）
            if (queryDto.getUserId() == null && queryDto.getQueryRole() == 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
            }

            // 构建查询条件
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();
            
            queryWrapper.eq("status", NotificationRecordStatusEnum.SUCCESS.getCode())
                    .eq("isDelete", 0);

            // 根据queryRole查询不同维度的数据
            if(queryDto.getQueryRole() == 0) {
                // 管理员角色：查询角色为管理员的所有通知
                // 先查询notificationRole为0的任务
                QueryWrapper<NotificationTaskPo> taskWrapper = new QueryWrapper<>();
                taskWrapper.eq("notificationRole", 0);
                List<NotificationTaskPo> adminTasks = notificationTaskMapper.selectList(taskWrapper);
                
                if (adminTasks.isEmpty()) {
                    // 如果没有管理员角色的通知任务，返回空结果
                    Page<NotificationRecordVo> emptyPage = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize(), 0);
                    log.info("查询管理员通知记录成功，但没有找到管理员角色的通知任务");
                    return emptyPage;
                }
                
                // 获取所有管理员任务的ID
                List<Long> adminTaskIds = adminTasks.stream()
                        .map(NotificationTaskPo::getId)
                        .collect(Collectors.toList());
                
                // 查询这些任务的明细记录
                queryWrapper.in("notificationTaskId", adminTaskIds);
                
            } else if(queryDto.getQueryRole() == 1) {
                // 普通用户：查询该用户的通知
                queryWrapper.eq("userId", queryDto.getUserId());
            }

            // 增加已读状态查询
            if(queryDto.getReadStatus() != null){
                queryWrapper.eq("readStatus", queryDto.getReadStatus());
            }

            // 根据创建时间范围查询
            if (queryDto.getStartTime() != null) {
                LocalDateTime startDateTime = LocalDateTime.ofEpochSecond(
                    queryDto.getStartTime(), 0, java.time.ZoneOffset.ofHours(8)
                );
                queryWrapper.ge("createTime", startDateTime);
            }
            
            if (queryDto.getEndTime() != null) {
                LocalDateTime endDateTime = LocalDateTime.ofEpochSecond(
                    queryDto.getEndTime(), 0, java.time.ZoneOffset.ofHours(8)
                );
                queryWrapper.le("createTime", endDateTime);
            }

            // 按创建时间降序排列
            queryWrapper.orderByDesc("createTime");

            // 分页查询
            Page<NotificationRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
            Page<NotificationRecordPo> resultPage = this.page(page, queryWrapper);

            // 获取所有任务ID
            List<Long> taskIds = resultPage.getRecords().stream()
                    .map(NotificationRecordPo::getNotificationTaskId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询任务信息
            Map<Long, NotificationTaskPo> taskMap = new java.util.HashMap<>();
            if (!taskIds.isEmpty()) {
                List<NotificationTaskPo> tasks = notificationTaskMapper.selectBatchIds(taskIds);
                taskMap = tasks.stream().collect(Collectors.toMap(NotificationTaskPo::getId, t -> t));
            }

            // 转换为 VO
            Page<NotificationRecordVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
            Map<Long, NotificationTaskPo> finalTaskMap = taskMap;
            List<NotificationRecordVo> voList = resultPage.getRecords().stream()
                    .map(record -> convertToVo(record, finalTaskMap.get(record.getNotificationTaskId())))
                    .collect(Collectors.toList());
            voPage.setRecords(voList);

            // 如果有标题关键词，过滤结果
            if (StrUtil.isNotBlank(queryDto.getTitleKeyword())) {
                voList = voList.stream()
                        .filter(vo -> vo.getTitle() != null && vo.getTitle().contains(queryDto.getTitleKeyword()))
                        .collect(Collectors.toList());
                voPage.setRecords(voList);
                voPage.setTotal(voList.size());
            }

            log.info("查询通知记录成功: queryRole={}, userId={}, total={}, current={}, size={}",
                    queryDto.getQueryRole(), queryDto.getUserId(), voPage.getTotal(), voPage.getCurrent(), voPage.getSize());
            return voPage;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询通知记录时发生异常: queryDto={}, error={}", queryDto, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询通知记录失败");
        }
    }
    
    @Override
    public Page<NotificationRecordVo> queryAllSentNotifications(AdminNotificationQueryDto queryDto) {
        try {
            // 构建查询条件
            QueryWrapper<NotificationRecordPo> queryWrapper = new QueryWrapper<>();

            // 只查询发送成功的通知
            queryWrapper.eq("status", NotificationRecordStatusEnum.SUCCESS.getCode());

            // 如果指定了用户ID，则按用户ID查询
            if (queryDto.getUserId() != null) {
                queryWrapper.eq("userId", queryDto.getUserId());
            }

            // 如果指定了删除状态，则按删除状态查询
            if (queryDto.getIsDelete() != null) {
                queryWrapper.eq("isDelete", queryDto.getIsDelete());
            }

            // 如果指定了已读状态，则按已读状态查询
            if (queryDto.getReadStatus() != null) {
                if (queryDto.getReadStatus() == 0) {
                    // 兼容历史数据：将 readStatus 为空视为未读
                    queryWrapper.and(w -> w.eq("readStatus", 0).or().isNull("readStatus"));
                } else {
                    queryWrapper.eq("readStatus", queryDto.getReadStatus());
                }
            }

            // 根据标题关键词查询
            if (StrUtil.isNotBlank(queryDto.getTitleKeyword())) {
                // 需要关联查询任务表，这里先查记录，后过滤
            }

            // 根据创建时间范围查询
            if (queryDto.getStartTime() != null) {
                LocalDateTime startDateTime = LocalDateTime.ofEpochSecond(
                    queryDto.getStartTime(), 0, java.time.ZoneOffset.ofHours(8)
                );
                queryWrapper.ge("createTime", startDateTime);
            }
            
            if (queryDto.getEndTime() != null) {
                LocalDateTime endDateTime = LocalDateTime.ofEpochSecond(
                    queryDto.getEndTime(), 0, java.time.ZoneOffset.ofHours(8)
                );
                queryWrapper.le("createTime", endDateTime);
            }

            // 按创建时间降序排列
            queryWrapper.orderByDesc("createTime");

            // 分页查询
            Page<NotificationRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
            Page<NotificationRecordPo> resultPage = this.page(page, queryWrapper);

            // 获取所有任务ID
            List<Long> taskIds = resultPage.getRecords().stream()
                    .map(NotificationRecordPo::getNotificationTaskId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询任务信息
            Map<Long, NotificationTaskPo> taskMap = new java.util.HashMap<>();
            if (!taskIds.isEmpty()) {
                List<NotificationTaskPo> tasks = notificationTaskMapper.selectBatchIds(taskIds);
                taskMap = tasks.stream().collect(Collectors.toMap(NotificationTaskPo::getId, t -> t));
                
                // 如果有角色过滤
                if (queryDto.getNotificationRole() != null) {
                    Map<Long, NotificationTaskPo> finalTaskMap = taskMap;
                    taskMap = taskMap.entrySet().stream()
                            .filter(entry -> entry.getValue().getNotificationRole().equals(queryDto.getNotificationRole()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }
            }

            // 转换为 VO
            Page<NotificationRecordVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
            Map<Long, NotificationTaskPo> finalTaskMap = taskMap;
            List<NotificationRecordVo> voList = resultPage.getRecords().stream()
                    .filter(record -> finalTaskMap.containsKey(record.getNotificationTaskId()))
                    .map(record -> convertToVo(record, finalTaskMap.get(record.getNotificationTaskId())))
                    .collect(Collectors.toList());
            
            // 如果有标题关键词，过滤结果
            if (StrUtil.isNotBlank(queryDto.getTitleKeyword())) {
                voList = voList.stream()
                        .filter(vo -> vo.getTitle() != null && vo.getTitle().contains(queryDto.getTitleKeyword()))
                        .collect(Collectors.toList());
            }
            
            voPage.setRecords(voList);
            voPage.setTotal(voList.size());

            log.info("管理员查询所有已发送通知成功: userId={}, isDelete={}, total={}, current={}, size={}",
                    queryDto.getUserId(), queryDto.getIsDelete(), voPage.getTotal(), voPage.getCurrent(), voPage.getSize());
            return voPage;

        } catch (Exception e) {
            log.error("管理员查询所有已发送通知时发生异常: queryDto={}, error={}", queryDto, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询通知记录失败");
        }
    }
    
    @Override
    public boolean batchMarkAsRead(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID列表不能为空");
            }

            UpdateWrapper<NotificationRecordPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .eq("isDelete", 0)
                    .set("readStatus", 1)
                    .set("updateTime", LocalDateTime.now());

            boolean result = this.update(updateWrapper);

            if (result) {
                log.info("批量标记消息为已读成功: count={}", ids.size());
            } else {
                log.error("批量标记消息为已读失败: ids={}", ids);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量标记消息为已读时发生异常: ids={}, error={}", ids, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "批量标记消息为已读失败");
        }
    }
    
    @Override
    public boolean deleteNotificationRecord(Long id) {
        return deleteRecord(id);
    }
    
    @Override
    public boolean batchDeleteNotificationRecords(List<Long> ids) {
        return batchDeleteRecords(ids);
    }
    
    @Override
    public boolean clearUserNotifications(Long userId) {
        return clearUserReadRecords(userId);
    }
    
    /**
     * 将 Po 转换为 Vo
     */
    private NotificationRecordVo convertToVo(NotificationRecordPo record, NotificationTaskPo task) {
        NotificationRecordStatusEnum statusEnum = NotificationRecordStatusEnum.getByCode(record.getStatus());
        
        NotificationRecordVo.NotificationRecordVoBuilder builder = NotificationRecordVo.builder()
                .id(record.getId())
                .notificationTaskId(record.getNotificationTaskId())
                .userId(record.getUserId())
                .notificationMethod(record.getNotificationMethod())
                .status(record.getStatus())
                .statusDesc(statusEnum != null ? statusEnum.getDesc() : "未知")
                .readStatus(record.getReadStatus())
                .readStatusDesc(record.getReadStatus() == 1 ? "已读" : "未读")
                .sendTime(record.getSendTime())
                .errorMsg(record.getErrorMsg())
                .createTime(record.getCreateTime());
        
        // 添加任务信息
        if (task != null) {
            builder.title(task.getTitle())
                    .content(task.getContent())
                    .notificationRole(task.getNotificationRole());
        }
        
        return builder.build();
    }
}

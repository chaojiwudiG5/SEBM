package group5.sebm.notifiation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.notifiation.dao.NotificationRecordMapper;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.service.NotificationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知记录服务实现类
 */
@Slf4j
@Service
public class NotificationRecordServiceImpl extends ServiceImpl<NotificationRecordMapper, NotificationRecordPo> 
        implements NotificationRecordService {
    
    @Override
    public boolean saveNotificationRecord(Long userId, String title, String content, Integer status) {
        try {
            NotificationRecordPo record = NotificationRecordPo.builder()
                    .userId(userId)
                    .title(title)
                    .content(content)
                    .status(status)
                    .sendTime(LocalDateTime.now())
                    .isDelete(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            boolean result = this.save(record);
            
            if (result) {
                log.info("通知记录保存成功: userId={}, title={}, status={}", userId, title, status);
            } else {
                log.error("通知记录保存失败: userId={}, title={}", userId, title);
            }
            
            return result;
        } catch (Exception e) {
            log.error("保存通知记录时发生异常: userId={}, error={}", userId, e.getMessage(), e);
        }
        return false;
    }
    
    @Override
    public Long createNotificationRecord(Long userId, String title, String content, Integer status) {
        try {
            NotificationRecordPo record = NotificationRecordPo.builder()
                    .userId(userId)
                    .title(title)
                    .content(content)
                    .status(status)
                    .sendTime(null) // 待发送状态，发送时间为空
                    .isDelete(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            boolean result = this.save(record);
            
            if (result) {
                log.info("通知记录创建成功: recordId={}, userId={}, title={}, status={}", 
                        record.getId(), userId, title, status);
                return record.getId();
            } else {
                log.error("通知记录创建失败: userId={}, title={}", userId, title);
                return null;
            }
        } catch (Exception e) {
            log.error("创建通知记录时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean updateRecordStatus(Long recordId, Integer status) {
        try {
            NotificationRecordPo record = this.getById(recordId);
            if (record == null) {
                log.error("通知记录不存在: recordId={}", recordId);
                return false;
            }
            
            record.setStatus(status);
            record.setSendTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            
            boolean result = this.updateById(record);
            
            if (result) {
                log.info("通知记录状态更新成功: recordId={}, status={}", recordId, status);
            } else {
                log.error("通知记录状态更新失败: recordId={}", recordId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新通知记录状态时发生异常: recordId={}, error={}", recordId, e.getMessage(), e);
            return false;
        }
    }
}


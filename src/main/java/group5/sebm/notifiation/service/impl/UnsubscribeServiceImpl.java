package group5.sebm.notifiation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.notifiation.dao.UnsubscribeMapper;
import group5.sebm.notifiation.entity.UnsubscribePo;
import group5.sebm.notifiation.service.UnsubscribeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class UnsubscribeServiceImpl extends ServiceImpl<UnsubscribeMapper, UnsubscribePo>
        implements UnsubscribeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unsubscribe(Long userId, Integer notificationEvent) {
        ThrowUtils.throwIf(userId == null || userId <= 0 || notificationEvent == null,
                ErrorCode.PARAMS_ERROR, "参数错误");

        try {
            // 如果已存在未删除的退订记录，直接返回成功
            QueryWrapper<UnsubscribePo> query = new QueryWrapper<>();
            query.eq("userId", userId)
                    .eq("notificationEvent", notificationEvent)
                    .eq("isDelete", 0)
                    .last("LIMIT 1");
            UnsubscribePo existing = this.getOne(query);
            if (existing != null) {
                log.info("用户已退订：userId={}, event={}", userId, notificationEvent);
                return true;
            }

            UnsubscribePo po = UnsubscribePo.builder()
                    .userId(userId)
                    .notificationEvent(notificationEvent)
                    .isDelete(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            boolean result = this.save(po);
            if (result) {
                log.info("创建退订记录成功: userId={}, event={}", userId, notificationEvent);
            } else {
                log.error("创建退订记录失败: userId={}, event={}", userId, notificationEvent);
            }
            return result;
        } catch (Exception e) {
            log.error("退订操作异常: userId={}, event={}, error={}", userId, notificationEvent, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isUnsubscribed(Long userId, Integer notificationEvent) {
        if (userId == null || userId <= 0 || notificationEvent == null) {
            return false;
        }
        try {
            QueryWrapper<UnsubscribePo> query = new QueryWrapper<>();
            query.eq("userId", userId)
                    .eq("notificationEvent", notificationEvent)
                    .eq("isDelete", 0)
                    .last("LIMIT 1");
            UnsubscribePo po = this.getOne(query);
            return po != null;
        } catch (Exception e) {
            log.error("检查退订状态异常: userId={}, event={}, error={}", userId, notificationEvent, e.getMessage(), e);
            return false;
        }
    }
}

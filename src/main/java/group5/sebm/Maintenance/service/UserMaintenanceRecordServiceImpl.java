package group5.sebm.Maintenance.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author Luoimo
* @description 针对表【userMaintenanceRecord(设备维修报单表)】的数据库操作Service实现
* @createDate 2025-09-26 13:41:45
*/
@Service
public class UserMaintenanceRecordServiceImpl extends ServiceImpl<UserMaintenanceRecordMapper, UserMaintenanceRecordPo>
    implements UserMaintenanceRecordService {

    @Override
    public Long createMaintenanceRecord(Long userId, UserCreateDto createDto) {
        Date now = new Date();
        UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
        record.setUserId(userId);
        record.setDeviceId(createDto.getDeviceId());
        record.setDescription(createDto.getDescription());
        record.setImage(createDto.getImage());
        record.setStatus(0);
        record.setIsDelete(0);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        this.save(record);
        return record.getId();
    }

    @Override
    public Page<UserMaintenanceRecordVo> listUserMaintenanceRecords(Long userId, UserQueryDto queryDto) {
        Page<UserMaintenanceRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
        LambdaQueryWrapper<UserMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMaintenanceRecordPo::getUserId, userId)
                .eq(UserMaintenanceRecordPo::getIsDelete, 0)
                .orderByDesc(UserMaintenanceRecordPo::getCreateTime);
        if (queryDto.getDeviceId() != null) {
            wrapper.eq(UserMaintenanceRecordPo::getDeviceId, queryDto.getDeviceId());
        }
        if (queryDto.getStatus() != null) {
            wrapper.eq(UserMaintenanceRecordPo::getStatus, queryDto.getStatus());
        }
        Page<UserMaintenanceRecordPo> poPage = this.page(page, wrapper);

        var voList = poPage.getRecords().stream()
                .map(po -> {
                    UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
                    BeanUtils.copyProperties(po, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 4) 组装 Page<Vo> 返回
        Page<UserMaintenanceRecordVo> voPage =
                new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public UserMaintenanceRecordVo getUserMaintenanceRecordDetail(Long userId, Long recordId) {
        LambdaQueryWrapper<UserMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMaintenanceRecordPo::getId, recordId)
                .eq(UserMaintenanceRecordPo::getUserId, userId)
                .eq(UserMaintenanceRecordPo::getIsDelete, 0);
        UserMaintenanceRecordPo record = this.getOne(wrapper);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "报修单不存在");
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    @Override
    public Boolean cancelMaintenanceRecord(Long userId, Long recordId) {
        LambdaQueryWrapper<UserMaintenanceRecordPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserMaintenanceRecordPo::getId, recordId)
                .eq(UserMaintenanceRecordPo::getUserId, userId)
                .eq(UserMaintenanceRecordPo::getIsDelete, 0);
        UserMaintenanceRecordPo record = this.getOne(queryWrapper);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "报修单不存在");
        ThrowUtils.throwIf(!Objects.equals(record.getStatus(), 0), ErrorCode.OPERATION_ERROR,
                "报修单已处理，无法取消");

        LambdaUpdateWrapper<UserMaintenanceRecordPo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserMaintenanceRecordPo::getId, recordId)
                .eq(UserMaintenanceRecordPo::getUserId, userId);
        UserMaintenanceRecordPo update = new UserMaintenanceRecordPo();
        update.setIsDelete(1);
        update.setUpdateTime(new Date());
        boolean success = this.update(update, updateWrapper);
        ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "取消报修单失败");
        return true;
    }

}





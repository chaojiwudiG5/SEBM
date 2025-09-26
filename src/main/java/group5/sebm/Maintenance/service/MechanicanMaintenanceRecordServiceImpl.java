package group5.sebm.Maintenance.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Maintenance.controller.dto.MechanicanClaimDto;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import group5.sebm.Maintenance.dao.MechanicanMaintenanceRecordMapper;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.Maintenance.service.services.MechanicanMaintenanceRecordService;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
* @author Luoimo
* @description 针对表【mechanicanMaintenanceRecord(技工设备维修报单表)】的数据库操作Service实现
* @createDate 2025-09-26 13:41:31
*/
@Service
public class MechanicanMaintenanceRecordServiceImpl extends ServiceImpl<MechanicanMaintenanceRecordMapper, MechanicanMaintenanceRecordPo>
    implements MechanicanMaintenanceRecordService {


    private static final Set<Integer> VALID_STATUS = Set.of(0, 1, 2, 3);

    @Resource
    private UserMaintenanceRecordMapper userMaintenanceRecordMapper;

    @Override
    public Long claimMaintenanceTask(Long mechanicId, MechanicanClaimDto claimDto) {
        UserMaintenanceRecordPo userRecord = userMaintenanceRecordMapper
                .selectById(claimDto.getUserMaintenanceRecordId());
        ThrowUtils.throwIf(userRecord == null, ErrorCode.NOT_FOUND_ERROR, "用户报修单不存在");
        ThrowUtils.throwIf(userRecord.getStatus() != null && userRecord.getStatus() == 1,
                ErrorCode.OPERATION_ERROR, "报修单已处理");

        Date now = new Date();
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setDeviceId(userRecord.getDeviceId());
        record.setUserId(mechanicId);
        record.setDescription(StringUtils.hasText(claimDto.getDescription())
                ? claimDto.getDescription()
                : userRecord.getDescription());
        record.setImage(null);
        record.setStatus(1);
        record.setIsDelete(0);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        this.save(record);
        return record.getId();
    }

    @Override
    public Page<MechanicanMaintenanceRecordVo> listMechanicMaintenanceRecords(Long mechanicId, MechanicanQueryDto queryDto)
    {
        Page<MechanicanMaintenanceRecordPo> page = new Page<>(queryDto.getPageNumber(), queryDto.getPageSize());
        LambdaQueryWrapper<MechanicanMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MechanicanMaintenanceRecordPo::getUserId, mechanicId)
                .eq(MechanicanMaintenanceRecordPo::getIsDelete, 0)
                .orderByDesc(MechanicanMaintenanceRecordPo::getCreateTime);
        if (queryDto.getDeviceId() != null) {
            wrapper.eq(MechanicanMaintenanceRecordPo::getDeviceId, queryDto.getDeviceId());
        }
        if (queryDto.getStatus() != null) {
            wrapper.eq(MechanicanMaintenanceRecordPo::getStatus, queryDto.getStatus());
        }
        Page<MechanicanMaintenanceRecordPo> poPage = this.page(page, wrapper);
        var voList = poPage.getRecords().stream()
                .map(po -> {
                    MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo();
                    BeanUtils.copyProperties(po, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 4) 组装 Page<Vo> 返回
        Page<MechanicanMaintenanceRecordVo> voPage =
                new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public MechanicanMaintenanceRecordVo getMechanicMaintenanceRecordDetail(Long mechanicId,
                                                                            Long recordId) {
        LambdaQueryWrapper<MechanicanMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MechanicanMaintenanceRecordPo::getId, recordId)
                .eq(MechanicanMaintenanceRecordPo::getUserId, mechanicId)
                .eq(MechanicanMaintenanceRecordPo::getIsDelete, 0);
        MechanicanMaintenanceRecordPo record = this.getOne(wrapper);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "维修单不存在");
        MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    @Override
    public Boolean updateMechanicMaintenanceRecord(Long mechanicId, MechanicanUpdateDto updateDto) {
        ThrowUtils.throwIf(!VALID_STATUS.contains(updateDto.getStatus()), ErrorCode.PARAMS_ERROR,
                "非法的状态值");
        MechanicanMaintenanceRecordPo record = this.getById(updateDto.getId());
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "维修单不存在");
        ThrowUtils.throwIf(record.getIsDelete() != null && record.getIsDelete() == 1,
                ErrorCode.NOT_FOUND_ERROR, "维修单不存在");
        ThrowUtils.throwIf(!Objects.equals(record.getUserId(), mechanicId), ErrorCode.NO_AUTH_ERROR,
                "无权限操作该维修单");

        record.setStatus(updateDto.getStatus());
        if (StringUtils.hasText(updateDto.getDescription())) {
            record.setDescription(updateDto.getDescription());
        }
        if (StringUtils.hasText(updateDto.getImage())) {
            record.setImage(updateDto.getImage());
        }
        record.setUpdateTime(new Date());
        this.updateById(record);

        if (updateDto.getUserMaintenanceRecordId() != null
                && (updateDto.getStatus() == 2 || updateDto.getStatus() == 3)) {
            UserMaintenanceRecordPo userRecord = userMaintenanceRecordMapper
                    .selectById(updateDto.getUserMaintenanceRecordId());
            if (userRecord != null && (userRecord.getIsDelete() == null
                    || userRecord.getIsDelete() == 0)) {
                userRecord.setStatus(1);
                userRecord.setUpdateTime(new Date());
                userMaintenanceRecordMapper.updateById(userRecord);
            }
        }
        return true;
    }
}





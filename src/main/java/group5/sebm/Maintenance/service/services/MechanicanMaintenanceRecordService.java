package group5.sebm.Maintenance.service.services;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.Maintenance.controller.dto.MechanicRecordQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanClaimDto;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;

/**
* @author Luoimo
* @description 针对表【mechanicanMaintenanceRecord(技工设备维修报单表)】的数据库操作Service
* @createDate 2025-09-26 13:41:31
*/
public interface MechanicanMaintenanceRecordService extends IService<MechanicanMaintenanceRecordPo> {

    /**
     * 技工认领报修单生成维修任务
     */
    Long addMaintenanceTask(Long mechanicId, Long userMaintenanceRecordId);

    /**
     * 分页查询技工自己的维修任务
     */
    Page<MechanicanMaintenanceRecordVo> listMechanicMaintenanceRecords(Long mechanicId, MechanicanQueryDto queryDto);

    /**
     * 查看维修任务详情
     */
    MechanicanMaintenanceRecordVo getMechanicMaintenanceRecordDetail(MechanicRecordQueryDto queryDto);

    /**
     * 更新维修任务状态
     */
    Boolean updateMechanicMaintenanceRecord(Long mechanicId, MechanicanUpdateDto updateDto);
}
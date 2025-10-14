package group5.sebm.Maintenance.service.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.common.dto.UserMaintenanceRecordDto;

/**
* @author Luoimo
* @description 针对表【userMaintenanceRecord(设备维修报单表)】的数据库操作Service
* @createDate 2025-09-26 13:41:46
*/
public interface UserMaintenanceRecordService extends IService<UserMaintenanceRecordPo> {
    UserMaintenanceRecordDto updateStatus(Long recordId,Integer status);

    UserMaintenanceRecordDto getUserMaintenanceRecordDto(Long recordId);
    /**
     * 创建用户报修单
     */
    UserMaintenanceRecordVo createMaintenanceRecord(Long userId, UserCreateDto createDto);

    /**
     * 查询当前用户的报修单列表
     */
    Page<UserMaintenanceRecordVo> listUserMaintenanceRecords(Long userId, UserQueryDto queryDto);

    /**
     * 获取报修单详情
     */
    UserMaintenanceRecordVo getUserMaintenanceRecordDetail(Long userId, Long recordId);

    /**
     * 取消报修单
     */
    Boolean cancelMaintenanceRecord(Long userId, Long recordId);
}
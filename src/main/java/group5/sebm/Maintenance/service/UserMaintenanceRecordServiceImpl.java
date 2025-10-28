package group5.sebm.Maintenance.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.common.dto.BorrowRecordDto;
import group5.sebm.common.dto.UserMaintenanceRecordDto;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.service.services.DeviceService;
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

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author Luoimo
 * @description 针对表【userMaintenanceRecord(设备维修报单表)】的数据库操作Service实现
 * @createDate 2025-09-26 13:41:45
 */
@Service
@AllArgsConstructor
public class UserMaintenanceRecordServiceImpl extends
    ServiceImpl<UserMaintenanceRecordMapper, UserMaintenanceRecordPo>
    implements UserMaintenanceRecordService {

  private final UserMaintenanceRecordMapper userMaintenanceRecordMapper;

  private final DeviceService deviceService;

  private final BorrowRecordService borrowRecordService;

  @Override
  public UserMaintenanceRecordDto updateStatus(Long recordId, Integer status) {
    //1.查询报修单
    UserMaintenanceRecordPo record = userMaintenanceRecordMapper.selectById(recordId);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    //2.校验状态是否合法
    ThrowUtils.throwIf(record.getStatus() != 0, ErrorCode.OPERATION_ERROR,
        "maintenance record cannot be updated");
    //3.修改状态
    record.setStatus(status);
    record.setUpdateTime(new Date());
    int success = userMaintenanceRecordMapper.updateById(record);
    ThrowUtils.throwIf(success != 1, ErrorCode.OPERATION_ERROR, "update maintenance record status failed");
    //4.返回DTO
    UserMaintenanceRecordDto dto = new UserMaintenanceRecordDto();
    BeanUtils.copyProperties(record, dto);
    return dto;
  }

  @Override
  public UserMaintenanceRecordDto getUserMaintenanceRecordDto(Long recordId) {
    //1.查询报修单
    UserMaintenanceRecordPo record = userMaintenanceRecordMapper.selectById(recordId);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    //2.转换成DTO
    UserMaintenanceRecordDto dto = new UserMaintenanceRecordDto();
    BeanUtils.copyProperties(record, dto);
    //3.返回DTO
    return dto;
  }

  @Override
  public UserMaintenanceRecordVo createMaintenanceRecord(Long userId, UserCreateDto createDto) {
    //1.校验借用记录是否存在
    BorrowRecordDto borrowRecordDto = borrowRecordService.getBorrowRecordById(
        createDto.getBorrowRecordId());
    //2.校验设备是否被该用户借出
    ThrowUtils.throwIf(userId.longValue() != borrowRecordDto.getUserId().longValue(),
        ErrorCode.NO_AUTH_ERROR, "Device is not borrowed by this user");
    //3.创建报修单
    UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
    BeanUtils.copyProperties(createDto, record);
    record.setUserId(userId);
    record.setDeviceId(borrowRecordDto.getDeviceId());
    record.setStatus(0);  // 设置默认状态：待处理
    record.setCreateTime(new Date());
    record.setUpdateTime(new Date());
    //4.保存报修单
    int success = userMaintenanceRecordMapper.insert(record);
    ThrowUtils.throwIf(success != 1, ErrorCode.OPERATION_ERROR, "create maintenance record failed");
    //5.修改设备状态为维修中
    deviceService.updateDeviceStatus(borrowRecordDto.getDeviceId(),
        DeviceStatusEnum.MAINTENANCE.getCode());
    //6.返回报修单
    UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
    BeanUtils.copyProperties(record, vo);
    return vo;
  }

  @Override
  public Page<UserMaintenanceRecordVo> listUserMaintenanceRecords(Long userId,
      UserQueryDto queryDto) {
    //1.分页查询报修单
    QueryWrapper<UserMaintenanceRecordPo> queryWrapper = new QueryWrapper<>();
    if (userId != null){
      queryWrapper.eq("userId", userId);
    }
    if (queryDto.getStatus() != null) {
      queryWrapper.eq("status", queryDto.getStatus());
    }
    Page<UserMaintenanceRecordPo> page = userMaintenanceRecordMapper.selectPage(
        new Page<>(queryDto.getPageNumber(), queryDto.getPageSize()), queryWrapper);
    //2.转换成VO
    Page<UserMaintenanceRecordVo> voPage = new Page<>();
    BeanUtils.copyProperties(page, voPage);
    voPage.setRecords(page.getRecords().stream().map(record -> {
      UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
      BeanUtils.copyProperties(record, vo);
      DeviceVo deviceVo = deviceService.getDeviceById(record.getDeviceId());
      vo.setDeviceName(deviceVo.getDeviceName());
      return vo;
    }).collect(Collectors.toList()));
    return voPage;
  }

  @Override
  public UserMaintenanceRecordVo getUserMaintenanceRecordDetail(Long userId, Long recordId) {
    LambdaQueryWrapper<UserMaintenanceRecordPo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(UserMaintenanceRecordPo::getId, recordId)
        .eq(UserMaintenanceRecordPo::getUserId, userId)
        .eq(UserMaintenanceRecordPo::getIsDelete, 0);
    UserMaintenanceRecordPo record = userMaintenanceRecordMapper.selectOne(wrapper);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
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
    UserMaintenanceRecordPo record = userMaintenanceRecordMapper.selectOne(queryWrapper);
    ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR,
        "user maintenance record not found");
    ThrowUtils.throwIf(!Objects.equals(record.getStatus(), 0), ErrorCode.OPERATION_ERROR,
        "maintenance record cannot be cancelled");
    //1.构建更新条件并设置要更新的字段
    LambdaUpdateWrapper<UserMaintenanceRecordPo> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(UserMaintenanceRecordPo::getId, recordId)
        .eq(UserMaintenanceRecordPo::getUserId, userId)
        .set(UserMaintenanceRecordPo::getIsDelete, 1)  // 显式设置isDelete字段
        .set(UserMaintenanceRecordPo::getUpdateTime, new Date());
    //2.逻辑删除报修单
    boolean success = userMaintenanceRecordMapper.update(null, updateWrapper) == 1;
    //3.修改设备状态为可用
    success = success && (deviceService.updateDeviceStatus(record.getDeviceId(), 0) != null);
    ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "delete maintenance record failed");
    return true;
  }

}





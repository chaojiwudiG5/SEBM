package group5.sebm.BorrowRecord.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryWithStatusDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordReturnDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordRenewDto;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;
import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
import group5.sebm.BorrowRecord.entity.BorrowRecordPo;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.common.constant.BorrowConstant;
import group5.sebm.common.dto.User.UserInfoDto;
import group5.sebm.common.enums.BorrowStatusEnum;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.User.service.BorrowerService;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * @author Luoimo
 * @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service实现
 * @createDate 2025-09-26 11:27:18
 */
@Service
@AllArgsConstructor
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecordPo>
    implements BorrowRecordService {

  private final BorrowRecordMapper borrowRecordMapper;

  private final BorrowerService borrowerService;

  private final DeviceService deviceService;

  @Override
  public BorrowRecordVo borrowDevice(BorrowRecordAddDto borrowRecordAddDto,
      HttpServletRequest request) {
    //1. 获取当前用户
    UserInfoDto currentUser = borrowerService.getCurrentUserDto(request);
    //2. 检查时间区间是否合法
    //2.1 借出时间不能早于当前时间，加上10秒以防止时间误差
    ThrowUtils.throwIf(
        borrowRecordAddDto.getBorrowTime().getTime() + 1000 * 10 < System.currentTimeMillis(),
        ErrorCode.PARAMS_ERROR, "Borrow time cannot be earlier than current time");
    //2.2 应还时间不能早于借出时间
    ThrowUtils.throwIf(
        borrowRecordAddDto.getDueTime().getTime() < borrowRecordAddDto.getBorrowTime()
            .getTime(), ErrorCode.PARAMS_ERROR, "Due time cannot be earlier than borrow time");
    //2.3 应还时间不能超过7天
    ThrowUtils.throwIf(borrowRecordAddDto.getDueTime().getTime() - System.currentTimeMillis()
            > BorrowConstant.MAX_BORROW_DAYS * 24 * 60 * 60 * 1000,
        ErrorCode.PARAMS_ERROR, "Due time cannot be later than 7 days");
    //3. 判断用户是否还能借用设备
    ThrowUtils.throwIf(currentUser.getBorrowedDeviceCount() >= BorrowConstant.MAX_BORROW_COUNT,
        ErrorCode.NO_AUTH_ERROR, "Borrowed device count exceed limit");
    ThrowUtils.throwIf(currentUser.getOverdueTimes() >= BorrowConstant.MAX_OVERDUE_TIMES,
        ErrorCode.NO_AUTH_ERROR, "Overdue times exceed limit");
    //4. 修改设备状态
    DevicePo device = deviceService.getById(borrowRecordAddDto.getDeviceId());
    ThrowUtils.throwIf(device == null, ErrorCode.NOT_FOUND_ERROR, "Device not found");
    ThrowUtils.throwIf(device.getStatus() != DeviceStatusEnum.AVAILABLE.getCode(),
        ErrorCode.PARAMS_ERROR, "Device is not available");
    device.setStatus(DeviceStatusEnum.BORROWED.getCode());
    deviceService.updateById(device);
    //5. 保存记录
    BorrowRecordPo borrowRecord = new BorrowRecordPo();
    BeanUtils.copyProperties(borrowRecordAddDto, borrowRecord);
    borrowRecord.setUserId(currentUser.getId());
    borrowRecordMapper.insert(borrowRecord);
    //6. 增加用户借用设备数量
    borrowerService.updateBorrowedCount(currentUser.getId(), BorrowConstant.PLUS);
    //7. 返回结果
    BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
    BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
    return borrowRecordVo;
  }


  @Override
  public Page<BorrowRecordVo> getBorrowRecordList(BorrowRecordQueryDto borrowRecordQueryDto) {
    //1. 根据userId查询记录
    Page<BorrowRecordPo> page = new Page<>(borrowRecordQueryDto.getPageNumber(),
        borrowRecordQueryDto.getPageSize());
    //2. 分页查询
    Page<BorrowRecordPo> recordPage = borrowRecordMapper.selectPage(page,
        new QueryWrapper<BorrowRecordPo>().eq("userId", borrowRecordQueryDto.getUserId()));
    List<Long> deviceIds = recordPage.getRecords().stream().map(BorrowRecordPo::getDeviceId)
        .toList();
    //3. 查询设备信息
    List<DevicePo> deviceList = deviceService.listByIds(deviceIds);
    //4. 转换结果
    Page<BorrowRecordVo> resultPage = new Page<>();
    BeanUtils.copyProperties(recordPage, resultPage);
    resultPage.setRecords(recordPage.getRecords().stream().map(borrowRecord -> {
      BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
      BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
      DevicePo device = deviceList.stream()
          .filter(d -> d.getId().longValue() == borrowRecord.getDeviceId().longValue())
          .findFirst().orElse(null);
      if (device != null) {
        borrowRecordVo.setDeviceName(device.getDeviceName());
        borrowRecordVo.setImage(device.getImage());
      }
      return borrowRecordVo;
    }).toList());
    return resultPage;
  }

  @Override
  public Page<BorrowRecordVo> getBorrowRecordListWithStatus(
      BorrowRecordQueryWithStatusDto borrowRecordQueryWithStatusDto) {
    //1. 根据userId和status查询记录
    Page<BorrowRecordPo> page = new Page<>(borrowRecordQueryWithStatusDto.getPageNumber(),
        borrowRecordQueryWithStatusDto.getPageSize());
    //2. 分页查询
    Page<BorrowRecordPo> recordPage = borrowRecordMapper.selectPage(page,
        new QueryWrapper<BorrowRecordPo>()
            .eq("userId", borrowRecordQueryWithStatusDto.getUserId())
            .eq("status", borrowRecordQueryWithStatusDto.getStatus()));
    List<Long> deviceIds = recordPage.getRecords().stream().map(BorrowRecordPo::getDeviceId)
        .toList();
    //3. 查询设备信息
    List<DevicePo> deviceList = deviceService.listByIds(deviceIds);
    //4. 转换结果
    Page<BorrowRecordVo> resultPage = new Page<>();
    BeanUtils.copyProperties(recordPage, resultPage);
    resultPage.setRecords(recordPage.getRecords().stream().map(borrowRecord -> {
      BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
      BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
      DevicePo device = deviceList.stream()
          .filter(d -> d.getId().longValue() == borrowRecord.getDeviceId().longValue())
          .findFirst()
          .orElse(null);
      if (device != null) {
        borrowRecordVo.setDeviceName(device.getDeviceName());
        borrowRecordVo.setImage(device.getImage());
      }
      return borrowRecordVo;
    }).toList());
    return resultPage;
  }

  @Override
  public BorrowRecordVo returnDevice(BorrowRecordReturnDto borrowRecordReturnDto,
      HttpServletRequest request) {
    //1. 校验参数
    UserInfoDto currentUser = borrowerService.getCurrentUserDto(request);
    //2. 更新记录
    BorrowRecordPo borrowRecord = borrowRecordMapper.selectById(borrowRecordReturnDto.getId());
    ThrowUtils.throwIf(borrowRecord == null, ErrorCode.NOT_FOUND_ERROR, "No borrow record");
    borrowRecord.setReturnTime(borrowRecordReturnDto.getReturnTime());
    borrowRecord.setStatus(BorrowStatusEnum.RETURNED.getCode());
    if (borrowRecordReturnDto.getRemarks() != null) {
      borrowRecord.setRemarks(borrowRecordReturnDto.getRemarks());
    }
    borrowRecordMapper.updateById(borrowRecord);
    //3. 更新设备状态
    DevicePo device = deviceService.getById(borrowRecord.getDeviceId());
    ThrowUtils.throwIf(device == null, ErrorCode.NOT_FOUND_ERROR, "No device");
    device.setStatus(DeviceStatusEnum.AVAILABLE.getCode());
    deviceService.updateById(device);
    //4. 减少用户借用设备数量
    borrowerService.updateBorrowedCount(currentUser.getId(), BorrowConstant.MINUS);
    //4. 返回结果
    BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
    BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
    return borrowRecordVo;
  }

  /**
   * 续借设备
   *
   * @param borrowRecordRenewDto
   * @param request
   * @return
   */
  @Override
  @Deprecated
  public BorrowRecordVo renewDevice(BorrowRecordRenewDto borrowRecordRenewDto,
      HttpServletRequest request) {
    //1. 校验参数
    UserInfoDto currentUser = borrowerService.getCurrentUserDto(request);
    ThrowUtils.throwIf(
        currentUser.getId().longValue() != borrowRecordRenewDto.getUserId().longValue(),
        ErrorCode.NO_AUTH_ERROR, "无权限操作");
    //2. 禁止续借超过7天
    ThrowUtils.throwIf(borrowRecordRenewDto.getDueTime().getTime() - System.currentTimeMillis()
            > 7 * 24 * 60 * 60 * 1000,
        ErrorCode.PARAMS_ERROR, "续借时间超过7天");
    //3. 更新记录
    BorrowRecordPo borrowRecord = borrowRecordMapper.selectById(borrowRecordRenewDto.getId());
    ThrowUtils.throwIf(borrowRecord == null, ErrorCode.NOT_FOUND_ERROR, "借用记录不存在");

    //4. 返回结果
    BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
    BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
    return borrowRecordVo;
  }
}





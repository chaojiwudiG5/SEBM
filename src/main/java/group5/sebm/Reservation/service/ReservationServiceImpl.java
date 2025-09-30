package group5.sebm.Reservation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.Reservation.controller.dto.ReservationAddDto;
import group5.sebm.Reservation.controller.dto.ReservationCancelDto;
import group5.sebm.Reservation.controller.dto.ReservationConfirmDto;
import group5.sebm.Reservation.controller.vo.ReservationVo;
import group5.sebm.Reservation.dao.ReservationMapper;
import group5.sebm.Reservation.entity.ReservationPo;
import group5.sebm.Reservation.service.services.ReservationService;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.UserService;
import group5.sebm.User.service.UserServiceImpl;
import group5.sebm.common.dto.PageDto;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.common.enums.ReservationStatusEnum;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Luoimo
 * @description 针对表【reservation(设备预约表)】的数据库操作Service实现
 * @createDate 2025-09-28 00:18:21
 */
@Service
@AllArgsConstructor
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, ReservationPo>
    implements ReservationService {

  private final UserService userServiceImpl;

  private final DeviceService deviceService;

  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public ReservationVo reserveDevice(ReservationAddDto reservationAddDto,
      HttpServletRequest request) {
    //1. 校验参数
    //1.1 预约开始时间不能晚于结束时间
    ThrowUtils.throwIf(reservationAddDto.getReserveStart().after(reservationAddDto.getReserveEnd()),
        ErrorCode.PARAMS_ERROR, "Reservation start time cannot be after end time");
    //2.获取当前用户
    //2.1 从request中获取当前用户id
    Long userId = (Long) request.getAttribute("userId");
    //2.2 查询用户是否存在
    UserPo userPo = userServiceImpl.getById(userId);
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not found");
    //3. 保存预约信息
    ReservationPo reservationPo = new ReservationPo();
    BeanUtils.copyProperties(reservationAddDto, reservationPo);
    reservationPo.setUserId(userId);
    reservationPo.setStatus(ReservationStatusEnum.NOT_CONFIRMED.getCode());
    try {
      this.save(reservationPo);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Reservation failed");
    }
    //4. 更新设备状态
    DevicePo devicePo = deviceService.getById(reservationAddDto.getDeviceId());
    ThrowUtils.throwIf(devicePo == null, ErrorCode.NOT_FOUND_ERROR, "Device not found");
    Long deviceId = devicePo.getId();
    try {
      deviceService.updateDeviceStatus(deviceId, DeviceStatusEnum.RESERVED.getCode());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update device status failed");
    }
    //5. 返回预约信息
    ReservationVo reservationVo = new ReservationVo();
    BeanUtils.copyProperties(reservationPo, reservationVo);
    reservationVo.setUserName(userPo.getUsername());
    reservationVo.setDeviceName(devicePo.getDeviceName());
    return reservationVo;
  }

  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public Boolean cancelReservation(ReservationCancelDto reservationCancelDto,
      HttpServletRequest request) {
    //1. 获取当前用户
    //1.1 从request中获取当前用户id
    Long userId = (Long) request.getAttribute("userId");
    //2. 查询预约信息
    ReservationPo reservationPo = this.getById(reservationCancelDto.getId());
    ThrowUtils.throwIf(reservationPo == null, ErrorCode.NOT_FOUND_ERROR, "Reservation not found");
    //3. 校验预约信息
    //3.1 预约状态必须为待确认
    ThrowUtils.throwIf(reservationPo.getStatus() != ReservationStatusEnum.NOT_CONFIRMED.getCode(),
        ErrorCode.PARAMS_ERROR, "Reservation status must be NOT_CONFIRMED");
    //3.2 预约用户id必须与当前用户id一致
    ThrowUtils.throwIf(reservationPo.getUserId().longValue() != userId.longValue(),
        ErrorCode.PARAMS_ERROR,
        "Reservation user id must be current user id");
    //4. 更新预约状态
    reservationPo.setStatus(ReservationStatusEnum.CANCELED.getCode());
    try {
      this.updateById(reservationPo);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Cancel reservation failed");
    }
    //5. 更新设备状态
    DevicePo devicePo = deviceService.getById(reservationPo.getDeviceId());
    ThrowUtils.throwIf(devicePo == null, ErrorCode.NOT_FOUND_ERROR, "Device not found");
    Long deviceId = devicePo.getId();
    try {
      deviceService.updateDeviceStatus(deviceId, DeviceStatusEnum.AVAILABLE.getCode());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update device status failed");
    }
    //6. 返回成功
    return true;
  }

  @Override
  public List<ReservationVo> getReservationList(PageDto pageDto, HttpServletRequest request) {
    //1. 获取当前用户
    //1.1 从request中获取当前用户id
    Long userId = (Long) request.getAttribute("userId");
    //1.2 查询用户是否存在
    UserPo userPo = userServiceImpl.getById(userId);
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not found");
    //2.分页查询预约信息
    QueryWrapper<ReservationPo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userId", userId);
    Page<ReservationPo> page = new Page<>(pageDto.getPageNumber(), pageDto.getPageSize());
    try {
      this.page(page, queryWrapper);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Get reservation list failed");
    }
    //3. 转换成ReservationVo并返回
    List<ReservationVo> reservationVoList = new ArrayList<>();
    for (ReservationPo reservationPo : page.getRecords()) {
      ReservationVo reservationVo = new ReservationVo();
      BeanUtils.copyProperties(reservationPo, reservationVo);
      reservationVo.setUserName(userPo.getUsername());
      reservationVoList.add(reservationVo);
    }
    //4. 返回预约信息
    return reservationVoList;
  }

  @Override
  public Boolean confirmReservation(ReservationConfirmDto reservationConfirmDto,
      HttpServletRequest request) {
    //1. 获取当前用户
    //1.1 从request中获取当前用户id
    Long userId = (Long) request.getAttribute("userId");
    //1.2 查询用户是否存在
    UserPo userPo = userServiceImpl.getById(userId);
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not found");
    //2. 查询预约信息
    ReservationPo reservationPo = this.getById(reservationConfirmDto.getId());
    ThrowUtils.throwIf(reservationPo == null, ErrorCode.NOT_FOUND_ERROR, "Reservation not found");
    //3. 校验预约信息
    //3.1 预约状态必须为待确认
    ThrowUtils.throwIf(reservationPo.getStatus() != ReservationStatusEnum.NOT_CONFIRMED.getCode(),
        ErrorCode.PARAMS_ERROR, "Reservation status must be NOT_CONFIRMED");
    //3.2 预约用户id必须与当前用户id一致
    ThrowUtils.throwIf(reservationPo.getUserId().longValue() != userId.longValue(),
        ErrorCode.PARAMS_ERROR,
        "Reservation user id must be current user id");
    //4. 更新预约状态
    reservationPo.setStatus(ReservationStatusEnum.CONFIRMED.getCode());
    try {
      this.updateById(reservationPo);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Confirm reservation failed");
    }
    //5. 返回成功
    return true;
  }
}





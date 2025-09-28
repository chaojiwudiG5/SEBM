package group5.sebm.Device.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Device.controller.dto.DeviceAddDto;
import group5.sebm.Device.controller.dto.DeviceQueryDto;
import group5.sebm.Device.controller.dto.DeviceUpdateDto;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.common.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Device")
@RequestMapping("/device")
@AllArgsConstructor
public class DeviceController {

  private final DeviceService deviceService;

  @PostMapping("/getDeviceList")
  public BaseResponse<List<DeviceVo>> getDeviceList(@RequestBody @Valid DeviceQueryDto deviceQueryDto) {
    Page<DeviceVo> deviceVoPage = deviceService.getDeviceList(deviceQueryDto);
    log.info("GetDeviceList called with pageDto: {}, deviceVoPage: {}", deviceQueryDto, deviceVoPage);
    return ResultUtils.success(deviceVoPage.getRecords()); // 返回List
  }

  @GetMapping("/getDevice/{id}")
  public BaseResponse<DeviceVo> getDevice(@PathVariable("id") Long id) {
    DeviceVo deviceVo = deviceService.getDeviceById(id);
    log.info("GetDeviceById called with id: {}, deviceVo: {}", id, deviceVo);
    return ResultUtils.success(deviceVo); // 返回单个DeviceVo
  }

  @PostMapping("/addDevice")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Long> addDevice(@RequestBody @Valid DeviceAddDto deviceAddDto) {
    Long id = deviceService.addDevice(deviceAddDto);
    log.info("AddDevice called with deviceAddDto: {}, id: {}", deviceAddDto, id);
    return ResultUtils.success(id); // 返回新增的id
  }

  @PostMapping("/updateDevice")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Long> updateDevice(@RequestBody @Valid DeviceUpdateDto deviceUpdateDto) {
    Long id = deviceService.updateDevice(deviceUpdateDto);
    log.info("UpdateDevice called with deviceUpdateDto: {}, id: {}", deviceUpdateDto, id);
    return ResultUtils.success(id); // 返回新增的id
  }

  @PostMapping("/updateDeviceStatus")
  public BaseResponse<Boolean> updateDeviceStatus(Long deviceId, Integer status) {
    Boolean result = deviceService.updateDeviceStatus(deviceId, status);
    log.info("UpdateDeviceStatus called with deviceId: {}, status: {}, result: {}", deviceId,
        status, result);
    return ResultUtils.success(result); // 返回更新的状态
  }

  @PostMapping("/deleteDevice")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Boolean> deleteDevice(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean result = deviceService.removeDeviceById(deleteDto);
    log.info("DeleteDevice called with deleteDto: {}, result: {}", deleteDto, result);
    return ResultUtils.success(result); // 返回删除的id
  }
}

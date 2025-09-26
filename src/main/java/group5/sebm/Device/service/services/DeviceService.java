package group5.sebm.Device.service.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.Device.controller.dto.DeviceAddDto;
import group5.sebm.Device.controller.dto.DeviceUpdateDto;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.common.dto.DeleteDto;
import jakarta.validation.Valid;

/**
 * @author Luoimo
 * @description 针对表【device(设备表)】的数据库操作Service
 * @createDate 2025-09-26 11:29:28
 */
public interface DeviceService extends IService<DevicePo> {

  Page<DeviceVo> getDeviceList(PageDto pageDto);

  DeviceVo getDeviceById(Long id);

  Long addDevice(DeviceAddDto deviceAddDto);

  Long updateDevice(DeviceUpdateDto deviceUpdateDto);

  Boolean removeDeviceById(DeleteDto deleteDto);

}

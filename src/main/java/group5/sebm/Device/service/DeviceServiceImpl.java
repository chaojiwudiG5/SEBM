package group5.sebm.Device.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Device.controller.dto.DeviceAddDto;
import group5.sebm.Device.controller.dto.DeviceUpdateDto;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.dao.DeviceMapper;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.common.dto.DeleteDto;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
* @author Luoimo
* @description 针对表【device(设备表)】的数据库操作Service实现
* @createDate 2025-09-26 11:29:28
*/
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DevicePo>
    implements DeviceService {

  @Resource
  private DeviceMapper deviceMapper;

  /**
   * 获取设备列表
   * @param pageDto
   * @return
   */
  @Override
  public Page<DeviceVo> getDeviceList(PageDto pageDto) {
    // 1. 获取分页参数
    int pageNum = pageDto.getPageNumber();
    int pageSize = pageDto.getPageSize();

    // 2. 构建分页对象
    Page<DevicePo> page = new Page<>(pageNum, pageSize);

    // 3. 查询数据库（可以加条件，比如只查有效设备）
    Page<DevicePo> devicePage = deviceMapper.selectPage(page, new QueryWrapper<>());

    // 4. 转换为 VO 对象
    List<DeviceVo> deviceVoList = devicePage.getRecords().stream()
        .map(device -> {
          DeviceVo vo = new DeviceVo();
          BeanUtils.copyProperties(device, vo); // 复制属性
          return vo;
        })
        .collect(Collectors.toList());

    // 5. 封装成新的分页对象（Page<DeviceVo>）
    Page<DeviceVo> resultPage = new Page<>(pageNum, pageSize, devicePage.getTotal());
    resultPage.setRecords(deviceVoList);

    return resultPage;
  }
  /**
   * 根据id获取设备
   * @param id
   * @return
   */
  @Override
  public DeviceVo getDeviceById(Long id) {
    //1. 根据id查询设备
    DevicePo device = deviceMapper.selectById(id);
    //2. 转换为 VO 对象
    DeviceVo vo = new DeviceVo();
    BeanUtils.copyProperties(device, vo); // 复制属性
    return vo;
  }
  /**
   * 新增设备
   * @param deviceAddDto
   * @return
   */
  @Override
  public Long addDevice(DeviceAddDto deviceAddDto) {
    //1. 转换为实体对象
    DevicePo device = new DevicePo();
    BeanUtils.copyProperties(deviceAddDto, device); // 复制属性
    //2. 插入数据库
    deviceMapper.insert(device);
    //3. 返回id
    return device.getId();
  }
  /**
   * 更新设备
   * @param deviceUpdateDto
   * @return
   */
  @Override
  public Long updateDevice(DeviceUpdateDto deviceUpdateDto) {
    //1. 转换为实体对象
    DevicePo device = new DevicePo();
    BeanUtils.copyProperties(deviceUpdateDto, device); // 复制属性
    //2. 更新数据库
    deviceMapper.updateById(device);
    //3. 返回id
    return device.getId();
  }
  /**
   * 根据id删除设备
   * @param deleteDto
   * @return
   */
  @Override
  public Boolean removeDeviceById(DeleteDto deleteDto) {
    //1. 根据id删除设备
    deviceMapper.deleteById(deleteDto.getId());
    //2. 返回成功
    return true;
  }

}





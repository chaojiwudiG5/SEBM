package group5.sebm.DeviceServiceTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Device.controller.DeviceController;
import group5.sebm.Device.controller.dto.DeviceAddDto;
import group5.sebm.Device.controller.dto.DeviceQueryDto;
import group5.sebm.Device.controller.dto.DeviceUpdateDto;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.common.BaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceControllerTest {

    private DeviceService deviceService;
    private DeviceController deviceController;

    @BeforeEach
    public void setUp() {
        deviceService = mock(DeviceService.class);
        deviceController = new DeviceController(deviceService);
    }

    @Test
    public void testGetDeviceList() {
        DeviceQueryDto queryDto = new DeviceQueryDto();
        Page<DeviceVo> page = new Page<>();
        DeviceVo vo = new DeviceVo();
        vo.setId(1L);
        page.setRecords(Collections.singletonList(vo));

        when(deviceService.getDeviceList(queryDto)).thenReturn(page);

        BaseResponse<List<DeviceVo>> response = deviceController.getDeviceList(queryDto);
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals(1L, response.getData().get(0).getId());
    }

    @Test
    public void testGetDevice() {
        DeviceVo vo = new DeviceVo();
        vo.setId(1L);
        when(deviceService.getDeviceById(1L)).thenReturn(vo);

        BaseResponse<DeviceVo> response = deviceController.getDevice(1L);
        assertNotNull(response);
        assertEquals(1L, response.getData().getId());
    }

    @Test
    public void testAddDevice() {
        DeviceAddDto addDto = new DeviceAddDto();
        when(deviceService.addDevice(addDto)).thenReturn(1L);

        BaseResponse<Long> response = deviceController.addDevice(addDto);
        assertNotNull(response);
        assertEquals(1L, response.getData());
    }

    @Test
    public void testUpdateDevice() {
        DeviceUpdateDto updateDto = new DeviceUpdateDto();
        DeviceVo vo = new DeviceVo();
        vo.setId(1L);

        when(deviceService.updateDevice(updateDto)).thenReturn(vo);

        BaseResponse<DeviceVo> response = deviceController.updateDevice(updateDto);
        assertNotNull(response);
        assertEquals(1L, response.getData().getId());
    }

    @Test
    public void testUpdateDeviceStatus() {
        DeviceVo vo = new DeviceVo();
        vo.setId(1L);
        when(deviceService.updateDeviceStatus(1L, 1)).thenReturn(vo);

        BaseResponse<DeviceVo> response = deviceController.updateDeviceStatus(1L, 1);
        assertNotNull(response);
        assertEquals(1L, response.getData().getId());
    }

    @Test
    public void testDeleteDevice() {
        when(deviceService.deleteDevice(ArgumentMatchers.any())).thenReturn(true);

        BaseResponse<Boolean> response = deviceController.deleteDevice(new group5.sebm.common.dto.DeleteDto());
        assertNotNull(response);
        assertTrue(response.getData());
    }
}

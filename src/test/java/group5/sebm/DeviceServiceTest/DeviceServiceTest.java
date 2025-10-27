package group5.sebm.DeviceServiceTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Device.controller.dto.DeviceAddDto;
import group5.sebm.Device.controller.dto.DeviceQueryDto;
import group5.sebm.Device.controller.dto.DeviceUpdateDto;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.dao.DeviceMapper;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.Device.service.DeviceServiceImpl;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 单元测试：DeviceServiceImpl
 */
class DeviceServiceTest {

    @Mock
    private DeviceMapper deviceMapper;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDeviceList() {
        // 模拟分页数据
        DevicePo po = new DevicePo();
        po.setId(1L);
        po.setDeviceName("Camera");
        po.setDeviceType("Sensor");
        po.setStatus(0);

        Page<DevicePo> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(Collections.singletonList(po));

        when(deviceMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 构造查询条件
        DeviceQueryDto queryDto = new DeviceQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        Page<DeviceVo> result = deviceService.getDeviceList(queryDto);

        assertEquals(1, result.getTotal());
        assertEquals("Camera", result.getRecords().get(0).getDeviceName());
        verify(deviceMapper, times(1)).selectPage(any(Page.class), any());
    }
    @Test
    void testGetDeviceList_withAllFilters() {
        // Arrange
        DeviceQueryDto dto = new DeviceQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setDeviceName("Camera");
        dto.setDeviceType("Sensor");
        dto.setStatus(0);
        dto.setLocation("Building A");

        DevicePo mockDevice = new DevicePo();
        mockDevice.setDeviceName("Camera 1");
        mockDevice.setDeviceType("Sensor");
        mockDevice.setStatus(0);
        mockDevice.setLocation("Building A");

        Page<DevicePo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(mockDevice));
        mockPage.setTotal(1);

        when(deviceMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<DeviceVo> result = deviceService.getDeviceList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("Camera 1", result.getRecords().get(0).getDeviceName());

        // Capture the query wrapper used
        ArgumentCaptor<QueryWrapper<DevicePo>> captor = ArgumentCaptor.forClass(QueryWrapper.class);
        verify(deviceMapper).selectPage(any(Page.class), captor.capture());

        QueryWrapper<DevicePo> usedWrapper = captor.getValue();
        assertNotNull(usedWrapper);

        // 验证 mapper 调用过一次
        verify(deviceMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testGetDeviceList_withNoFilters() {
        // Arrange
        DeviceQueryDto dto = new DeviceQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(5);

        Page<DevicePo> mockPage = new Page<>(1, 5);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        when(deviceMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<DeviceVo> result = deviceService.getDeviceList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        verify(deviceMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testGetDeviceList_withPartialFilters() {
        // Arrange
        DeviceQueryDto dto = new DeviceQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setDeviceName("Camera");

        DevicePo device = new DevicePo();
        device.setDeviceName("Camera X");

        Page<DevicePo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(device));
        mockPage.setTotal(1);

        when(deviceMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<DeviceVo> result = deviceService.getDeviceList(dto);

        // Assert
        assertEquals(1, result.getTotal());
        assertEquals("Camera X", result.getRecords().get(0).getDeviceName());
        verify(deviceMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testGetDeviceById() {
        DevicePo po = new DevicePo();
        po.setId(1L);
        po.setDeviceName("Camera");

        when(deviceMapper.selectById(1L)).thenReturn(po);

        DeviceVo vo = deviceService.getDeviceById(1L);

        assertEquals(1L, vo.getId());
        assertEquals("Camera", vo.getDeviceName());
    }

    @Test
    void testAddDevice() {
        DeviceAddDto dto = new DeviceAddDto();
        dto.setDeviceName("Camera");

        DevicePo po = new DevicePo();
        po.setId(123L);
        po.setDeviceName("Camera");

        doAnswer(invocation -> {
            DevicePo arg = invocation.getArgument(0);
            arg.setId(123L);
            return 1;
        }).when(deviceMapper).insert(any(DevicePo.class));

        Long id = deviceService.addDevice(dto);
        assertEquals(123L, id);
        verify(deviceMapper, times(1)).insert(any(DevicePo.class));
    }

    @Test
    void testUpdateDevice() {
        DeviceUpdateDto dto = new DeviceUpdateDto();
        dto.setId(1L);
        dto.setDeviceName("UpdatedCam");

        when(deviceMapper.updateById(any(DevicePo.class))).thenReturn(1);

        DeviceVo vo = deviceService.updateDevice(dto);
        assertEquals("UpdatedCam", vo.getDeviceName());
        verify(deviceMapper, times(1)).updateById(any(DevicePo.class));
    }

    @Test
    void testDeleteDevice_Success() {
        DeleteDto dto = new DeleteDto();
        dto.setId(1L);

        when(deviceMapper.deleteById(1L)).thenReturn(1);

        boolean result = deviceService.deleteDevice(dto);
        assertTrue(result);
        verify(deviceMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDevice_Fail() {
        DeleteDto dto = new DeleteDto();
        dto.setId(999L);

        doThrow(new RuntimeException("DB Error")).when(deviceMapper).deleteById(999L);

        assertThrows(BusinessException.class, () -> deviceService.deleteDevice(dto));
    }

    @Test
    void testUpdateDeviceStatus() {
        DevicePo po = new DevicePo();
        po.setId(1L);
        po.setDeviceName("Cam");
        po.setStatus(0);

        when(deviceMapper.selectById(1L)).thenReturn(po);
        when(deviceMapper.updateById(any(DevicePo.class))).thenReturn(1);

        DeviceVo vo = deviceService.updateDeviceStatus(1L, 1);
        assertEquals(1, vo.getStatus());
        verify(deviceMapper, times(1)).updateById(any(DevicePo.class));
    }
}

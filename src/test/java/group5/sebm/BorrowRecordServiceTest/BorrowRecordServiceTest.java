package group5.sebm.BorrowRecordServiceTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryWithStatusDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordReturnDto;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;
import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
import group5.sebm.BorrowRecord.entity.BorrowRecordPo;
import group5.sebm.BorrowRecord.service.BorrowRecordServiceImpl;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.User.service.UserServiceInterface.BorrowerService;
import group5.sebm.common.constant.BorrowConstant;
import group5.sebm.common.dto.UserDto;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;

import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowRecordServiceTest {

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @Mock
    private BorrowerService borrowerService;

    @Mock
    private DeviceService deviceService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BorrowRecordServiceImpl borrowRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBorrowDevice_Success() {
        BorrowRecordAddDto dto = new BorrowRecordAddDto();
        dto.setDeviceId(1L);
        dto.setBorrowTime(new Timestamp(System.currentTimeMillis() + 5000)); // 未来时间
        dto.setDueTime(new Timestamp(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000)); // 2天后

        UserDto mockUser = new UserDto();
        mockUser.setId(10L);
        mockUser.setBorrowedDeviceCount(0);
        mockUser.setMaxBorrowedDeviceCount(3);
        mockUser.setOverdueTimes(0);
        mockUser.setMaxOverdueTimes(3);

        DevicePo mockDevice = new DevicePo();
        mockDevice.setId(1L);
        mockDevice.setStatus(DeviceStatusEnum.AVAILABLE.getCode());

        when(borrowerService.getCurrentUserDtoFromID(10L)).thenReturn(mockUser);
        when(deviceService.getById(1L)).thenReturn(mockDevice);
        DeviceVo mockDeviceVo = new DeviceVo();
        when(deviceService.updateDeviceStatus(anyLong(), anyInt())).thenReturn(mockDeviceVo);

        when(borrowerService.updateBorrowedCount(anyLong(), anyInt())).thenReturn(true);


        BorrowRecordVo result = borrowRecordService.borrowDevice(dto, 10L);

        assertNotNull(result);
        assertEquals(dto.getDeviceId(), result.getDeviceId());
        verify(borrowRecordMapper).insert(any(BorrowRecordPo.class));
        verify(deviceService).updateDeviceStatus(1L, DeviceStatusEnum.BORROWED.getCode());
        verify(borrowerService).updateBorrowedCount(10L, BorrowConstant.PLUS);
    }

    @Test
    void testBorrowDevice_BorrowTimeTooEarly() {
        BorrowRecordAddDto dto = new BorrowRecordAddDto();

        // 使用固定的过去时间，避免时间依赖
        long fixedTime = 1609459200000L; // 2021-01-01 00:00:00
        dto.setBorrowTime(new Timestamp(fixedTime - 10000)); // 明确的过去时间
        dto.setDueTime(new Timestamp(fixedTime + 3600 * 1000)); // 明确的未来时间

        // 模拟用户信息，确保不触发借用次数限制
        UserDto mockUser = new UserDto();
        mockUser.setId(10L);
        mockUser.setOverdueTimes(0);
        mockUser.setMaxOverdueTimes(3);
        mockUser.setBorrowedDeviceCount(0);
        mockUser.setMaxBorrowedDeviceCount(5);

        when(borrowerService.getCurrentUserDtoFromID(10L)).thenReturn(mockUser);

        // 模拟设备存在且可用，避免设备状态检查触发异常
        DevicePo mockDevice = new DevicePo();
        mockDevice.setId(1L);
        mockDevice.setStatus(DeviceStatusEnum.AVAILABLE.getCode());
        when(deviceService.getById(anyLong())).thenReturn(mockDevice);

        // 断言只触发 Borrow time 异常
        BusinessException ex = assertThrows(BusinessException.class,
                () -> borrowRecordService.borrowDevice(dto, 10L));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Borrow time cannot be earlier"));
    }


    @Test
    void testBorrowDevice_DeviceNotFound() {
        BorrowRecordAddDto dto = new BorrowRecordAddDto();
        dto.setDeviceId(1L);
        dto.setBorrowTime(new Timestamp(System.currentTimeMillis() + 10000));
        dto.setDueTime(new Timestamp(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000));

        UserDto mockUser = new UserDto();
        mockUser.setId(10L);
        mockUser.setBorrowedDeviceCount(0);
        mockUser.setMaxBorrowedDeviceCount(3);
        mockUser.setOverdueTimes(0);
        mockUser.setMaxOverdueTimes(3);

        when(borrowerService.getCurrentUserDtoFromID(10L)).thenReturn(mockUser);
        when(deviceService.getById(1L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> borrowRecordService.borrowDevice(dto, 10L));

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Device not found"));
    }

    @Test
    void testReturnDevice_Success() {
        BorrowRecordReturnDto dto = new BorrowRecordReturnDto();
        dto.setId(1L);
        dto.setLongitude(String.valueOf(BorrowConstant.CENTER_LONGITUDE_HOME));
        dto.setLatitude(String.valueOf(BorrowConstant.CENTER_LATITUDE_HOME));
        dto.setReturnTime(new Timestamp(System.currentTimeMillis()));

        UserDto mockUser = new UserDto();
        mockUser.setId(10L);

        BorrowRecordPo recordPo = new BorrowRecordPo();
        recordPo.setId(1L);
        recordPo.setDeviceId(2L);
        recordPo.setUserId(10L);

        DevicePo devicePo = new DevicePo();
        devicePo.setId(2L);
        devicePo.setStatus(DeviceStatusEnum.BORROWED.getCode());

        when(borrowerService.getCurrentUserDtoFromID(10L)).thenReturn(mockUser);
        when(borrowRecordMapper.selectById(1L)).thenReturn(recordPo);
        when(deviceService.getById(2L)).thenReturn(devicePo);

        BorrowRecordVo result = borrowRecordService.returnDevice(dto, 10L);

        assertNotNull(result);
        assertEquals(recordPo.getId(), result.getId());
        verify(deviceService).updateDeviceStatus(2L, DeviceStatusEnum.AVAILABLE.getCode());
        verify(borrowerService).updateBorrowedCount(10L, BorrowConstant.MINUS);
    }

    @Test
    void testReturnDevice_OutOfGeoFence() {
        BorrowRecordReturnDto dto = new BorrowRecordReturnDto();
        dto.setId(1L);
        dto.setLongitude(String.valueOf(999.0)); // 不在范围
        dto.setLatitude(String.valueOf(999.0));
        dto.setReturnTime(new Timestamp(System.currentTimeMillis()));

        UserDto mockUser = new UserDto();
        mockUser.setId(10L);

        when(borrowerService.getCurrentUserDtoFromID(10L)).thenReturn(mockUser);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> borrowRecordService.returnDevice(dto, 10L));

        assertEquals(ErrorCode.FORBIDDEN_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Out of geofence"));
    }

    @Test
    public void testGetBorrowRecordById() {
        BorrowRecordPo po = new BorrowRecordPo();
        po.setId(1L);

        // 模拟Mapper返回
        when(borrowRecordMapper.selectById(1L)).thenReturn(po);

        // 调用Service
        var dto = borrowRecordService.getBorrowRecordById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());

        // 验证Mapper被调用
        verify(borrowRecordMapper, times(1)).selectById(1L);
    }
    @Test
    public void testGetBorrowRecordList() {
        BorrowRecordQueryDto queryDto = new BorrowRecordQueryDto();
        queryDto.setUserId(100L);
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        BorrowRecordPo po = new BorrowRecordPo();
        po.setId(3L);
        po.setDeviceId(10L);

        Page<BorrowRecordPo> poPage = new Page<>();
        poPage.setRecords(Collections.singletonList(po));

        DevicePo device = new DevicePo();
        device.setId(10L);
        device.setDeviceName("Device-A");
        device.setImage("img.jpg");

        when(borrowRecordMapper.selectPage(any(), any())).thenReturn(poPage);
        when(deviceService.listByIds(anyList())).thenReturn(Collections.singletonList(device));

        Page<BorrowRecordVo> result = borrowRecordService.getBorrowRecordList(queryDto);

        assertNotNull(result.getRecords());
        assertEquals(1, result.getRecords().size());
        assertEquals("Device-A", result.getRecords().get(0).getDeviceName());
    }
    @Test
    public void testGetBorrowRecordListWithStatus() {
        // 1. 构造查询参数
        BorrowRecordQueryWithStatusDto queryDto = new BorrowRecordQueryWithStatusDto();
        queryDto.setUserId(100L);
        queryDto.setStatus(1); // 假设状态为已借
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        // 2. 构造 BorrowRecordPo 数据
        BorrowRecordPo po = new BorrowRecordPo();
        po.setId(5L);
        po.setDeviceId(20L);
        po.setStatus(1);

        Page<BorrowRecordPo> poPage = new Page<>();
        poPage.setRecords(Collections.singletonList(po));

        // 3. 构造 DevicePo 数据
        DevicePo device = new DevicePo();
        device.setId(20L);
        device.setDeviceName("Device-B");
        device.setImage("imageB.jpg");

        // 4. Mock 依赖方法
        when(borrowRecordMapper.selectPage(any(), any())).thenReturn(poPage);
        when(deviceService.listByIds(anyList())).thenReturn(Collections.singletonList(device));

        // 5. 调用测试方法
        Page<BorrowRecordVo> result = borrowRecordService.getBorrowRecordListWithStatus(queryDto);

        // 6. 验证结果
        assertNotNull(result.getRecords());
        assertEquals(1, result.getRecords().size());

        BorrowRecordVo vo = result.getRecords().get(0);
        assertEquals(5L, vo.getId());
        assertEquals("Device-B", vo.getDeviceName());
        assertEquals("imageB.jpg", vo.getImage());

        // 7. 验证依赖调用次数
        verify(borrowRecordMapper, times(1)).selectPage(any(), any());
        verify(deviceService, times(1)).listByIds(anyList());
    }
    @Test
    public void testSendDelayNotification() throws Exception {
        // 1. 构造测试数据
        BorrowRecordPo borrowRecord = new BorrowRecordPo();
        borrowRecord.setId(1L);
        borrowRecord.setBorrowTime(new Date());
        borrowRecord.setDueTime(new Date(System.currentTimeMillis() + 3600_000)); // 1小时后
        borrowRecord.setDeviceId(10L);

        DevicePo device = new DevicePo();
        device.setId(10L);
        device.setDeviceName("Device-A");

        UserDto user = new UserDto();
        user.setId(100L);
        user.setUsername("Tommy");

        // 2. Mock 发送通知
        when(notificationService.sendNotification(any())).thenReturn(true);

        // 3. 通过反射调用 private 方法
        var method = BorrowRecordServiceImpl.class.getDeclaredMethod(
                "sendDelayNotification", BorrowRecordPo.class, DevicePo.class, UserDto.class);
        method.setAccessible(true);
        method.invoke(borrowRecordService, borrowRecord, device, user);

        // 4. 验证调用
        verify(notificationService, times(1)).sendNotification(any(SendNotificationDto.class));
    }


}

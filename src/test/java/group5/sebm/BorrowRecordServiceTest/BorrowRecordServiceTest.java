package group5.sebm.BorrowRecordServiceTest;

import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
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
import group5.sebm.notifiation.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.Timestamp;
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
        // 设置借出时间为过去 10 秒，确保触发 Borrow time 检查
        dto.setBorrowTime(new Timestamp(System.currentTimeMillis() - 10000));
        // 设置应还时间为未来，避免触发 dueTime 检查
        dto.setDueTime(new Timestamp(System.currentTimeMillis() + 3600 * 1000));

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

}

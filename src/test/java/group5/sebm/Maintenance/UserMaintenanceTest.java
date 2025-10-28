package group5.sebm.Maintenance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.Maintenance.service.UserMaintenanceRecordServiceImpl;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import group5.sebm.common.dto.BorrowRecordDto;
import group5.sebm.common.dto.UserMaintenanceRecordDto;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Arrays;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMaintenanceTest {

  @Mock
  private UserMaintenanceRecordMapper userMaintenanceRecordMapper;

  @Mock
  private DeviceService deviceService;

  @Mock
  private BorrowRecordService borrowRecordService;

  @InjectMocks
  private UserMaintenanceRecordServiceImpl maintenanceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testUpdateStatus_Success() {
    // 模拟数据库已有的记录
    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setStatus(0);

    // 当 Mapper 调用 selectById 时返回上面的 po
    when(userMaintenanceRecordMapper.selectById(1L)).thenReturn(po);

    // 当 Mapper 调用 updateById 时返回 1（表示成功）
    when(userMaintenanceRecordMapper.updateById(any(UserMaintenanceRecordPo.class))).thenReturn(1);

    // 调用 service
    var dto = maintenanceService.updateStatus(1L, 1); // 第一个参数是 recordId，第二个是新状态

    // 断言
    assertNotNull(dto);
    assertEquals(1, dto.getStatus());

    // 验证 Mapper 方法被调用
    verify(userMaintenanceRecordMapper).selectById(1L);
    verify(userMaintenanceRecordMapper).updateById(any(UserMaintenanceRecordPo.class));
  }


  @Test
  void testUpdateStatus_RecordNotFound() {
    when(userMaintenanceRecordMapper.selectById(1L)).thenReturn(null);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> maintenanceService.updateStatus(1L, 1));

    assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testCreateMaintenanceRecord_Success() {
    UserCreateDto createDto = new UserCreateDto();
    createDto.setBorrowRecordId(100L);

    BorrowRecordDto borrowRecordDto = new BorrowRecordDto();
    borrowRecordDto.setUserId(10L);
    borrowRecordDto.setDeviceId(20L);

    when(borrowRecordService.getBorrowRecordById(100L)).thenReturn(borrowRecordDto);
    when(userMaintenanceRecordMapper.insert(any(UserMaintenanceRecordPo.class))).thenReturn(1);

    DeviceVo deviceVo = new DeviceVo();
    deviceVo.setDeviceName("Device A");
    when(deviceService.updateDeviceStatus(20L, DeviceStatusEnum.MAINTENANCE.getCode()))
        .thenReturn(deviceVo);

    UserMaintenanceRecordVo vo = maintenanceService.createMaintenanceRecord(10L, createDto);

    assertNotNull(vo);
    verify(deviceService).updateDeviceStatus(20L, DeviceStatusEnum.MAINTENANCE.getCode());
  }

  @Test
  void testCreateMaintenanceRecord_NotAuthorized() {
    UserCreateDto createDto = new UserCreateDto();
    createDto.setBorrowRecordId(100L);

    BorrowRecordDto borrowRecordDto = new BorrowRecordDto();
    borrowRecordDto.setUserId(99L); // 不同用户
    borrowRecordDto.setDeviceId(20L);

    when(borrowRecordService.getBorrowRecordById(100L)).thenReturn(borrowRecordDto);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> maintenanceService.createMaintenanceRecord(10L, createDto));

    assertEquals(ErrorCode.NO_AUTH_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testListUserMaintenanceRecords() {
    UserQueryDto queryDto = new UserQueryDto();
    queryDto.setPageNumber(1);
    queryDto.setPageSize(10);
    queryDto.setStatus(null);

    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setDeviceId(20L);

    Page<UserMaintenanceRecordPo> page = new Page<>();
    page.setRecords(Arrays.asList(po));
    page.setTotal(1);

    when(userMaintenanceRecordMapper.selectPage(any(), any())).thenReturn(page);
    when(deviceService.getDeviceById(anyLong())).thenReturn(new DeviceVo(){{
      setDeviceName("Device A");
    }});

    var result = maintenanceService.listUserMaintenanceRecords(10L, queryDto);

    assertNotNull(result);
    assertEquals(1, result.getRecords().size());
    assertEquals("Device A", result.getRecords().get(0).getDeviceName());
  }

  @Test
  void testCancelMaintenanceRecord_Success() {
    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setUserId(10L);
    po.setStatus(0);
    po.setDeviceId(20L);

    // ✅ mock selectOne
    when(userMaintenanceRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(po);
    // ✅ mock update
    when(userMaintenanceRecordMapper.update(any(UserMaintenanceRecordPo.class),
        any(LambdaUpdateWrapper.class))).thenReturn(1);
    // ✅ mock deviceService
    when(deviceService.updateDeviceStatus(anyLong(), anyInt())).thenReturn(new DeviceVo());
    Boolean result = maintenanceService.cancelMaintenanceRecord(10L, 1L);

    assertTrue(result);
    verify(deviceService).updateDeviceStatus(20L, 0);
  }

  @Test
  void testCancelMaintenanceRecord_NotFound() {
    when(userMaintenanceRecordMapper.selectOne(any())).thenReturn(null);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> maintenanceService.cancelMaintenanceRecord(10L, 1L));

    assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testCancelMaintenanceRecord_InvalidStatus() {
    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setUserId(10L);
    po.setStatus(1); // 非可取消状态

    when(userMaintenanceRecordMapper.selectOne(any())).thenReturn(po);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> maintenanceService.cancelMaintenanceRecord(10L, 1L));

    assertEquals(ErrorCode.OPERATION_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testListUserMaintenanceRecords_WithStatus() {
    UserQueryDto queryDto = new UserQueryDto();
    queryDto.setPageNumber(1);
    queryDto.setPageSize(10);
    queryDto.setStatus(1); // 带 status 条件

    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setDeviceId(20L);

    Page<UserMaintenanceRecordPo> page = new Page<>();
    page.setRecords(Arrays.asList(po));
    page.setTotal(1);

    when(userMaintenanceRecordMapper.selectPage(any(), any())).thenReturn(page);
    when(deviceService.getDeviceById(anyLong())).thenReturn(new DeviceVo(){{
      setDeviceName("Device A");
    }});

    var result = maintenanceService.listUserMaintenanceRecords(10L, queryDto);

    assertNotNull(result);
    assertEquals(1, result.getRecords().size());
    assertEquals("Device A", result.getRecords().get(0).getDeviceName());
  }
  @Test
  void testGetUserMaintenanceRecordDto_Success() {
    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setStatus(0);
    po.setUserId(10L);

    when(userMaintenanceRecordMapper.selectById(1L)).thenReturn(po);

    UserMaintenanceRecordDto dto = maintenanceService.getUserMaintenanceRecordDto(1L);

    assertNotNull(dto);
    assertEquals(1L, dto.getId());
    assertEquals(0, dto.getStatus());
    assertEquals(10L, dto.getUserId());
    verify(userMaintenanceRecordMapper).selectById(1L);
  }

  @Test
  void testGetUserMaintenanceRecordDto_NotFound() {
    when(userMaintenanceRecordMapper.selectById(1L)).thenReturn(null);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> maintenanceService.getUserMaintenanceRecordDto(1L));

    assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testGetUserMaintenanceRecordDetail_Success() {
    UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
    po.setId(1L);
    po.setUserId(10L);
    po.setDeviceId(20L);

    when(userMaintenanceRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(po);

    UserMaintenanceRecordVo vo = maintenanceService.getUserMaintenanceRecordDetail(10L, 1L);

    assertNotNull(vo);
    assertEquals(1L, vo.getId());
    assertEquals(10L, vo.getUserId());

    verify(userMaintenanceRecordMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  void testGetUserMaintenanceRecordDetail_NotFound() {
    when(userMaintenanceRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> maintenanceService.getUserMaintenanceRecordDetail(10L, 1L));

    assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
  }


}

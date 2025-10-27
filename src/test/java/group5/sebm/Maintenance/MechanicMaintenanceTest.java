package group5.sebm.Maintenance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.dto.MechanicRecordQueryDto;
import group5.sebm.Maintenance.dao.MechanicanMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import group5.sebm.Maintenance.service.MechanicanMaintenanceRecordServiceImpl;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import group5.sebm.common.dto.UserMaintenanceRecordDto;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Date;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MechanicMaintenanceTest {

  @Mock
  private MechanicanMaintenanceRecordMapper mechanicanMaintenanceRecordMapper;

  @Mock
  private DeviceService deviceService;

  @Mock
  private UserMaintenanceRecordService userMaintenanceRecordService;

  @InjectMocks
  private MechanicanMaintenanceRecordServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAddMaintenanceTask_Success() {
    Long mechanicId = 1L;
    Long userMaintenanceRecordId = 2L;

    UserMaintenanceRecordDto userRecord = new UserMaintenanceRecordDto();
    userRecord.setDeviceId(10L);
    userRecord.setStatus(0);
    userRecord.setDescription("desc");
    userRecord.setImage("img");

    when(userMaintenanceRecordService.getUserMaintenanceRecordDto(userMaintenanceRecordId))
        .thenReturn(userRecord);

    MechanicanMaintenanceRecordPo insertedPo = new MechanicanMaintenanceRecordPo();
    insertedPo.setId(100L);
    when(mechanicanMaintenanceRecordMapper.insert(any(MechanicanMaintenanceRecordPo.class)))
        .thenAnswer(invocation -> {
          MechanicanMaintenanceRecordPo po = invocation.getArgument(0);
          po.setId(100L);
          return 1;
        });

    Long result = service.addMaintenanceTask(mechanicId, userMaintenanceRecordId);

    assertEquals(100L, result);
    verify(mechanicanMaintenanceRecordMapper).insert(any(MechanicanMaintenanceRecordPo.class));
  }

  @Test
  void testAddMaintenanceTask_RecordAlreadyAdded() {
    UserMaintenanceRecordDto userRecord = new UserMaintenanceRecordDto();
    userRecord.setStatus(1); // 已添加

    when(userMaintenanceRecordService.getUserMaintenanceRecordDto(anyLong()))
        .thenReturn(userRecord);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.addMaintenanceTask(1L, 2L));

    assertEquals(ErrorCode.OPERATION_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testListMechanicMaintenanceRecords() {
    MechanicanQueryDto queryDto = new MechanicanQueryDto();
    queryDto.setPageNumber(1);
    queryDto.setPageSize(10);

    MechanicanMaintenanceRecordPo po = new MechanicanMaintenanceRecordPo();
    po.setId(1L);
    po.setDeviceId(20L);

    Page<MechanicanMaintenanceRecordPo> page = new Page<>();
    page.setRecords(Arrays.asList(po));
    page.setTotal(1);

    when(mechanicanMaintenanceRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
        .thenReturn(page);

    var result = service.listMechanicMaintenanceRecords(1L, queryDto);

    assertNotNull(result);
    assertEquals(1, result.getRecords().size());
  }

  @Test
  void testGetMechanicMaintenanceRecordDetail_Success() {
    MechanicRecordQueryDto queryDto = new MechanicRecordQueryDto();
    queryDto.setDeviceId(10L);
    queryDto.setStatus(1);

    MechanicanMaintenanceRecordPo po = new MechanicanMaintenanceRecordPo();
    po.setId(100L);
    po.setDeviceId(10L);
    po.setStatus(1);

    when(mechanicanMaintenanceRecordMapper.selectOne(any(QueryWrapper.class)))
        .thenReturn(po);

    MechanicanMaintenanceRecordVo vo = service.getMechanicMaintenanceRecordDetail(queryDto);

    assertNotNull(vo);
    assertEquals(100L, vo.getId());
  }

  @Test
  void testGetMechanicMaintenanceRecordDetail_NotFound() {
    when(mechanicanMaintenanceRecordMapper.selectOne(any(QueryWrapper.class)))
        .thenReturn(null);

    MechanicRecordQueryDto queryDto = new MechanicRecordQueryDto();
    queryDto.setDeviceId(10L);
    queryDto.setStatus(1);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.getMechanicMaintenanceRecordDetail(queryDto));

    assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testUpdateMechanicMaintenanceRecord_Success() {
    MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
    updateDto.setId(1L);
    updateDto.setStatus(2);
    updateDto.setDescription("new desc");
    updateDto.setImage("img");
    updateDto.setUserMaintenanceRecordId(10L);

    MechanicanMaintenanceRecordPo recordPo = new MechanicanMaintenanceRecordPo();
    recordPo.setId(1L);
    recordPo.setUserId(1L);
    recordPo.setDeviceId(20L);

    when(mechanicanMaintenanceRecordMapper.selectById(1L)).thenReturn(recordPo);
    when(deviceService.updateDeviceStatus(20L, DeviceStatusEnum.AVAILABLE.getCode()))
        .thenReturn(new DeviceVo());

    Boolean result = service.updateMechanicMaintenanceRecord(1L, updateDto);

    assertTrue(result);
    verify(userMaintenanceRecordService).updateStatus(10L, 1);
    verify(deviceService).updateDeviceStatus(20L, DeviceStatusEnum.AVAILABLE.getCode());
  }

  @Test
  void testUpdateMechanicMaintenanceRecord_NoPermission() {
    MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
    updateDto.setId(1L);

    MechanicanMaintenanceRecordPo recordPo = new MechanicanMaintenanceRecordPo();
    recordPo.setId(1L);
    recordPo.setUserId(2L);

    when(mechanicanMaintenanceRecordMapper.selectById(1L)).thenReturn(recordPo);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.updateMechanicMaintenanceRecord(1L, updateDto));

    assertEquals(ErrorCode.FORBIDDEN_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testUpdateMechanicMaintenanceRecord_StatusBroken() {
    MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
    updateDto.setId(1L);
    updateDto.setStatus(3); // 状态为 BROKEN
    updateDto.setDescription("broken desc");
    updateDto.setImage("img");
    updateDto.setUserMaintenanceRecordId(10L);

    MechanicanMaintenanceRecordPo recordPo = new MechanicanMaintenanceRecordPo();
    recordPo.setId(1L);
    recordPo.setUserId(1L);
    recordPo.setDeviceId(20L);

    when(mechanicanMaintenanceRecordMapper.selectById(1L)).thenReturn(recordPo);
    when(deviceService.updateDeviceStatus(20L, DeviceStatusEnum.BROKEN.getCode()))
        .thenReturn(new DeviceVo());

    Boolean result = service.updateMechanicMaintenanceRecord(1L, updateDto);

    assertTrue(result);
    verify(userMaintenanceRecordService).updateStatus(10L, 1);
    verify(deviceService).updateDeviceStatus(20L, DeviceStatusEnum.BROKEN.getCode());
  }

  @Test
  void testAddMaintenanceTask_NullMechanicId() {
    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.addMaintenanceTask(null, 1L));
    assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testAddMaintenanceTask_NullUserMaintenanceRecordId() {
    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.addMaintenanceTask(1L, null));
    assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testAddMaintenanceTask_UserRecordNotFound() {
    when(userMaintenanceRecordService.getUserMaintenanceRecordDto(anyLong()))
        .thenReturn(null);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.addMaintenanceTask(1L, 2L));

    assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
  }

}
